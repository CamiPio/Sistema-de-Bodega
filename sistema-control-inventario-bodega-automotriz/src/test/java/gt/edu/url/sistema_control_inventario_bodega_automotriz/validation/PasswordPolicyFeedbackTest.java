package gt.edu.url.sistema_control_inventario_bodega_automotriz.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PasswordPolicyFeedbackTest {

    @Test
    void describeIndicaLasReglasQueFaltan() {
        assertEquals("Falta: una mayúscula; un carácter especial.",
                PasswordPolicyFeedback.describe("abc123"));
    }

    @Test
    void describeIdentificaLosCaracteresProhibidos() {
        assertEquals("Falta: quitar el carácter no permitido «-».",
                PasswordPolicyFeedback.describe("Abc123-!"));
    }
}
