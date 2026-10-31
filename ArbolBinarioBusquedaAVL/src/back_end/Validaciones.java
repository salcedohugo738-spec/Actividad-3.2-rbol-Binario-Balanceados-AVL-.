/*
Salcedo Alvarez Hugo Emmanuel
Morales Escobar Juan Adrian
 */
package back_end;

/**
 * Validaciones reutilizables tanto por la interfaz gráfica como por la
 * estructura de datos. Todos los métodos lanzan IllegalArgumentException con
 * un mensaje comprensible para el usuario cuando el valor no es válido.
 *
 * @author ASUS
 */
public final class Validaciones {

    private Validaciones() {
    }

    /** Verifica que un texto obligatorio no sea nulo ni esté vacío (o solo espacios). */
    public static String textoRequerido(String campo, String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio y no puede estar vacío.");
        }
        return valor.trim();
    }

    /** Convierte un texto en un identificador entero válido. */
    public static Integer parseId(String texto) {
        String t = textoRequerido("ID", texto);
        try {
            return Integer.valueOf(t);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "El ID debe ser un número entero válido (sin letras, decimales ni espacios). Valor recibido: \"" + t + "\".");
        }
    }

    /** Convierte un texto en un salario numérico, finito y no negativo. */
    public static Double parseSalario(String texto) {
        String t = textoRequerido("Salario", texto);
        // Solo dígitos con un punto decimal opcional; rechaza "NaN", "Infinity", "1e5", "1d", etc.
        if (!t.matches("[-+]?(\\d+\\.?\\d*|\\.\\d+)")) {
            throw new IllegalArgumentException(
                    "El salario debe ser un número válido (ejemplo: 12500.50). Valor recibido: \"" + t + "\".");
        }
        double valor = Double.parseDouble(t);
        validarSalario(valor);
        return valor;
    }

    /** Verifica que el salario sea finito y no negativo. */
    public static void validarSalario(Double salario) {
        if (salario == null) {
            throw new IllegalArgumentException("El salario es obligatorio.");
        }
        if (salario.isNaN() || salario.isInfinite()) {
            throw new IllegalArgumentException("El salario debe ser un número finito.");
        }
        if (salario < 0) {
            throw new IllegalArgumentException("El salario no puede ser negativo.");
        }
    }
}
