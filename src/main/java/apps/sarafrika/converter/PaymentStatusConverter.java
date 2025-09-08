package apps.sarafrika.converter;

import apps.sarafrika.enums.PaymentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentStatusConverter implements AttributeConverter<PaymentStatus, String> {

    @Override
    public String convertToDatabaseColumn(PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            return null;
        }
        return paymentStatus.getValue();
    }

    @Override
    public PaymentStatus convertToEntityAttribute(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return PaymentStatus.fromValue(value);
    }
}