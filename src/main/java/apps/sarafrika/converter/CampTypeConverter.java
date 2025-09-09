package apps.sarafrika.converter;

import apps.sarafrika.enums.CampType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CampTypeConverter implements AttributeConverter<CampType, String> {

    @Override
    public String convertToDatabaseColumn(CampType attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public CampType convertToEntityAttribute(String dbData) {
        return dbData != null ? CampType.valueOf(dbData) : null;
    }
}