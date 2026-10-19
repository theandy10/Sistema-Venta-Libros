package venta.util;

public final class Validador {

    private Validador() {

    }

    public static boolean vacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public static String limpio(String texto) {
        return texto == null ? "" : texto.trim();
    }

    public static boolean dni(String texto) {
        return !vacio(texto) && texto.trim().matches("\\d{8}");
    }

    public static boolean celular(String texto) {
        return !vacio(texto) && texto.trim().matches("\\d{9}");
    }

    public static boolean esEntero(String texto) {
        if (vacio(texto)) {
            return false;
        }
        try {
            Integer.parseInt(texto.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean enteroPositivo(String texto) {
        return esEntero(texto) && entero(texto, 0) >= 1;
    }

    public static int entero(String texto, int porDefecto) {
        if (vacio(texto)) {
            return porDefecto;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    public static boolean largoMinimo(String texto, int largo) {
        return !vacio(texto) && texto.trim().length() >= largo;
    }
}
