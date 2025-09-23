package apps.sarafrika.service;

import apps.sarafrika.entity.SmsLog;
import apps.sarafrika.enums.SmsDeliveryStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SmsTrackingService {

    @Transactional
    public void logSms(SmsLog smsLog) {
        smsLog.persist();
    }

    @Transactional
    public void updateSmsStatus(String messageId, String status, String providerResponse) {
        SmsLog.find("messageId", messageId).firstResultOptional()
                .ifPresent(smsLog -> {
                    SmsDeliveryStatus newStatus = switch (status.toUpperCase()) {
                        case "DELIVERED" -> SmsDeliveryStatus.DELIVERED;
                        case "FAILED" -> SmsDeliveryStatus.FAILED;
                        default -> null;
                    };

                    if (newStatus != null) {
                        ((SmsLog) smsLog).setDeliveryStatus(newStatus);
                    }
                    ((SmsLog) smsLog).setProviderResponse(providerResponse);
                    smsLog.persist();
                });
    }
}
