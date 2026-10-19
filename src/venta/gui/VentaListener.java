package venta.gui;

import java.math.BigDecimal;
import venta.modelo.Libro;

public interface VentaListener {

    void agregarItem(Libro libro, int cantidad, BigDecimal precioAplicado);

    int cantidadEnDetalle(int codigoLibro);

    String nombreCliente();
}
