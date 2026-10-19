package venta.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public final class Dinero {

    public static final BigDecimal CERO = new BigDecimal("0.00");

    private static final int ESCALA = 2;

    private Dinero() {

    }

    public static BigDecimal de(double valor) {
        return new BigDecimal(String.format(Locale.US, "%.2f", valor));
    }

    public static BigDecimal de(String texto) {
        if (texto == null) {
            return CERO;
        }
        String t = texto.trim().replace(',', '.');
        if (t.isEmpty()) {
            return CERO;
        }
        try {
            return new BigDecimal(t).setScale(ESCALA, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static BigDecimal escala(BigDecimal valor) {
        if (valor == null) {
            return CERO.setScale(ESCALA);
        }
        return valor.setScale(ESCALA, RoundingMode.HALF_UP);
    }

    public static BigDecimal suma(BigDecimal a, BigDecimal b) {
        return escala(escala(a).add(escala(b)));
    }

    public static BigDecimal resta(BigDecimal a, BigDecimal b) {
        return escala(escala(a).subtract(escala(b)));
    }

    public static BigDecimal multiplica(BigDecimal a, int veces) {
        return escala(escala(a).multiply(new BigDecimal(veces)));
    }

    public static boolean mayor(BigDecimal a, BigDecimal b) {
        return escala(a).compareTo(escala(b)) > 0;
    }

    public static boolean mayorOIgual(BigDecimal a, BigDecimal b) {
        return escala(a).compareTo(escala(b)) >= 0;
    }

    public static boolean positivo(BigDecimal valor) {
        return escala(valor).compareTo(CERO) > 0;
    }

    public static String texto(BigDecimal valor) {
        return String.format(Locale.US, "%.2f", escala(valor));
    }

    public static String soles(BigDecimal valor) {
        return "S/ " + texto(valor);
    }
}
