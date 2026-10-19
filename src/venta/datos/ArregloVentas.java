package venta.datos;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import venta.modelo.Venta;
import venta.modelo.DetalleVenta;
import venta.util.Archivo;
import venta.util.Dinero;
import venta.util.Fecha;
import venta.util.Validador;

public class ArregloVentas {

    private ArrayList<Venta> ven;
    private final String archivo;
    private final ArregloLibros arrLib;
    private final ArregloDetalle arrDet;

    public ArregloVentas(String carpeta, ArregloLibros arrLib, ArregloDetalle arrDet) {
        this.archivo = carpeta + File.separator + "ventas.txt";
        this.arrLib = arrLib;
        this.arrDet = arrDet;
        this.ven = new ArrayList<Venta>();
        cargar();
    }

    public Venta registrar(String usuario, String dni, ArrayList<DetalleVenta> detalle,
                            String fecha, String hora, BigDecimal descuento,
                            String metodo, BigDecimal montoPagado) {
        int numero = generarNumero();
        BigDecimal bruto = arrDet.sumaSubtotales(detalle);
        for (int i = 0; i < detalle.size(); i++) {
            DetalleVenta d = detalle.get(i);
            d.setNumero(numero);
            arrDet.adicionar(d);
        }
        Venta x = new Venta(numero, fecha, hora, usuario, dni, bruto, descuento, metodo, montoPagado);
        ven.add(x);
        for (int i = 0; i < detalle.size(); i++) {
            arrLib.descontarStock(detalle.get(i).getCodigoLibro(), detalle.get(i).getCantidad());
        }
        return x;
    }

    public boolean anular(int numero) {
        Venta x = buscar(numero);
        if (x == null) {
            return false;
        }
        ArrayList<DetalleVenta> d = arrDet.detalleDeVenta(numero);
        for (int i = 0; i < d.size(); i++) {
            arrLib.devolverStock(d.get(i).getCodigoLibro(), d.get(i).getCantidad());
        }
        arrDet.eliminarDeVenta(numero);
        ven.remove(x);
        return true;
    }

    public int generarNumero() {
        int max = 0;
        for (int i = 0; i < ven.size(); i++) {
            if (ven.get(i).getNumero() > max) {
                max = ven.get(i).getNumero();
            }
        }
        return max + 1;
    }

    public Venta buscar(int numero) {
        for (int i = 0; i < ven.size(); i++) {
            if (ven.get(i).getNumero() == numero) {
                return ven.get(i);
            }
        }
        return null;
    }

    public int tamaño() {
        return ven.size();
    }

    public Venta obtener(int pos) {
        return ven.get(pos);
    }

    public ArrayList<String[]> paraSugerencias() {
        ArrayList<String[]> r = new ArrayList<String[]>();
        for (int i = 0; i < ven.size(); i++) {
            Venta x = ven.get(i);
            r.add(new String[]{String.valueOf(x.getNumero()),
                    x.numeroTexto() + " · " + x.getFecha() + " " + x.getHora()});
        }
        return r;
    }

    public ArrayList<Venta> todas() {
        return new ArrayList<Venta>(ven);
    }

    public ArrayList<Venta> todasPorFechaDescendente() {
        return ordenarRecientes(new ArrayList<Venta>(ven));
    }

    private ArrayList<Venta> ordenarRecientes(ArrayList<Venta> lista) {
        for (int i = 1; i < lista.size(); i++) {
            Venta actual = lista.get(i);
            int j = i - 1;
            while (j >= 0 && esMasReciente(actual, lista.get(j))) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, actual);
        }
        return lista;
    }

    private boolean esMasReciente(Venta a, Venta b) {

        LocalDate fa = Fecha.aFecha(a.getFecha());
        LocalDate fb = Fecha.aFecha(b.getFecha());
        if (fa != null && fb != null && !fa.equals(fb)) {
            return fa.isAfter(fb);
        }
        if (!a.getHora().equals(b.getHora())) {
            return a.getHora().compareTo(b.getHora()) > 0;
        }
        return a.getNumero() > b.getNumero();
    }

    public ArrayList<Venta> ventasDeCliente(String dni) {
        ArrayList<Venta> r = new ArrayList<Venta>();
        for (int i = ven.size() - 1; i >= 0; i--) {
            if (ven.get(i).getDni().equals(dni)) {
                r.add(ven.get(i));
            }
        }
        return r;
    }

    public boolean tieneVentas(String dni) {
        for (int i = 0; i < ven.size(); i++) {
            if (ven.get(i).getDni().equals(dni)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Venta> ventasEntre(String desde, String hasta) {
        LocalDate f1 = Fecha.aFecha(desde);
        LocalDate f2 = Fecha.aFecha(hasta);
        ArrayList<Venta> r = new ArrayList<Venta>();
        for (int i = 0; i < ven.size(); i++) {
            Venta v = ven.get(i);
            LocalDate f = Fecha.aFecha(v.getFecha());
            if (f == null) {
                continue;
            }
            boolean ok1 = f1 == null || !f.isBefore(f1);
            boolean ok2 = f2 == null || !f.isAfter(f2);
            if (ok1 && ok2) {
                r.add(v);
            }
        }
        return ordenarRecientes(r);
    }

    public ArrayList<Venta> ventasDeHoy(String usuario) {
        ArrayList<Venta> r = new ArrayList<Venta>();
        for (int i = 0; i < ven.size(); i++) {
            Venta v = ven.get(i);
            if (Fecha.esHoy(v.getFecha())
                    && (usuario == null || usuario.equals(v.getUsuario()))) {
                r.add(v);
            }
        }
        return ordenarRecientes(r);
    }

    public ArrayList<Venta> ventasDeUsuario(String usuario) {
        ArrayList<Venta> r = new ArrayList<Venta>();
        for (int i = 0; i < ven.size(); i++) {
            if (ven.get(i).getUsuario().equals(usuario)) {
                r.add(ven.get(i));
            }
        }
        return ordenarRecientes(r);
    }

    public Venta ultimaVenta(String usuario) {
        ArrayList<Venta> lista = usuario == null
                ? todasPorFechaDescendente() : ventasDeUsuario(usuario);
        return lista.isEmpty() ? null : lista.get(0);
    }

    public BigDecimal totalDe(ArrayList<Venta> lista) {
        BigDecimal t = Dinero.CERO;
        for (int i = 0; i < lista.size(); i++) {
            t = Dinero.suma(t, lista.get(i).getTotal());
        }
        return t;
    }

    private void cargar() {
        List<String> lineas = Archivo.leer(archivo);
        for (int i = 0; i < lineas.size(); i++) {
            try {
                String[] s = Archivo.partir(lineas.get(i));
                if (s.length < 11) {
                    continue;
                }
                int numero = Integer.parseInt(s[0]);
                String fecha = s[1];
                String hora = s[2];
                String usuario = s[3];
                String dni = s[4].isEmpty() ? Venta.PUBLICO : s[4];
                BigDecimal descuento = Dinero.de(s[5]);
                BigDecimal subtotal = Dinero.de(s[6]);
                String metodo = s[8];
                BigDecimal pagado = Dinero.de(s[9]);
                if (descuento == null || subtotal == null || pagado == null) {
                    continue;
                }

                ven.add(new Venta(numero, fecha, hora, usuario, dni,
                        subtotal, descuento, metodo, pagado));
            } catch (NumberFormatException e) {

            }
        }
    }

    public boolean grabar() {
        List<String> lineas = new ArrayList<String>();
        for (int i = 0; i < ven.size(); i++) {
            Venta v = ven.get(i);
            lineas.add(Archivo.unir(
                    String.format(Locale.US, "%06d", v.getNumero()),
                    v.getFecha(),
                    v.getHora(),
                    Validador.limpio(v.getUsuario()).isEmpty() ? "admin" : v.getUsuario(),
                    v.getDni(),
                    Dinero.texto(v.getDescuento()),
                    Dinero.texto(v.getSubtotal()),
                    Dinero.texto(v.getTotal()),
                    v.getMetodo(),
                    Dinero.texto(v.getMontoPagado()),
                    Dinero.texto(v.getVuelto())));
        }
        return Archivo.escribir(archivo, lineas);
    }

    public String getArchivo() {
        return archivo;
    }
}
