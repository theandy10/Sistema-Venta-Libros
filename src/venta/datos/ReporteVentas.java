package venta.datos;

import java.math.BigDecimal;
import java.util.ArrayList;
import venta.modelo.Cliente;
import venta.modelo.DetalleVenta;
import venta.modelo.Libro;
import venta.modelo.Venta;
import venta.util.Dinero;

public class ReporteVentas {

    public static class FilaCliente {
        private final Cliente cliente;
        private final int compras;
        private final BigDecimal total;

        public FilaCliente(Cliente cliente, int compras, BigDecimal total) {
            this.cliente = cliente;
            this.compras = compras;
            this.total = Dinero.escala(total);
        }

        public Cliente getCliente() {
            return cliente;
        }

        public int getCompras() {
            return compras;
        }

        public BigDecimal getTotal() {
            return total;
        }
    }

    public static class FilaCategoria {
        private final String categoria;
        private final int unidades;
        private final BigDecimal ingreso;

        public FilaCategoria(String categoria, int unidades, BigDecimal ingreso) {
            this.categoria = categoria;
            this.unidades = unidades;
            this.ingreso = Dinero.escala(ingreso);
        }

        public String getCategoria() {
            return categoria;
        }

        public int getUnidades() {
            return unidades;
        }

        public BigDecimal getIngreso() {
            return ingreso;
        }
    }

    private final ArregloLibros arrLib;
    private final ArregloClientes arrCli;
    private final ArregloVentas arrVen;
    private final ArregloDetalle arrDet;

    public ReporteVentas(ArregloLibros arrLib, ArregloClientes arrCli,
                         ArregloVentas arrVen, ArregloDetalle arrDet) {
        this.arrLib = arrLib;
        this.arrCli = arrCli;
        this.arrVen = arrVen;
        this.arrDet = arrDet;
    }

    public ArrayList<Venta> ventasPorDia(String desde, String hasta) {
        return arrVen.ventasEntre(desde, hasta);
    }

    public BigDecimal totalVentas(ArrayList<Venta> lista) {
        return arrVen.totalDe(lista);
    }

    public int cantidadVentas(ArrayList<Venta> lista) {
        return lista.size();
    }

    public ArrayList<Libro> stockBajo(int umbral) {
        return arrLib.stockBajo(umbral);
    }

    public int cantidadStockBajo(int umbral) {
        return arrLib.stockBajo(umbral).size();
    }

    public int unidades(int codigoLibro) {
        return arrDet.unidadesVendidas(codigoLibro);
    }

    public ArrayList<Libro> masVendidos() {

        ArrayList<int[]> cuenta = new ArrayList<int[]>();
        ArrayList<Libro> todos = arrLib.todos();
        for (int i = 0; i < todos.size(); i++) {
            Libro x = todos.get(i);
            int u = arrDet.unidadesVendidas(x.getCodigo());
            if (u > 0) {
                cuenta.add(new int[]{x.getCodigo(), u});
            }
        }

        for (int i = 1; i < cuenta.size(); i++) {
            int[] actual = cuenta.get(i);
            int j = i - 1;
            while (j >= 0 && cuenta.get(j)[1] < actual[1]) {
                cuenta.set(j + 1, cuenta.get(j));
                j--;
            }
            cuenta.set(j + 1, actual);
        }
        ArrayList<Libro> r = new ArrayList<Libro>();
        for (int i = 0; i < cuenta.size(); i++) {
            Libro x = arrLib.buscar(cuenta.get(i)[0]);
            if (x != null) {
                r.add(x);
            }
        }
        return r;
    }

    public ArrayList<FilaCliente> ventasPorCliente() {
        ArrayList<FilaCliente> r = new ArrayList<FilaCliente>();
        for (int i = 0; i < arrCli.tamaño(); i++) {
            Cliente c = arrCli.obtener(i);
            ArrayList<Venta> v = arrVen.ventasDeCliente(c.getDni());
            r.add(new FilaCliente(c, v.size(), arrVen.totalDe(v)));
        }
        return r;
    }

    public ArrayList<FilaCategoria> ingresosPorCategoria() {
        ArrayList<String> categorias = new ArrayList<String>();
        for (int i = 0; i < arrLib.tamaño(); i++) {
            String g = arrLib.obtener(i).getGenero();
            if (!categorias.contains(g)) {
                categorias.add(g);
            }
        }
        ArrayList<FilaCategoria> r = new ArrayList<FilaCategoria>();
        for (int c = 0; c < categorias.size(); c++) {
            String cat = categorias.get(c);
            int unidades = 0;
            BigDecimal ingreso = Dinero.CERO;
            for (int i = 0; i < arrDet.tamaño(); i++) {
                DetalleVenta d = arrDet.obtener(i);
                Libro l = arrLib.buscar(d.getCodigoLibro());
                if (l != null && cat.equals(l.getGenero())) {
                    unidades += d.getCantidad();
                    ingreso = Dinero.suma(ingreso, d.getSubtotal());
                }
            }
            r.add(new FilaCategoria(cat, unidades, ingreso));
        }

        for (int i = 1; i < r.size(); i++) {
            FilaCategoria actual = r.get(i);
            int j = i - 1;
            while (j >= 0 && r.get(j).getIngreso().compareTo(actual.getIngreso()) < 0) {
                r.set(j + 1, r.get(j));
                j--;
            }
            r.set(j + 1, actual);
        }
        return r;
    }

    public BigDecimal totalHoy(String usuario) {
        return arrVen.totalDe(arrVen.ventasDeHoy(usuario));
    }

    public int ventasDeHoy(String usuario) {
        return arrVen.ventasDeHoy(usuario).size();
    }

    public Venta ultimaVenta(String usuario) {
        return arrVen.ultimaVenta(usuario);
    }
}
