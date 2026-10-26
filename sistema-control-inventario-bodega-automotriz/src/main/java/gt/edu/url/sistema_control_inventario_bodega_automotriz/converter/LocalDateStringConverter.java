package gt.edu.url.sistema_control_inventario_bodega_automotriz.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class LocalDateStringConverter implements AttributeConverter<LocalDate, String> {

    private static final DateTimeFormatter LEGACY_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    @Override
    public String convertToDatabaseColumn(LocalDate date) {
        return date == null ? null : date.toString();
    }

    @Override
    public LocalDate convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException isoDateError) {
            try {
                return LocalDate.parse(value, LEGACY_FORMAT);
            } catch (DateTimeParseException legacyDateError) {
                legacyDateError.addSuppressed(isoDateError);
                throw new IllegalArgumentException("La fecha almacenada no tiene un formato reconocido.", legacyDateError);
            }
        }
    }
}
