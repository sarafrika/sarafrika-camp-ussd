package apps.sarafrika.converter;

import apps.sarafrika.enums.SessionEventType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SessionEventTypeConverter implements AttributeConverter<SessionEventType, String> {

    @Override
    public String convertToDatabaseColumn(SessionEventType sessionEventType) {
        if (sessionEventType == null) {
            return null;
        }
        return sessionEventType.getValue();
    }

    @Override
    public SessionEventType convertToEntityAttribute(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return SessionEventType.fromValue(value);
    }
}