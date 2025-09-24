package apps.sarafrika.service;

import apps.sarafrika.dto.CelcomSmsRequest;
import apps.sarafrika.dto.CelcomSmsResponse;
import apps.sarafrika.entity.SmsLog;
import apps.sarafrika.enums.SmsDeliveryStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class SmsNotificationService {

    private static final Logger LOG = Logger.getLogger(SmsNotificationService.class);

    @Inject
    @RestClient
    CelcomAfricaClient celcomAfricaClient;

    @Inject
    SmsTrackingService smsTrackingService;

    @ConfigProperty(name = "celcom-africa.api-key")
    String apiKey;

    @ConfigProperty(name = "celcom-africa.partner-id")
    String partnerId;

    @ConfigProperty(name = "celcom-africa.shortcode")
    String shortcode;

    @ConfigProperty(name = "app.organizer-contact")
    String organizerContact;

    public boolean sendSms(String phoneNumber, String message) {
        String cleanedPhoneNumber = normalizePhoneNumber(phoneNumber);
        if (cleanedPhoneNumber == null) {
            LOG.errorf("Invalid phone number format: %s", maskPhoneNumber(phoneNumber));
            return false;
        }

        SmsLog smsLog = new SmsLog(cleanedPhoneNumber, message, null, SmsDeliveryStatus.PENDING, null);
        smsTrackingService.logSms(smsLog);

        try {
            CelcomSmsRequest request = new CelcomSmsRequest(partnerId, apiKey, cleanedPhoneNumber, message, shortcode);
            CelcomSmsResponse response = celcomAfricaClient.sendSms(request);

            smsLog.setMessageId(response.messageId());
            smsLog.setProviderResponse(response.responseDescription());

            if (response.responseCode() == 200) {
                smsLog.setDeliveryStatus(SmsDeliveryStatus.SENT);
                LOG.infof("SMS sent successfully to %s. MessageId: %s",
                        maskPhoneNumber(cleanedPhoneNumber), response.messageId());
                smsTrackingService.logSms(smsLog);
                return true;
            } else {
                smsLog.setDeliveryStatus(SmsDeliveryStatus.FAILED);
                if (response.responseCode() == 0) {
                    LOG.errorf("Failed to send SMS to %s. Received response code 0. This might indicate an authentication issue or invalid credentials with the SMS provider.",
                        maskPhoneNumber(cleanedPhoneNumber));
                } else {
                    LOG.errorf("Failed to send SMS to %s. Status: %s, Description: %s",
                        maskPhoneNumber(cleanedPhoneNumber), response.responseCode(), response.responseDescription());
                }
                smsTrackingService.logSms(smsLog);
                return false;
            }

        } catch (Exception e) {
            smsLog.setDeliveryStatus(SmsDeliveryStatus.FAILED);
            smsLog.setProviderResponse(e.getMessage());
            smsTrackingService.logSms(smsLog);
            LOG.errorf(e, "Error sending SMS to %s", maskPhoneNumber(phoneNumber));
            return false;
        }
    }

    public boolean sendRegistrationConfirmation(String phoneNumber, String participantName,
                                               String campName, String referenceCode) {
        String message = String.format(
            "Registration confirmed for %s at %s. Reference: %s. " +
            "Payment instructions will follow. Camp Sarafrika.",
            participantName, campName, referenceCode
        );
        return sendSms(phoneNumber, message);
    }

    public boolean sendGuardianNotification(String guardianPhone, String participantName,
                                          String campName, String referenceCode) {
        String message = String.format(
            "Your child %s has been registered for %s. Reference: %s. " +
            "Payment details will be sent shortly. Camp Sarafrika.",
            participantName, campName, referenceCode
        );
        return sendSms(guardianPhone, message);
    }

    public boolean sendPaymentReminder(String phoneNumber, String participantName,
                                     String campName, String referenceCode, double amount) {
        String message = String.format(
            "Payment reminder: Complete payment of KSH %.0f for %s's registration at %s. " +
            "Reference: %s. Camp Sarafrika.",
            amount, participantName, campName, referenceCode
        );
        return sendSms(phoneNumber, message);
    }

    public boolean sendPaymentConfirmation(String phoneNumber, String participantName,
                                         String activityName, String locationName, String locationDates,
                                         String referenceCode, double amountPaid) {
        String message = String.format(
            "Hi %s,\nYou are registered for %s (%s, %s).\nRef: %s. Fee paid: KShs %.0f.\nOrganizer Contact: %s",
            participantName, activityName, locationName, locationDates, referenceCode, amountPaid, organizerContact
        );
        return sendSms(phoneNumber, message);
    }

    public boolean sendBulkNotification(List<String> phoneNumbers, String message) {
        AtomicInteger successCount = new AtomicInteger(0);
        phoneNumbers.stream()
                .map(this::normalizePhoneNumber)
                .filter(num -> num != null)
                .forEach(cleanedNumber -> {
                    if (sendSms(cleanedNumber, message)) {
                        successCount.getAndIncrement();
                    }
                });

        LOG.infof("Bulk SMS sent to %d/%d recipients successfully", successCount.get(), phoneNumbers.size());
        return successCount.get() > 0;
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;

        String cleaned = phoneNumber.replaceAll("\\s+", "");

        if (cleaned.startsWith("0")) {
            cleaned = "254" + cleaned.substring(1);
        } else if (cleaned.startsWith("+")) {
            cleaned = cleaned.substring(1);
        }

        if (cleaned.matches("^254[17][0-9]{8}$")) {
            return cleaned;
        }

        return null;
    }

    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 8) {
            return "***";
        }
        return phoneNumber.substring(0, 4) + "***" + phoneNumber.substring(phoneNumber.length() - 3);
    }
}