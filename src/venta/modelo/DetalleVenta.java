package venta.modelo;

import java.math.BigDecimal;
import venta.util.Dinero;

public class DetalleVenta {

    private int numero;
    private int codigoLibro;
    private String nombreLibro;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public DetalleVenta(int numero, int codigoLibro, String nombreLibro,
                        int cantidad, BigDecimal precioUnitario) {
        this.numero = numero;
        this.codigoLibro = codigoLibro;
        this.nombreLibro = nombreLibro;
        this.cantidad = cantidad;
        this.precioUnitario = Dinero.escala(precioUnitario);
        this.subtotal = Dinero.multiplica(this.precioUnitario, cantidad);
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getCodigoLibro() {
        return codigoLibro;
    }

    public void setCodigoLibro(int codigoLibro) {
        this.codigoLibro = codigoLibro;
    }

    public String getNombreLibro() {
        return nombreLibro;
    }

    public void setNombreLibro(String nombreLibro) {
        this.nombreLibro = nombreLibro;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = Dinero.multiplica(this.precioUnitario, cantidad);
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = Dinero.escala(precioUnitario);
        this.subtotal = Dinero.multiplica(this.precioUnitario, this.cantidad);
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    @Override
    public String toString() {
        return nombreLibro + " x" + cantidad;
    }
}
