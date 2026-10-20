package venta.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import venta.control.Sesion;
import venta.datos.ReporteVentas;
import venta.datos.Sistema;
import venta.modelo.Cliente;
import venta.modelo.Libro;
import venta.modelo.Venta;
import venta.util.Dinero;
import venta.util.Fecha;
import venta.util.Validador;

public class FrmReporte extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String[] TIPOS = {
        "Ventas por dia", "Cuadre de caja", "Stock bajo", "Libros mas vendidos",
        "Ventas por cliente", "Ingresos por categoria"};

    private final Sistema sis;

    private final JComboBox<String> cmbTipo;
    private final SelectorFecha txtDesde;
    private final SelectorFecha txtHasta;
    private final JTextField txtUmbral;
    private final JLabel lblDesde;
    private final JLabel lblHasta;
    private final JLabel lblUmbral;
    private final JPanel panelFiltros;
    private DefaultTableModel modelo;
    private final JTable tabla;
    private final JLabel lblTotal;

    public FrmReporte(Sistema sis) {
        this.sis = sis;
        setTitle("Reportes");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);

        cmbTipo = Estilo.combo(TIPOS);

        cmbTipo.setPreferredSize(new Dimension(230, Estilo.ALTO_CAMPO));
        txtDesde = new SelectorFecha(10);
        txtHasta = new SelectorFecha(10);
        txtUmbral = Estilo.monto(6, true);
        txtUmbral.setText(String.valueOf(Libro.UMBRAL_STOCK_BAJO));
        txtDesde.setText(Fecha.hoy());
        txtHasta.setText(Fecha.hoy());

        lblDesde = Estilo.etiqueta("Desde");
        lblHasta = Estilo.etiqueta("Hasta");
        lblUmbral = Estilo.etiqueta("Umbral");

        JButton btnGenerar = Estilo.primario("Generar");
        btnGenerar.addActionListener(this);
        JButton btnHoy = Estilo.botonBarra("Hoy");
        btnHoy.addActionListener(this);
        JButton btnVolver = Estilo.volver();
        btnVolver.addActionListener(this);

        panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        panelFiltros.setOpaque(false);

        modelo = Estilo.modelo(columnas("Ventas por dia"));
        tabla = Estilo.tabla(modelo);

        JPanel derechaBarra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derechaBarra.setOpaque(false);
        derechaBarra.add(btnHoy);
        derechaBarra.add(btnVolver);

        JPanel barra = new JPanel(new BorderLayout(10, 0));
        barra.setOpaque(false);
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izquierda.setOpaque(false);
        izquierda.add(Estilo.etiqueta("Reporte"));
        izquierda.add(cmbTipo);
        izquierda.add(btnGenerar);
        barra.add(izquierda, BorderLayout.WEST);
        barra.add(panelFiltros, BorderLayout.CENTER);

        JPanel resultados = Estilo.tarjeta("Resultados");
        resultados.setLayout(new BorderLayout(0, 8));
        resultados.add(Estilo.scroll(tabla), BorderLayout.CENTER);

        lblTotal = Estilo.mensaje("");
        lblTotal.setFont(new Font(Estilo.FUENTE, Font.BOLD, 15));
        lblTotal.setForeground(Estilo.ACENTO);

        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.setBackground(Estilo.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        contenido.add(barra, BorderLayout.NORTH);
        contenido.add(resultados, BorderLayout.CENTER);
        contenido.add(lblTotal, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(Estilo.cabecera("Reportes", derechaBarra), BorderLayout.NORTH);
        getContentPane().add(contenido, BorderLayout.CENTER);

        cmbTipo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarReporte();
            }
        });
        ActionListener aplicarFecha = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usaFechas((String) cmbTipo.getSelectedItem())) {
                    generar();
                }
            }
        };
        txtDesde.addActionListener(aplicarFecha);
        txtHasta.addActionListener(aplicarFecha);
        cambiarReporte();
    }

    private static String[] columnas(String tipo) {
        if (tipo.equals("Cuadre de caja")) {
            return new String[]{"Metodo de pago", "Ventas",
                "Monto pagado", "Vuelto", "Total vendido"};
        }
        if (tipo.equals("Stock bajo")) {
            return new String[]{"Libro", "Autor", "Stock", "Codigo"};
        }
        if (tipo.equals("Libros mas vendidos")) {
            return new String[]{"Puesto", "Libro", "Cantidad vendida", "Codigo"};
        }
        if (tipo.equals("Ventas por cliente")) {
            return new String[]{"Cliente", "Compras", "Total comprado", "Codigo", "DNI"};
        }
        if (tipo.equals("Ingresos por categoria")) {
            return new String[]{"Categoria", "Unidades", "Ingreso", "% del total"};
        }
        return new String[]{"N.", "Fecha", "Hora", "Cliente", "Metodo", "Total"};
    }

    private static boolean usaFechas(String tipo) {
        return "Ventas por dia".equals(tipo) || "Cuadre de caja".equals(tipo);
    }

    private void cambiarReporte() {
        String tipo = (String) cmbTipo.getSelectedItem();
        panelFiltros.removeAll();
        if (usaFechas(tipo)) {
            panelFiltros.add(lblDesde);
            panelFiltros.add(txtDesde);
            panelFiltros.add(lblHasta);
            panelFiltros.add(txtHasta);
        } else if (tipo.equals("Stock bajo")) {
            panelFiltros.add(lblUmbral);
            panelFiltros.add(txtUmbral);
        }
        panelFiltros.revalidate();
        panelFiltros.repaint();

        modelo = Estilo.modelo(columnas(tipo));
        tabla.setModel(modelo);
        if (tipo.equals("Cuadre de caja")) {
            Estilo.alinearDerecha(tabla, 2, 3, 4);
        } else {
            Estilo.alinearDerecha(tabla, columnaMonto(tipo));
        }
        lblTotal.setText(" ");
        generar();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "Generar":
                generar();
                break;
            case "Hoy":
                txtDesde.setText(Fecha.hoy());
                txtHasta.setText(Fecha.hoy());
                generar();
                break;
            case "Volver":
                sis.grabarTodo();
                dispose();
                break;
            default:
                break;
        }
    }

    private void generar() {
        String tipo = (String) cmbTipo.getSelectedItem();
        modelo.setRowCount(0);
        if (tipo.equals("Ventas por dia")) {
            reporteVentasPorDia();
        } else if (tipo.equals("Cuadre de caja")) {
            reporteCuadreCaja();
        } else if (tipo.equals("Stock bajo")) {
            reporteStockBajo();
        } else if (tipo.equals("Libros mas vendidos")) {
            reporteMasVendidos();
        } else if (tipo.equals("Ventas por cliente")) {
            reporteVentasPorCliente();
        } else {
            reporteIngresosPorCategoria();
        }
    }

    private static int columnaMonto(String tipo) {
        if (tipo.equals("Ventas por dia")) {
            return 5;
        }
        if (tipo.equals("Ventas por cliente")) {
            return 2;
        }
        return 2;
    }

    private ArrayList<Venta> ventasDelRango() {
        String desde = Validador.limpio(txtDesde.getText());
        String hasta = Validador.limpio(txtHasta.getText());
        if (!desde.isEmpty() && !Fecha.esFecha(desde)) {
            Estilo.error(this, "La fecha inicial no tiene el formato dd/mm/aaaa.");
            return null;
        }
        if (!hasta.isEmpty() && !Fecha.esFecha(hasta)) {
            Estilo.error(this, "La fecha final no tiene el formato dd/mm/aaaa.");
            return null;
        }
        LocalDate f1 = Fecha.aFecha(desde);
        LocalDate f2 = Fecha.aFecha(hasta);
        if (f1 != null && f2 != null && f1.isAfter(f2)) {
            Estilo.error(this, "La fecha inicial no puede ser mayor que la fecha final.");
            return null;
        }
        return sis.getReporte().ventasPorDia(desde, hasta);
    }

    private void reporteVentasPorDia() {
        ArrayList<Venta> lista = ventasDelRango();
        if (lista == null) {
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            Venta v = lista.get(i);
            Cliente c = v.clienteRegistrado()
                    ? sis.getClientes().buscar(v.getDni()) : null;
            modelo.addRow(new Object[]{
                v.numeroTexto(), v.getFecha(), v.getHora(),
                c == null ? "Público general" : c.nombreCompleto(),
                v.getMetodo(), Dinero.texto(v.getTotal())});
        }
        lblTotal.setText(lista.isEmpty()
                ? "No hay ventas en el rango indicado."
                : lista.size() + " venta(s) · total " + Dinero.soles(sis.getReporte().totalVentas(lista)));
    }

    private void reporteCuadreCaja() {
        ArrayList<Venta> lista = ventasDelRango();
        if (lista == null) {
            return;
        }
        ArrayList<ReporteVentas.FilaCuadre> filas = sis.getReporte().cuadreCaja(lista);
        int totalVentas = 0;
        BigDecimal totalPagado = Dinero.CERO;
        BigDecimal totalVuelto = Dinero.CERO;
        BigDecimal totalGeneral = Dinero.CERO;
        BigDecimal efectivo = Dinero.CERO;
        for (int i = 0; i < filas.size(); i++) {
            ReporteVentas.FilaCuadre f = filas.get(i);
            modelo.addRow(new Object[]{
                f.getMetodo(), f.getVentas(), Dinero.texto(f.getMontoPagado()),
                Dinero.texto(f.getVuelto()), Dinero.texto(f.getTotal())});
            totalVentas += f.getVentas();
            totalPagado = Dinero.suma(totalPagado, f.getMontoPagado());
            totalVuelto = Dinero.suma(totalVuelto, f.getVuelto());
            totalGeneral = Dinero.suma(totalGeneral, f.getTotal());
            if (Venta.EFECTIVO.equals(f.getMetodo())) {
                efectivo = f.getTotal();
            }
        }
        modelo.addRow(new Object[]{"TOTAL", totalVentas, Dinero.texto(totalPagado),
            Dinero.texto(totalVuelto), Dinero.texto(totalGeneral)});
        lblTotal.setText(lista.isEmpty()
                ? "No hay ventas en el rango indicado."
                : totalVentas + " venta(s) · total " + Dinero.soles(totalGeneral)
                + " · efectivo en caja " + Dinero.soles(efectivo));
    }

    private void reporteStockBajo() {
        int umbral = Validador.entero(txtUmbral.getText(), Libro.UMBRAL_STOCK_BAJO);
        ArrayList<Libro> lista = sis.getReporte().stockBajo(umbral);
        for (int i = 0; i < lista.size(); i++) {
            Libro x = lista.get(i);
            modelo.addRow(new Object[]{x.getNombre(), x.getAutor(), x.getStock(), x.getCodigo()});
        }
        lblTotal.setText(lista.isEmpty()
                ? "No hay libros con stock menor o igual a " + umbral + "."
                : lista.size() + " libro(s) con stock menor o igual a " + umbral + ".");
    }

    private void reporteMasVendidos() {
        ArrayList<Libro> lista = sis.getReporte().masVendidos();
        for (int i = 0; i < lista.size(); i++) {
            Libro x = lista.get(i);
            modelo.addRow(new Object[]{
                i + 1, x.getNombre(), sis.getReporte().unidades(x.getCodigo()),
                x.getCodigo()});
        }
        lblTotal.setText(lista.isEmpty()
                ? "Todavía no hay ventas registradas."
                : lista.size() + " libro(s) vendidos, ordenados de mayor a menor.");
    }

    private void reporteVentasPorCliente() {
        ArrayList<ReporteVentas.FilaCliente> lista = sis.getReporte().ventasPorCliente();
        BigDecimal total = Dinero.CERO;
        int conCompras = 0;
        for (int i = 0; i < lista.size(); i++) {
            ReporteVentas.FilaCliente f = lista.get(i);
            modelo.addRow(new Object[]{
                f.getCliente().nombreCompleto(), f.getCompras(), Dinero.texto(f.getTotal()),
                f.getCliente().getCodigo(), f.getCliente().getDni()});
            total = Dinero.suma(total, f.getTotal());
            if (f.getCompras() > 0) {
                conCompras++;
            }
        }
        lblTotal.setText(lista.isEmpty()
                ? "No hay clientes registrados."
                : lista.size() + " cliente(s), " + conCompras + " con compras · total "
                + Dinero.soles(total));
    }

    private void reporteIngresosPorCategoria() {
        ArrayList<ReporteVentas.FilaCategoria> lista = sis.getReporte().ingresosPorCategoria();
        BigDecimal total = Dinero.CERO;
        for (int i = 0; i < lista.size(); i++) {
            total = Dinero.suma(total, lista.get(i).getIngreso());
        }
        for (int i = 0; i < lista.size(); i++) {
            ReporteVentas.FilaCategoria f = lista.get(i);
            modelo.addRow(new Object[]{
                f.getCategoria(), f.getUnidades(), Dinero.texto(f.getIngreso()),
                porcentaje(f.getIngreso(), total)});
        }
        lblTotal.setText(lista.isEmpty()
                ? "No hay categorias registradas."
                : lista.size() + " categoria(s) · total " + Dinero.soles(total));
    }

    private static String porcentaje(BigDecimal parte, BigDecimal total) {
        if (total.signum() == 0) {
            return "0.0 %";
        }
        BigDecimal p = parte.multiply(new BigDecimal("100"))
                .divide(total, 1, java.math.RoundingMode.HALF_UP);
        return p.toPlainString() + " %";
    }

    public static boolean permitido() {
        return Sesion.esAdmin();
    }

    public JComboBox<String> combo() {
        return cmbTipo;
    }

    public void generarReporte() {
        generar();
    }
}
