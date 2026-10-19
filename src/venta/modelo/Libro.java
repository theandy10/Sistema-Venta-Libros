package venta.modelo;

import java.math.BigDecimal;
import venta.util.Dinero;

public class Libro {

    public static final int UMBRAL_STOCK_BAJO = 5;

    private int codigo;
    private String nombre;
    private String autor;
    private String genero;
    private int stock;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal precioMinimo;

    public Libro(int codigo, String nombre, String autor, String genero, int stock,
                 BigDecimal precioCompra, BigDecimal precioVenta, BigDecimal precioMinimo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.autor = autor;
        this.genero = genero;
        this.stock = stock;
        this.precioCompra = Dinero.escala(precioCompra);
        this.precioVenta = Dinero.escala(precioVenta);
        this.precioMinimo = Dinero.escala(precioMinimo);
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = Dinero.escala(precioCompra);
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = Dinero.escala(precioVenta);
    }

    public BigDecimal getPrecioMinimo() {
        return precioMinimo;
    }

    public void setPrecioMinimo(BigDecimal precioMinimo) {
        this.precioMinimo = Dinero.escala(precioMinimo);
    }

    public boolean disponible() {
        return stock > 0;
    }

    public boolean stockBajo(int umbral) {
        return stock <= umbral;
    }

    public BigDecimal valorInventario() {
        return Dinero.multiplica(precioCompra, stock);
    }

    public BigDecimal utilidad() {
        return Dinero.multiplica(Dinero.resta(precioVenta, precioCompra), stock);
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
