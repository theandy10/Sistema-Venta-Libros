package venta.modelo;

public class Cliente {

    private int codigo;
    private String dni;
    private String nombres;
    private String apellidos;
    private int celular;

    public Cliente(int codigo, String dni, String nombres, String apellidos, int celular) {
        this.codigo = codigo;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.celular = celular;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getCelular() {
        return celular;
    }

    public void setCelular(int celular) {
        this.celular = celular;
    }

    public String nombreCompleto() {
        return nombres + " " + apellidos;
    }

    @Override
    public String toString() {
        return dni + " - " + nombreCompleto();
    }
}
