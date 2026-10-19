package venta.datos;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import venta.modelo.DetalleVenta;
import venta.util.Archivo;
import venta.util.Dinero;

public class ArregloDetalle {

    private ArrayList<DetalleVenta> det;
    private final String archivo;

    public ArregloDetalle(String carpeta) {
        this.archivo = carpeta + File.separator + "detalle_ventas.txt";
        this.det = new ArrayList<DetalleVenta>();
        cargar();
    }

    public void adicionar(DetalleVenta x) {
        det.add(x);
    }

    public DetalleVenta eliminar(DetalleVenta x) {
        int pos = det.indexOf(x);
        return pos >= 0 ? det.remove(pos) : null;
    }

    public int tamaño() {
        return det.size();
    }

    public DetalleVenta obtener(int pos) {
        return det.get(pos);
    }

    public ArrayList<DetalleVenta> detalleDeVenta(int numero) {
        ArrayList<DetalleVenta> r = new ArrayList<DetalleVenta>();
        for (int i = 0; i < det.size(); i++) {
            if (det.get(i).getNumero() == numero) {
                r.add(det.get(i));
            }
        }
        return r;
    }

    public int eliminarDeVenta(int numero) {
        int borradas = 0;
        for (int i = det.size() - 1; i >= 0; i--) {
            if (det.get(i).getNumero() == numero) {
                det.remove(i);
                borradas++;
            }
        }
        return borradas;
    }

    public BigDecimal totalDeVenta(int numero) {
        BigDecimal t = Dinero.CERO;
        ArrayList<DetalleVenta> d = detalleDeVenta(numero);
        for (int i = 0; i < d.size(); i++) {
            t = Dinero.suma(t, d.get(i).getSubtotal());
        }
        return t;
    }

    public BigDecimal sumaSubtotales(ArrayList<DetalleVenta> lista) {
        BigDecimal t = Dinero.CERO;
        for (int i = 0; i < lista.size(); i++) {
            t = Dinero.suma(t, lista.get(i).getSubtotal());
        }
        return t;
    }

    public int unidadesVendidas(int codigoLibro) {
        int suma = 0;
        for (int i = 0; i < det.size(); i++) {
            if (det.get(i).getCodigoLibro() == codigoLibro) {
                suma += det.get(i).getCantidad();
            }
        }
        return suma;
    }

    public boolean tieneVentas(int codigoLibro) {
        for (int i = 0; i < det.size(); i++) {
            if (det.get(i).getCodigoLibro() == codigoLibro) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Integer> codigosVendidos() {
        ArrayList<Integer> r = new ArrayList<Integer>();
        for (int i = 0; i < det.size(); i++) {
            Integer c = det.get(i).getCodigoLibro();
            if (!r.contains(c)) {
                r.add(c);
            }
        }
        return r;
    }

    private void cargar() {
        List<String> lineas = Archivo.leer(archivo);
        for (int i = 0; i < lineas.size(); i++) {
            try {
                String[] s = Archivo.partir(lineas.get(i));
                if (s.length < 5) {
                    continue;
                }
                int numero = Integer.parseInt(s[0]);
                int codLibro = Integer.parseInt(s[1]);
                String nombre = s[2];
                int cantidad = Integer.parseInt(s[3]);
                BigDecimal precio = Dinero.de(s[4]);
                if (precio == null) {
                    continue;
                }
                DetalleVenta d = new DetalleVenta(numero, codLibro, nombre, cantidad, precio);
                det.add(d);
            } catch (NumberFormatException e) {

            }
        }
    }

    public boolean grabar() {
        List<String> lineas = new ArrayList<String>();
        for (int i = 0; i < det.size(); i++) {
            DetalleVenta d = det.get(i);
            lineas.add(Archivo.unir(
                    String.format(Locale.US, "%06d", d.getNumero()),
                    String.valueOf(d.getCodigoLibro()),
                    d.getNombreLibro(),
                    String.valueOf(d.getCantidad()),
                    Dinero.texto(d.getPrecioUnitario()),
                    Dinero.texto(d.getSubtotal())));
        }
        return Archivo.escribir(archivo, lineas);
    }

    public String getArchivo() {
        return archivo;
    }
}
