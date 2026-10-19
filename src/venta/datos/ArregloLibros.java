package venta.datos;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import venta.modelo.Libro;
import venta.util.Archivo;
import venta.util.Dinero;
import venta.util.Validador;

public class ArregloLibros {

    public static final int CODIGO_INICIAL = 30001;

    public static final int DIGITOS_CODIGO = 5;

    private ArrayList<Libro> lib;
    private final String archivo;
    private final ArregloDetalle arrDet;

    public ArregloLibros(String carpeta, ArregloDetalle arrDet) {
        this.archivo = carpeta + File.separator + "libros.txt";
        this.arrDet = arrDet;
        this.lib = new ArrayList<Libro>();
        cargar();
        ordenarPorCodigo();
    }

    public void adicionar(Libro x) {
        lib.add(x);
        ordenarPorCodigo();
    }

    public Libro eliminar(Libro x) {
        int pos = lib.indexOf(x);
        return pos >= 0 ? lib.remove(pos) : null;
    }

    public int tamaño() {
        return lib.size();
    }

    public Libro obtener(int pos) {
        return lib.get(pos);
    }

    public Libro buscar(int codigo) {

        for (int i = 0; i < lib.size(); i++) {
            Libro x = lib.get(i);
            if (x.getCodigo() == codigo) {
                return x;
            }
            if (x.getCodigo() > codigo) {
                return null;
            }
        }
        return null;
    }

    public boolean existe(int codigo) {
        return buscar(codigo) != null;
    }

    public Libro buscarPorNombre(String nombre) {
        String n = Validador.limpio(nombre).toLowerCase();
        for (int i = 0; i < lib.size(); i++) {
            if (lib.get(i).getNombre().toLowerCase().equals(n)) {
                return lib.get(i);
            }
        }
        return null;
    }

    public ArrayList<Libro> buscarPorTexto(String texto) {
        String t = Validador.limpio(texto).toLowerCase();
        ArrayList<Libro> r = new ArrayList<Libro>();
        if (t.isEmpty()) {
            return todos();
        }
        for (int i = 0; i < lib.size(); i++) {
            Libro x = lib.get(i);
            if (x.getNombre().toLowerCase().contains(t)
                    || x.getAutor().toLowerCase().contains(t)
                    || x.getGenero().toLowerCase().contains(t)
                    || String.valueOf(x.getCodigo()).contains(t)) {
                r.add(x);
            }
        }
        return r;
    }

    public ArrayList<Libro> todos() {
        return new ArrayList<Libro>(lib);
    }

    public ArrayList<String[]> paraSugerencias() {
        ArrayList<String[]> r = new ArrayList<String[]>();
        for (int i = 0; i < lib.size(); i++) {
            Libro x = lib.get(i);
            r.add(new String[]{String.valueOf(x.getCodigo()),
                    x.getCodigo() + " · " + x.getNombre()});
        }
        return r;
    }

    public int generarCodigo() {
        if (lib.isEmpty()) {
            return CODIGO_INICIAL;
        }

        int max = lib.get(0).getCodigo();
        for (int i = 1; i < lib.size(); i++) {
            if (lib.get(i).getCodigo() > max) {
                max = lib.get(i).getCodigo();
            }
        }
        return max + 1;
    }

    public void ordenarPorCodigo() {
        for (int i = 1; i < lib.size(); i++) {
            Libro actual = lib.get(i);
            int j = i - 1;
            while (j >= 0 && lib.get(j).getCodigo() > actual.getCodigo()) {
                lib.set(j + 1, lib.get(j));
                j--;
            }
            lib.set(j + 1, actual);
        }
    }

    public boolean tieneVentas(Libro x) {
        return arrDet != null && arrDet.tieneVentas(x.getCodigo());
    }

    public void descontarStock(int codigoLibro, int cantidad) {
        Libro x = buscar(codigoLibro);
        if (x != null) {
            int nuevo = x.getStock() - cantidad;
            x.setStock(nuevo < 0 ? 0 : nuevo);
        }
    }

    public void devolverStock(int codigoLibro, int cantidad) {
        Libro x = buscar(codigoLibro);
        if (x != null) {
            x.setStock(x.getStock() + cantidad);
        }
    }

    public ArrayList<Libro> stockBajo(int umbral) {
        ArrayList<Libro> r = new ArrayList<Libro>();
        for (int i = 0; i < lib.size(); i++) {
            if (lib.get(i).stockBajo(umbral)) {
                r.add(lib.get(i));
            }
        }
        return r;
    }

    public BigDecimal valorInventario() {
        BigDecimal t = Dinero.CERO;
        for (int i = 0; i < lib.size(); i++) {
            t = Dinero.suma(t, lib.get(i).valorInventario());
        }
        return t;
    }

    private void cargar() {
        List<String> lineas = Archivo.leer(archivo);
        for (int i = 0; i < lineas.size(); i++) {
            try {
                String[] s = Archivo.partir(lineas.get(i));
                if (s.length < 8) {
                    continue;
                }
                int codigo = Integer.parseInt(s[0]);
                int stock = Integer.parseInt(s[4]);
                BigDecimal compra = Dinero.de(s[5]);
                BigDecimal venta = Dinero.de(s[6]);
                BigDecimal minimo = Dinero.de(s[7]);
                if (compra == null || venta == null || minimo == null) {
                    continue;
                }
                lib.add(new Libro(codigo, s[1], s[2], s[3], stock, compra, venta, minimo));
            } catch (NumberFormatException e) {

            }
        }
    }

    public boolean grabar() {
        List<String> lineas = new ArrayList<String>();
        for (int i = 0; i < lib.size(); i++) {
            Libro x = lib.get(i);
            lineas.add(Archivo.unir(
                    String.valueOf(x.getCodigo()),
                    x.getNombre(),
                    x.getAutor(),
                    x.getGenero(),
                    String.valueOf(x.getStock()),
                    Dinero.texto(x.getPrecioCompra()),
                    Dinero.texto(x.getPrecioVenta()),
                    Dinero.texto(x.getPrecioMinimo())));
        }
        return Archivo.escribir(archivo, lineas);
    }

    public String getArchivo() {
        return archivo;
    }
}
