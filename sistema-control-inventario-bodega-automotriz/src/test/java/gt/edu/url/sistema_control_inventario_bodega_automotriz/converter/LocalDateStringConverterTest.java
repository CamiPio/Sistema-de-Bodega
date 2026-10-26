package gt.edu.url.sistema_control_inventario_bodega_automotriz.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class LocalDateStringConverterTest {

    private final LocalDateStringConverter converter = new LocalDateStringConverter();

    @Test
    void convierteFechaIsoYFormatoAnteriorSinPerderDatos() {
        LocalDate fecha = LocalDate.of(2026, 10, 19);

        assertEquals(fecha, converter.convertToEntityAttribute("2026-10-19"));
        assertEquals(fecha, converter.convertToEntityAttribute("19/10/2026"));
        assertEquals("2026-10-19", converter.convertToDatabaseColumn(fecha));
    }
}
