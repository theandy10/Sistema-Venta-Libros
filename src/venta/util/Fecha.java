package venta.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Fecha {

    public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter FECHA_LARGA =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es"));
    public static final DateTimeFormatter HORA_CORTA = DateTimeFormatter.ofPattern("h:mm a", Locale.forLanguageTag("es"));

    private Fecha() {

    }

    public static String hoy() {
        return LocalDate.now().format(FECHA);
    }

    public static String ahora() {
        return LocalTime.now().format(HORA);
    }

    public static String hoyLargo() {
        String s = LocalDate.now().format(FECHA_LARGA);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static LocalDate aFecha(String ddmmyyyy) {
        if (Validador.vacio(ddmmyyyy)) {
            return null;
        }
        try {
            return LocalDate.parse(ddmmyyyy.trim(), FECHA);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean esFecha(String ddmmyyyy) {
        return aFecha(ddmmyyyy) != null;
    }

    public static boolean esHoy(String ddmmyyyy) {
        LocalDate f = aFecha(ddmmyyyy);
        return f != null && f.equals(LocalDate.now());
    }
}
