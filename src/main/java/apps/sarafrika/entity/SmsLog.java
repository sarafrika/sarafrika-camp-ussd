package apps.sarafrika.entity;

import apps.sarafrika.enums.SmsDeliveryStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sms_logs")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsLog extends BaseEntity {

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "message")
    private String message;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "delivery_status")
    private SmsDeliveryStatus deliveryStatus;

    @Column(name = "provider_response")
    private String providerResponse;
}
