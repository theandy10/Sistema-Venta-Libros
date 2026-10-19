package venta.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import venta.control.Sesion;
import venta.datos.Sistema;
import venta.modelo.Libro;
import venta.modelo.Venta;
import venta.util.Dinero;
import venta.util.Fecha;

public class FrmInicio extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final Sistema sis;
    private FrmRegistroVentas frmRegistro;

    private final JLabel lblVentasHoy;
    private final JLabel lblVentasHoyDetalle;
    private final JLabel lblStockBajo;
    private final JLabel lblUltimaVenta;

    private final DefaultTableModel modeloUltimas;
    private final JTable tablaUltimas;
    private final JPanel panelRegistro;
    private final JPanel panelConsulta;
    private final DefaultTableModel modeloStock;
    private final JTable tablaStock;
    private final JPanel panelMantenimiento;
    private final JPanel panelReporte;
    private final JPanel panelStockBajo;
    private final JButton btnMisVentas;
    private final JButton btnReporteVentasPorDia;
    private final JButton btnReporteStockBajo;
    private final JButton btnReporteMasVendidos;
    private final JButton btnReporteCategoria;
    private final JLabel lblEstado;

    public FrmInicio(Sistema sis) {
        this.sis = sis;
        setTitle("Librería Virtual");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new Dimension(1180, 720));
        setLocationRelativeTo(null);

        boolean admin = Sesion.esAdmin();

        String filtroUsuario = admin ? null : Sesion.codigo();

        JPanel barra = Estilo.barra();
        barra.add(Estilo.deBarra("Librería Virtual", 20, true), BorderLayout.WEST);
JPanel navegacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        navegacion.setOpaque(false);
        if (admin) {
            navegacion.add(itemBarra("Mantenimiento", "navMantenimiento"));
        }
        navegacion.add(itemBarra("Registro", "navRegistro"));
        navegacion.add(itemBarra("Consulta", "navConsulta"));
        if (admin) {
            navegacion.add(itemBarra("Reporte", "navReporte"));
        }
        barra.add(navegacion, BorderLayout.CENTER);
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);
        derecha.add(Estilo.deBarra(Sesion.nombreCompleto() + " · " + Sesion.perfil(), 14, false));
        JButton btnSalir = Estilo.botonBarra("Cerrar sesión");
        btnSalir.addActionListener(this);
        derecha.add(btnSalir);
        barra.add(derecha, BorderLayout.EAST);

        JPanel saludo = new JPanel(new BorderLayout());
        saludo.setBackground(Estilo.FONDO);
        saludo.setBorder(BorderFactory.createEmptyBorder(12, 20, 6, 20));
        JLabel lblHola = Estilo.titulo("Bienvenido, " + Sesion.nombreCompleto());
        JLabel lblFecha = Estilo.etiqueta(Fecha.hoyLargo() + "   ·   Usuario: " + Sesion.codigo());
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new javax.swing.BoxLayout(textos, javax.swing.BoxLayout.Y_AXIS));
        textos.add(lblHola);
        textos.add(javax.swing.Box.createVerticalStrut(4));
        textos.add(lblFecha);
        saludo.add(textos, BorderLayout.WEST);

        lblVentasHoy = new JLabel(" ");
        lblVentasHoyDetalle = new JLabel(" ");
        lblStockBajo = new JLabel(" ");
        lblUltimaVenta = new JLabel(" ");

        JPanel resumen = new JPanel(new GridLayout(1, 3, 16, 0));
        resumen.setBackground(Estilo.FONDO);
        resumen.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        resumen.setPreferredSize(new Dimension(10, 128));
        resumen.add(tarjeta(admin ? "Ventas de hoy" : "Mis ventas de hoy",
                lblVentasHoy, lblVentasHoyDetalle, null));
        resumen.add(tarjeta("Libros con stock bajo", lblStockBajo,
                new JLabel(admin ? "Revisar en Reporte" : "Solo aviso"),
                new JLabel("Con " + Libro.UMBRAL_STOCK_BAJO + " unidades o menos")));
        resumen.add(tarjeta(admin ? "Última venta" : "Mi última venta",
                lblUltimaVenta, new JLabel(" "), null));

        modeloUltimas = Estilo.modelo(new String[]{"N.", "Cliente", "Total", "Hora"});
        tablaUltimas = Estilo.tabla(modeloUltimas);
        tablaUltimas.setRowHeight(26);
        Estilo.alinearDerecha(tablaUltimas, 2);

        JButton btnNueva = Estilo.primario("Nueva venta");
        btnNueva.addActionListener(this);
        btnMisVentas = Estilo.secundario(admin ? "Ventas registradas" : "Mis ventas registradas");
        btnMisVentas.addActionListener(this);

        panelRegistro = panelRegistro("Registro de ventas",
                "Busca el libro, elige al cliente y cobra en una pantalla.");
        JPanel botonesRegistro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botonesRegistro.setOpaque(false);
        botonesRegistro.add(btnNueva);
        botonesRegistro.add(btnMisVentas);
        panelRegistro.add(botonesRegistro, BorderLayout.CENTER);
JLabel tituloUltimas = Estilo.etiqueta("Últimas ventas de hoy");
        JPanel cajaUltimas = new JPanel(new BorderLayout(0, 6));
        cajaUltimas.setOpaque(false);
        cajaUltimas.add(tituloUltimas, BorderLayout.NORTH);
        JScrollPane scUltimas = Estilo.scroll(tablaUltimas);

        scUltimas.setPreferredSize(new Dimension(400, 140));
        cajaUltimas.add(scUltimas, BorderLayout.CENTER);
        panelRegistro.add(cajaUltimas, BorderLayout.SOUTH);

        JButton btnLibros = botonModulo("Libros", "Libros", 120);
        JButton btnClientes = botonModulo("Clientes", "Clientes", 120);
        JButton btnUsuarios = botonNoImplementado("Usuarios", 120);
        JButton btnGeneros = botonNoImplementado("Géneros", 120);
        JButton btnVentaC = botonModulo("Ventas", "Ventas");
        JButton btnVentasPropias = botonModulo("Mis ventas", "Mis ventas");
        btnReporteVentasPorDia = botonModulo("Ventas por día", "Ventas por día", 0);
        btnReporteStockBajo = botonModulo("Stock bajo", "Stock bajo", 0);
        btnReporteMasVendidos = botonModulo("Más vendidos", "Más vendidos", 0);
        btnReporteCategoria = botonModulo("Ingresos por categoría", "Ingresos por categoría", 0);

panelConsulta = panelConsulta(admin, btnLibros, btnClientes, btnVentasPropias, btnVentaC);

panelMantenimiento = tarjetaModulo("Mantenimiento",
                "Altas, cambios y bajas de libros y clientes.",
                new JButton[]{
                    botonModulo("Libros", "Libros", 120),
                    botonModulo("Clientes", "Clientes", 120),
                    btnUsuarios,
                    btnGeneros}, 2);
        panelReporte = tarjetaModulo("Reporte",
                "Resúmenes de ventas, de stock y de títulos.",
                new JButton[]{
                    btnReporteVentasPorDia, btnReporteStockBajo,
                    btnReporteMasVendidos, btnReporteCategoria}, 2);

        modeloStock = Estilo.modelo(new String[]{"Libro", "Stock"});
        tablaStock = Estilo.tabla(modeloStock);
        tablaStock.setRowHeight(22);
        JScrollPane scStock = Estilo.scroll(tablaStock);
        scStock.setPreferredSize(new Dimension(300, 90));
        Estilo.alinearDerecha(tablaStock, 1);
panelStockBajo = tarjetaModulo("Stock bajo",
                "Aviso al administrador: reponer libros.", new JButton[0], 1);
        panelStockBajo.add(scStock, BorderLayout.CENTER);

        JPanel columnaDerecha = new JPanel(new GridLayout(0, 1, 0, 14));
        columnaDerecha.setBackground(Estilo.FONDO);
        columnaDerecha.add(panelConsulta);
        if (admin) {
            columnaDerecha.add(panelMantenimiento);
            columnaDerecha.add(panelReporte);
        } else {
            columnaDerecha.add(panelStockBajo);
        }

        JPanel cuerpo = new JPanel(new BorderLayout(16, 0));
        cuerpo.setBackground(Estilo.FONDO);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(0, 20, 14, 20));
        cuerpo.add(panelRegistro, BorderLayout.CENTER);
        columnaDerecha.setPreferredSize(new Dimension(380, 10));
        cuerpo.add(columnaDerecha, BorderLayout.EAST);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setBackground(Estilo.FONDO);
        centro.add(saludo, BorderLayout.NORTH);
        centro.add(resumen, BorderLayout.CENTER);
        JPanel centro2 = new JPanel(new BorderLayout());
        centro2.setBackground(Estilo.FONDO);
        centro2.add(centro, BorderLayout.NORTH);
        centro2.add(cuerpo, BorderLayout.CENTER);
        centro2.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        lblEstado = Estilo.etiqueta("");
        lblEstado.setOpaque(true);
        lblEstado.setBackground(Estilo.TARJETA);
        lblEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.BORDE),
                BorderFactory.createEmptyBorder(6, 20, 8, 20)));

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(barra, BorderLayout.NORTH);
        raiz.add(centro2, BorderLayout.CENTER);
        raiz.add(lblEstado, BorderLayout.SOUTH);
        setContentPane(raiz);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sis.grabarTodo();
            }
        });
        refrescar();
    }

    private JButton itemBarra(String texto, String comando) {
        JButton b = Estilo.botonBarra(texto);
        b.setActionCommand(comando);
        b.addActionListener(this);
        return b;
    }

private JButton botonModulo(String texto, String comando) {
        return botonModulo(texto, comando, 96);
    }

    private JButton botonModulo(String texto, String comando, int ancho) {
        JButton b = ancho > 0 ? Estilo.compacto(texto, ancho) : Estilo.boton(texto, false);
        b.setActionCommand(comando);
        b.addActionListener(this);
        return b;
    }

    private JButton botonNoImplementado(String texto, int ancho) {
        JButton b = Estilo.secundario(texto);
        b.setPreferredSize(new Dimension(ancho, 28));
        b.setEnabled(false);
        b.setToolTipText("Módulo no implementado en esta versión del proyecto.");
        return b;
    }

    private JPanel tarjeta(String titulo, JLabel cifra, JLabel nota, JLabel extra) {
        JPanel p = Estilo.tarjeta(titulo);
        p.setLayout(new BorderLayout(0, 4));
        cifra.setFont(new Font(Estilo.FUENTE, Font.BOLD, 24));
        if (nota != null) {
            nota.setForeground(Estilo.SECUNDARIO);
        }
        if (extra != null) {
            extra.setForeground(Estilo.SECUNDARIO);
        }
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new javax.swing.BoxLayout(textos, javax.swing.BoxLayout.Y_AXIS));
        if (nota != null) {
            textos.add(nota);
        }
        if (extra != null) {
            textos.add(extra);
        }
        textos.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        textos.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
        textos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JPanel arriba = new JPanel(new java.awt.BorderLayout(0, 4));
        arriba.add(cifra, BorderLayout.NORTH);
        arriba.add(textos, BorderLayout.CENTER);
        p.add(arriba, BorderLayout.NORTH);
        p.add(Estilo.relleno(), BorderLayout.CENTER);
        return p;
    }

    private JPanel panelRegistro(String titulo, String descripcion) {
        JPanel p = Estilo.tarjeta(titulo);
        p.setLayout(new BorderLayout(0, 10));
        JLabel d = Estilo.texto(descripcion);
        JPanel cabecera = new JPanel();
        cabecera.setLayout(new javax.swing.BoxLayout(cabecera, javax.swing.BoxLayout.Y_AXIS));
        cabecera.add(d);
        p.add(cabecera, BorderLayout.NORTH);
        return p;
    }

    private JPanel tarjetaModulo(String titulo, String descripcion, JButton[] botones, int columnas) {
        JPanel p = Estilo.tarjeta(titulo);
        p.setLayout(new BorderLayout(0, 6));
        JLabel d = Estilo.texto(descripcion);
        d.setForeground(Estilo.SECUNDARIO);

        JPanel arriba = new JPanel();
        arriba.setOpaque(false);
        arriba.setLayout(new javax.swing.BoxLayout(arriba, javax.swing.BoxLayout.Y_AXIS));

        d.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        arriba.add(d);
        arriba.add(javax.swing.Box.createVerticalStrut(6));
        JPanel grilla = Estilo.grillaBotones(
                (botones.length + columnas - 1) / columnas, columnas, botones);
        grilla.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        arriba.add(grilla);
        Estilo.sinEstirar(arriba);
        p.add(arriba, BorderLayout.NORTH);
        p.add(Estilo.relleno(), BorderLayout.CENTER);
        return p;
    }

private JPanel panelConsulta(boolean admin, JButton libros, JButton clientes,
                                  JButton misVentas, JButton ventas) {

        JButton[] botones = admin
                ? new JButton[]{libros, clientes, ventas}
                : new JButton[]{libros, clientes, misVentas};
        return tarjetaModulo("Consulta",
                "Busca un libro, un cliente o una venta ya registrada.", botones, botones.length);
    }

    public void refrescar() {
        boolean admin = Sesion.esAdmin();
        String usuario = admin ? null : Sesion.codigo();
        java.math.BigDecimal total = sis.getReporte().totalHoy(usuario);
        int cantidad = sis.getReporte().ventasDeHoy(usuario);
        lblVentasHoy.setText(Dinero.soles(total));
        lblVentasHoyDetalle.setText(cantidad + (cantidad == 1 ? " venta" : " ventas") + " hoy");
        lblStockBajo.setText(String.valueOf(
                sis.getReporte().cantidadStockBajo(Libro.UMBRAL_STOCK_BAJO)));
        Venta ultima = sis.getReporte().ultimaVenta(usuario);
        lblUltimaVenta.setText(ultima == null ? "Sin ventas" : "N.º " + ultima.numeroTexto());
        lblEstado.setText("Fecha: " + Fecha.hoy()
                + (admin ? "     Archivos de datos: " + sis.getCarpeta() : "     Perfil: VENDEDOR"));

modeloUltimas.setRowCount(0);
        ArrayList<Venta> lista = sis.getVentas().ventasDeHoy(usuario);
        for (int i = 0; i < lista.size() && i < 6; i++) {
            Venta v = lista.get(i);
            String nombre = "Público general";
            if (v.clienteRegistrado()) {
                venta.modelo.Cliente c = sis.getClientes().buscar(v.getDni());
                if (c != null) {
                    nombre = c.nombreCompleto();
                }
            }
            modeloUltimas.addRow(new Object[]{
                v.numeroTexto(), nombre, Dinero.texto(v.getTotal()), v.getHora()});
        }

        modeloStock.setRowCount(0);
        ArrayList<Libro> bajos = sis.getReporte().stockBajo(Libro.UMBRAL_STOCK_BAJO);
        for (int i = 0; i < bajos.size() && i < 3; i++) {
            Libro x = bajos.get(i);
            modeloStock.addRow(new Object[]{x.getNombre(), x.getStock()});
        }
    }

    private FrmRegistroVentas registro() {
        if (frmRegistro == null) {
            frmRegistro = new FrmRegistroVentas(sis);
        }
        return frmRegistro;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "navRegistro":
            case "Nueva venta":
                registro().refrescarLibro();
                registro().setLocationRelativeTo(this);
                registro().setVisible(true);
                registro().toFront();
                break;
            case "navMantenimiento":
                mostrarMantenimiento();
                break;
            case "navConsulta":
                new FrmConsulta(sis).setVisible(true);
                break;
            case "navReporte":
                if (FrmReporte.permitido()) {
                    new FrmReporte(sis).setVisible(true);
                } else {
                    Estilo.error(this, "El módulo Reporte es solo para el administrador.");
                }
                break;
            case "Ventas registradas":
            case "Mis ventas registradas":
                registro().getVentasRegistradas().refrescar();
                registro().getVentasRegistradas().setLocationRelativeTo(this);
                registro().getVentasRegistradas().setVisible(true);
                registro().getVentasRegistradas().toFront();
                break;
            case "Libros":
                mostrarMantenimiento("libros");
                break;
            case "Clientes":
                mostrarMantenimiento("clientes");
                break;
            case "Ventas":
            case "Mis ventas":
                case "Ventas por día":
            case "Stock bajo":
            case "Más vendidos":
            case "Ingresos por categoría":
                mostrarReporte(cmd);
                break;
            case "Cerrar sesión":
                cerrarSesion();
                break;
            default:
                break;
        }
    }

    private void mostrarMantenimiento() {
        mostrarMantenimiento("libros");
    }

    private void mostrarMantenimiento(String modulo) {
        if (!FrmMantLibros.permitido()) {
            Estilo.error(this, "El módulo Mantenimiento es solo para el administrador.");
            return;
        }
        sis.grabarTodo();
        if (modulo.equals("clientes")) {
            new FrmMantClientes(sis).setVisible(true);
        } else {
            new FrmMantLibros(sis).setVisible(true);
        }
        refrescar();
    }

    private void mostrarReporte(String que) {
        if (!FrmReporte.permitido()) {

            Estilo.info(this, "Los reportes son del administrador.\n"
                    + "Use el módulo Stock bajo para ver los libros por reponer.");
            return;
        }
        sis.grabarTodo();
        FrmReporte f = new FrmReporte(sis);
        String tipo = tipoReporte(que);
        if (tipo != null) {

            f.combo().setSelectedItem(tipo);
            f.generarReporte();
        }
        f.setLocationRelativeTo(this);
        f.setVisible(true);
        refrescar();
    }

    private static String tipoReporte(String comando) {
        if ("Stock bajo".equals(comando)) {
            return "Stock bajo";
        }
        if ("Más vendidos".equals(comando)) {
            return "Libros mas vendidos";
        }
        if ("Ingresos por categoría".equals(comando)) {
            return "Ingresos por categoria";
        }
        if ("Ventas".equals(comando) || "Mis ventas".equals(comando)
                || "Ventas por día".equals(comando)) {
            return "Ventas por dia";
        }
        return null;
    }

    private void cerrarSesion() {
        sis.grabarTodo();
        if (frmRegistro != null) {
            frmRegistro.getVentasRegistradas().dispose();
            frmRegistro.dispose();
            frmRegistro = null;
        }
        Sesion.cerrar();
        Sistema.reiniciar();
        dispose();
        new Login(Sistema.get()).setVisible(true);
    }
}
