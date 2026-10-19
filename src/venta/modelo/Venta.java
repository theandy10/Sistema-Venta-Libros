package venta.modelo;

import java.math.BigDecimal;
import java.util.Locale;
import venta.util.Dinero;

public class Venta {

    public static final String PUBLICO = "PUBLICO";
    public static final String EFECTIVO = "EFECTIVO";

    private int numero;
    private String fecha;
    private String hora;
    private String usuario;
    private String dni;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal total;
    private String metodo;
    private BigDecimal montoPagado;
    private BigDecimal vuelto;

    public Venta(int numero, String fecha, String hora, String usuario, String dni,
                 BigDecimal subtotal, BigDecimal descuento, String metodo, BigDecimal montoPagado) {
        this.numero = numero;
        this.fecha = fecha;
        this.hora = hora;
        this.usuario = usuario;
        this.dni = dni;
        this.subtotal = Dinero.escala(subtotal);
        this.descuento = Dinero.escala(descuento);
        this.total = Dinero.resta(this.subtotal, this.descuento);
        this.metodo = metodo;
        this.montoPagado = Dinero.escala(montoPagado);
        recalcularPago();
    }

    private void recalcularPago() {
        if (EFECTIVO.equals(metodo)) {
            this.vuelto = Dinero.resta(this.montoPagado, this.total);
        } else {
            this.montoPagado = this.total;
            this.vuelto = Dinero.CERO.setScale(2);
        }
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
        recalcularPago();
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public BigDecimal getVuelto() {
        return vuelto;
    }

    public boolean esEfectivo() {
        return EFECTIVO.equals(metodo);
    }

    public boolean clienteRegistrado() {
        return dni != null && !PUBLICO.equals(dni);
    }

    public String numeroTexto() {
        return String.format(Locale.US, "%06d", numero);
    }

    @Override
    public String toString() {
        return "Venta N." + numeroTexto();
    }
}
