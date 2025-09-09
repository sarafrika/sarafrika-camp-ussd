package apps.sarafrika.service;

import com.africastalking.AfricasTalking;
import com.africastalking.SmsService;
import com.africastalking.sms.Recipient;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class SmsNotificationService {

    private static final Logger LOG = Logger.getLogger(SmsNotificationService.class);

    @ConfigProperty(name = "africas-talking.username")
    String username;

    @ConfigProperty(name = "africas-talking.api-key")
    String apiKey;

    @ConfigProperty(name = "africas-talking.environment", defaultValue = "sandbox")
    String environment;

    private SmsService smsService;

    @PostConstruct
    public void init() {
        try {
            AfricasTalking.initialize(username, apiKey);
            smsService = AfricasTalking.getService(AfricasTalking.SERVICE_SMS);
            LOG.infof("SMS service initialized for environment: %s", environment);
        } catch (Exception e) {
            LOG.errorf(e, "Failed to initialize SMS service");
        }
    }

    public boolean sendSms(String phoneNumber, String message) {
        try {
            String cleanedPhoneNumber = normalizePhoneNumber(phoneNumber);
            if (cleanedPhoneNumber == null) {
                LOG.errorf("Invalid phone number format: %s", maskPhoneNumber(phoneNumber));
                return false;
            }

            List<Recipient> recipients = smsService.send(message, new String[]{cleanedPhoneNumber}, true);
            
            for (Recipient recipient : recipients) {
                if ("Success".equalsIgnoreCase(recipient.status)) {
                    LOG.infof("SMS sent successfully to %s. MessageId: %s", 
                             maskPhoneNumber(cleanedPhoneNumber), recipient.messageId);
                    return true;
                } else {
                    LOG.errorf("Failed to send SMS to %s. Status: %s, Cost: %s", 
                              maskPhoneNumber(cleanedPhoneNumber), recipient.status, recipient.cost);
                }
            }
            return false;
            
        } catch (Exception e) {
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

    public boolean sendBulkNotification(List<String> phoneNumbers, String message) {
        try {
            String[] cleanedNumbers = phoneNumbers.stream()
                .map(this::normalizePhoneNumber)
                .filter(num -> num != null)
                .toArray(String[]::new);

            if (cleanedNumbers.length == 0) {
                LOG.warn("No valid phone numbers provided for bulk SMS");
                return false;
            }

            List<Recipient> recipients = smsService.send(message, cleanedNumbers, true);
            
            int successCount = 0;
            for (Recipient recipient : recipients) {
                if ("Success".equalsIgnoreCase(recipient.status)) {
                    successCount++;
                } else {
                    LOG.errorf("Failed to send bulk SMS to %s. Status: %s", 
                              maskPhoneNumber(recipient.number), recipient.status);
                }
            }
            
            LOG.infof("Bulk SMS sent to %d/%d recipients successfully", successCount, recipients.size());
            return successCount > 0;
            
        } catch (Exception e) {
            LOG.errorf(e, "Error sending bulk SMS to %d recipients", phoneNumbers.size());
            return false;
        }
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;
        
        String cleaned = phoneNumber.replaceAll("\\s+", "");
        
        if (cleaned.startsWith("0")) {
            cleaned = "+254" + cleaned.substring(1);
        } else if (cleaned.startsWith("254")) {
            cleaned = "+" + cleaned;
        } else if (!cleaned.startsWith("+254")) {
            return null;
        }
        
        if (cleaned.matches("^\\+254[17][0-9]{8}$")) {
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