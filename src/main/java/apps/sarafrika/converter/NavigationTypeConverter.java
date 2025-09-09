package apps.sarafrika.converter;

import apps.sarafrika.enums.NavigationType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NavigationTypeConverter implements AttributeConverter<NavigationType, String> {

    @Override
    public String convertToDatabaseColumn(NavigationType navigationType) {
        if (navigationType == null) {
            return null;
        }
        return navigationType.getValue();
    }

    @Override
    public NavigationType convertToEntityAttribute(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return NavigationType.fromValue(value);
    }
}