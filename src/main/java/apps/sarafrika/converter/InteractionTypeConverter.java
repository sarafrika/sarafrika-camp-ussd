package apps.sarafrika.converter;

import apps.sarafrika.enums.InteractionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class InteractionTypeConverter implements AttributeConverter<InteractionType, String> {

    @Override
    public String convertToDatabaseColumn(InteractionType interactionType) {
        if (interactionType == null) {
            return null;
        }
        return interactionType.getValue();
    }

    @Override
    public InteractionType convertToEntityAttribute(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return InteractionType.fromValue(value);
    }
}