package gt.edu.url.sistema_control_inventario_bodega_automotriz.validation;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public final class PasswordPolicyFeedback {

    private static final Pattern SPECIAL_CHARACTER = Pattern.compile("[^a-zA-Z0-9\\s()\\-´”`’]");
    private static final String FORBIDDEN_CHARACTERS = "()-´”`’";

    private PasswordPolicyFeedback() {
    }

    public static String describe(String password) {
        if (password == null || password.isEmpty()) {
            return "Falta: ingresar la contraseña.";
        }

        List<String> issues = new ArrayList<>();
        if (password.length() < 6) {
            issues.add("llegar a 6 caracteres (faltan " + (6 - password.length()) + ")");
        } else if (password.length() > 72) {
            issues.add("no exceder 72 caracteres");
        }
        if (!password.matches("(?s).*[a-z].*")) {
            issues.add("una minúscula");
        }
        if (!password.matches("(?s).*[A-Z].*")) {
            issues.add("una mayúscula");
        }
        if (!password.matches("(?s).*[0-9].*")) {
            issues.add("un número");
        }
        if (!SPECIAL_CHARACTER.matcher(password).find()) {
            issues.add("un carácter especial");
        }

        Set<Character> forbidden = new LinkedHashSet<>();
        for (char character : password.toCharArray()) {
            if (FORBIDDEN_CHARACTERS.indexOf(character) >= 0) {
                forbidden.add(character);
            }
        }
        if (!forbidden.isEmpty()) {
            issues.add("quitar el carácter" + (forbidden.size() > 1 ? "es" : "")
                    + " no permitido" + (forbidden.size() > 1 ? "s" : "") + " "
                    + forbidden.stream().map(character -> "«" + character + "»")
                            .reduce((first, second) -> first + ", " + second).orElse(""));
        }

        return "Falta: " + String.join("; ", issues) + ".";
    }
}
