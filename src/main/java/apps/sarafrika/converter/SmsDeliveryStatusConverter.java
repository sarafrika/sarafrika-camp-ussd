package apps.sarafrika.converter;

import apps.sarafrika.enums.SmsDeliveryStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SmsDeliveryStatusConverter implements AttributeConverter<SmsDeliveryStatus, String> {

    @Override
    public String convertToDatabaseColumn(SmsDeliveryStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public SmsDeliveryStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return SmsDeliveryStatus.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle unknown status gracefully, e.g., return a default or log a warning
            return null;
        }
    }
}
