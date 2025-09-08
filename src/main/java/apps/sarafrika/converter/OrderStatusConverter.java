package apps.sarafrika.converter;

import apps.sarafrika.enums.OrderStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {

    @Override
    public String convertToDatabaseColumn(OrderStatus orderStatus) {
        if (orderStatus == null) {
            return null;
        }
        return orderStatus.getValue();
    }

    @Override
    public OrderStatus convertToEntityAttribute(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return OrderStatus.fromValue(value);
    }
}