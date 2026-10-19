package venta.modelo;

import venta.control.Sesion;

public class Usuario {

    private String usuario;
    private String clave;
    private String nombre;
    private String perfil;

    public Usuario(String usuario, String clave, String nombre, String perfil) {
        this.usuario = usuario;
        this.clave = clave;
        this.nombre = nombre;
        this.perfil = perfil;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getNombreCompleto() {
        return nombre;
    }

    public boolean esAdmin() {
        return Sesion.ADMINISTRADOR.equals(perfil);
    }

    @Override
    public String toString() {
        return nombre + " (" + perfil + ")";
    }
}
