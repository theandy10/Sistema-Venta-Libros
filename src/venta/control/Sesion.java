package venta.control;

import venta.modelo.Usuario;

public final class Sesion {

    public static final String ADMINISTRADOR = "ADMINISTRADOR";
    public static final String VENDEDOR = "VENDEDOR";

    private static Usuario usuario;

    private Sesion() {

    }

    public static void iniciar(Usuario u) {
        usuario = u;
    }

    public static void cerrar() {
        usuario = null;
    }

    public static Usuario usuario() {
        return usuario;
    }

    public static String codigo() {
        return usuario == null ? "admin" : usuario.getUsuario();
    }

    public static String nombreCompleto() {
        return usuario == null ? "Usuario" : usuario.getNombreCompleto();
    }

    public static String perfil() {
        return usuario == null ? ADMINISTRADOR : usuario.getPerfil();
    }

    public static boolean esAdmin() {
        return usuario == null || ADMINISTRADOR.equals(usuario.getPerfil());
    }

    public static boolean esVendedor() {
        return !esAdmin();
    }

    public static boolean activa() {
        return usuario != null;
    }
}
