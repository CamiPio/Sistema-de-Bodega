package gt.edu.url.sistema_control_inventario_bodega_automotriz.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class NullableIntegerStringConverter implements AttributeConverter<Integer, String> {

    @Override
    public String convertToDatabaseColumn(Integer value) {
        return value == null ? null : value.toString();
    }

    @Override
    public Integer convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La disponibilidad almacenada debe ser un número entero.", e);
        }
    }
}
