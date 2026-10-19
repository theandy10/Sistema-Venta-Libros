package venta.datos;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;
import venta.modelo.Cliente;
import venta.modelo.DetalleVenta;
import venta.modelo.Venta;
import venta.util.Archivo;
import venta.util.Dinero;

public final class Boleta {

    private static final int ANCHO = 62;

    private Boleta() {

    }

    public static String generar(Venta v, Cliente c, ArrayList<DetalleVenta> detalle) {
        StringBuilder sb = new StringBuilder();
        int unidades = 0;

        sb.append(centro("BOOK CENTER", ANCHO)).append('\n');
        sb.append(centro("Sistema de venta de libros", ANCHO)).append('\n');
        sb.append(linea('=')).append('\n');
        sb.append(centro("BOLETA DE VENTA N. " + v.numeroTexto(), ANCHO)).append('\n');
        sb.append(linea('-')).append('\n');
        sb.append(fecha("Fecha")).append(v.getFecha()).append('\n');
        sb.append(fecha("Hora")).append(v.getHora()).append('\n');

        if (c != null && v.clienteRegistrado()) {
            sb.append(fecha("Cliente")).append(c.nombreCompleto()).append('\n');
            sb.append(fecha("DNI")).append(c.getDni()).append('\n');
            sb.append(fecha("Celular")).append(c.getCelular()).append('\n');
        } else {
            sb.append(fecha("Cliente")).append("PUBLICO GENERAL").append('\n');
        }
        sb.append(fecha("Atendido por")).append(v.getUsuario()).append('\n');
        sb.append(linea('-')).append('\n');

        sb.append(String.format(Locale.US, "%-6s %-26s %4s %10s %11s",
                "COD", "DESCRIPCION", "CANT", "P. UNIT", "SUBTOTAL")).append('\n');
        sb.append(linea('-')).append('\n');
        for (int i = 0; i < detalle.size(); i++) {
            DetalleVenta d = detalle.get(i);
            sb.append(String.format(Locale.US, "%-6d %-26s %4d %10s %11s",
                    d.getCodigoLibro(),
                    cortar(d.getNombreLibro(), 26),
                    d.getCantidad(),
                    Dinero.texto(d.getPrecioUnitario()),
                    Dinero.texto(d.getSubtotal()))).append('\n');
            unidades += d.getCantidad();
        }
        sb.append(linea('-')).append('\n');
        sb.append(String.format(Locale.US, "%-40s %4d", "TOTAL UNIDADES", unidades)).append('\n');
        sb.append(linea('=')).append('\n');
        sb.append(monto("Subtotal", Dinero.texto(v.getSubtotal()))).append('\n');
        sb.append(monto("Descuento", Dinero.texto(v.getDescuento()))).append('\n');
        sb.append(monto("TOTAL", Dinero.texto(v.getTotal()))).append('\n');

        sb.append(monto("IGV (18%) incluido", Dinero.texto(igvIncluido(v.getTotal())))).append('\n');
        sb.append(linea('-')).append('\n');
        sb.append(fecha("Metodo de pago")).append(v.getMetodo()).append('\n');
        sb.append(monto("Monto pagado", Dinero.texto(v.getMontoPagado()))).append('\n');
        sb.append(monto("Vuelto", Dinero.texto(v.getVuelto()))).append('\n');
        sb.append(linea('=')).append('\n');
        sb.append('\n');
        sb.append(centro("Gracias por su compra", ANCHO)).append('\n');
        return sb.toString();
    }

    public static boolean guardar(String texto, String carpeta, int numero) {
        File dir = new File(carpeta, "boletas");
        if (!Archivo.crearCarpeta(dir.getAbsolutePath())) {
            return false;
        }
        File f = new File(dir, "boleta_" + String.format(Locale.US, "%06d", numero) + ".txt");
        ArrayList<String> lineas = new ArrayList<String>();
        for (String s : texto.split("\n")) {
            lineas.add(s);
        }
        return Archivo.escribir(f.getAbsolutePath(), lineas);
    }

    public static String rutaArchivo(String carpeta, int numero) {
        return carpeta + File.separator + "boletas" + File.separator
                + "boleta_" + String.format(Locale.US, "%06d", numero) + ".txt";
    }

    public static void imprimir(final String texto, final int numero) throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Boleta " + String.format(Locale.US, "%06d", numero));
        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics g, PageFormat pf, int pageIndex) {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }
                Graphics2D g2 = (Graphics2D) g;
                g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
                g2.translate(pf.getImageableX(), pf.getImageableY());
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int y = 0;
                String[] lineas = texto.split("\n");
                for (int i = 0; i < lineas.length; i++) {
                    g2.drawString(lineas[i], 0, y);
                    y += fm.getHeight();
                }
                return PAGE_EXISTS;
            }
        });
        if (job.printDialog()) {
            job.print();
        }
    }

    private static String linea(char c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ANCHO; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    private static String centro(String texto, int ancho) {
        int espacios = (ancho - texto.length()) / 2;
        if (espacios < 0) {
            espacios = 0;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < espacios; i++) {
            sb.append(' ');
        }
        return sb.append(texto).toString();
    }

    private static String fecha(String etiqueta) {
        return String.format(Locale.US, "%-16s: ", etiqueta);
    }

    private static String monto(String etiqueta, String valor) {
        return String.format(Locale.US, "%-38s S/ %10s", etiqueta, valor);
    }

    private static BigDecimal igvIncluido(BigDecimal total) {
        BigDecimal base = total.divide(new BigDecimal("1.18"), 2,
                java.math.RoundingMode.HALF_UP);
        return Dinero.escala(total.subtract(base));
    }

    private static String cortar(String texto, int largo) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= largo) {
            return texto;
        }
        return texto.substring(0, largo - 1) + ".";
    }
}
