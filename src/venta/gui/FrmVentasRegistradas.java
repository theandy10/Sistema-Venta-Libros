package venta.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import venta.control.Sesion;
import venta.datos.Boleta;
import venta.datos.Sistema;
import venta.modelo.Cliente;
import venta.modelo.DetalleVenta;
import venta.modelo.Venta;
import venta.util.Dinero;
import venta.util.Fecha;
import venta.util.Validador;

public class FrmVentasRegistradas extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final Sistema sis;
    private final FrmRegistroVentas padre;

    private final JTextField txtNumero;
    private final SelectorFecha txtDesde;
    private final SelectorFecha txtHasta;
    private final DefaultTableModel modeloVentas;
    private final JTable tablaVentas;
    private final JLabel lblTotal;

    private final JLabel lblTituloVenta;
    private final DefaultTableModel modeloDetalle;
    private final JTable tablaDetalle;
    private final JLabel lblSubtotal;
    private final JLabel lblDescuento;
    private final JLabel lblTotalVenta;
    private final JLabel lblPagado;
    private final JLabel lblVuelto;
    private final JTextField txtDniCliente;
    private final JPanel comboPago;
    private final PanelVenta.JComboBoxPago cmbPago;
    private final JButton btnAnular;

    private Venta ventaActual;

    public FrmVentasRegistradas(Sistema sis, FrmRegistroVentas padre) {
        this.sis = sis;
        this.padre = padre;
        setTitle("Registro · Ventas registradas");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new Dimension(1180, 680));

        txtNumero = Estilo.campo(10);
        txtDesde = new SelectorFecha(10);
        txtHasta = new SelectorFecha(10);
        JButton btnBuscar = Estilo.primario("Buscar");
        JButton btnTodas = Estilo.secundario("Ver todas");

modeloVentas = Estilo.modelo(new String[]{"N.", "Fecha", "Hora", "Cliente", "Metodo", "Total"});
        tablaVentas = Estilo.tabla(modeloVentas);
        tablaVentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tablaVentas.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaVentas.getColumnModel().getColumn(1).setPreferredWidth(90);
        tablaVentas.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaVentas.getColumnModel().getColumn(3).setPreferredWidth(215);
        tablaVentas.getColumnModel().getColumn(4).setPreferredWidth(110);
        tablaVentas.getColumnModel().getColumn(5).setPreferredWidth(90);
        Estilo.alinearDerecha(tablaVentas, 5);
        lblTotal = Estilo.mensaje("");

JPanel filtros = Estilo.tarjeta("Buscar ventas");
        filtros.setLayout(new BorderLayout(0, 8));

        JPanel campos = Estilo.columna(
                Estilo.fila("Número de venta", txtNumero, 260),
                Estilo.fila("Desde", txtDesde, 260),
                Estilo.fila("Hasta", txtHasta, 260));
        JPanel botonesFiltro = new JPanel(new GridLayout(2, 1, 0, 6));
        botonesFiltro.setOpaque(false);
        botonesFiltro.add(btnBuscar);
        botonesFiltro.add(btnTodas);
        JPanel todas = new JPanel(new BorderLayout());
        todas.setOpaque(false);
        todas.add(botonesFiltro, BorderLayout.NORTH);
        JPanel fila = new JPanel(new BorderLayout(8, 0));
        fila.setOpaque(false);
        fila.add(campos, BorderLayout.CENTER);
        fila.add(todas, BorderLayout.EAST);
        filtros.add(fila, BorderLayout.NORTH);
        filtros.add(Estilo.scroll(tablaVentas), BorderLayout.CENTER);
        filtros.add(lblTotal, BorderLayout.SOUTH);

lblTituloVenta = Estilo.titulo("Venta seleccionada");
        lblTituloVenta.setFont(new java.awt.Font(Estilo.FUENTE, java.awt.Font.BOLD, 20));
        modeloDetalle = Estilo.modelo(
                new String[]{"Código", "Descripción", "Cantidad", "P. unitario", "Subtotal"});
        tablaDetalle = Estilo.tabla(modeloDetalle);
        tablaDetalle.getColumnModel().getColumn(0).setPreferredWidth(70);
        tablaDetalle.getColumnModel().getColumn(1).setPreferredWidth(200);
        Estilo.alinearDerecha(tablaDetalle, 2, 3, 4);
        lblSubtotal = monto("Subtotal");
        lblDescuento = monto("Descuento");
        lblTotalVenta = monto("TOTAL");
        lblPagado = monto("Monto pagado");
        lblVuelto = monto("Vuelto");

        txtDniCliente = Estilo.campo(10);
        cmbPago = new PanelVenta.JComboBoxPago();
        comboPago = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        comboPago.setOpaque(false);
        comboPago.add(Estilo.etiqueta("Cambiar cliente por DNI"));
        comboPago.add(txtDniCliente);
        comboPago.add(Estilo.etiqueta("Metodo de pago"));
        comboPago.add(cmbPago);

JButton btnBoleta = Estilo.compacto("Ver boleta");
        JButton btnModificar = Estilo.compacto("Modificar");
btnAnular = Estilo.peligro("Anular venta");
        JButton btnGrabar = Estilo.primario("Grabar cambios");
        JButton btnVolver = Estilo.volver();

        JPanel pDerecha = Estilo.tarjeta("Detalle de la venta");
        pDerecha.setLayout(new BorderLayout(0, 10));
        pDerecha.add(lblTituloVenta, BorderLayout.NORTH);

        JScrollPane scrollDetalle = Estilo.scroll(tablaDetalle);
        scrollDetalle.setPreferredSize(new Dimension(520, 150));
        pDerecha.add(scrollDetalle, BorderLayout.CENTER);

        JPanel surDerecha = new JPanel(new BorderLayout(0, 8));
        surDerecha.setOpaque(false);
        surDerecha.add(panelMontos(), BorderLayout.NORTH);
        surDerecha.add(comboPago, BorderLayout.CENTER);
        JPanel botones = Estilo.grillaBotones(2, 2, btnBoleta, btnModificar,
                btnAnular, btnGrabar);
        surDerecha.add(botones, BorderLayout.SOUTH);
        pDerecha.add(surDerecha, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filtros, pDerecha);
        split.setResizeWeight(0.52);
        split.setContinuousLayout(true);
        split.setBorder(null);
        split.setDividerSize(8);
        split.setBackground(Estilo.FONDO);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Estilo.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        contenido.add(split, BorderLayout.CENTER);

        JPanel derechaBarra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derechaBarra.setOpaque(false);
        derechaBarra.add(btnVolver);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(Estilo.cabecera("Registro · Ventas registradas", derechaBarra),
                BorderLayout.NORTH);
        getContentPane().add(contenido, BorderLayout.CENTER);

        ActionListener aplicarFiltro = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refrescar();
            }
        };
        btnBuscar.addActionListener(this);
        txtNumero.addActionListener(aplicarFiltro);
        txtDesde.addActionListener(aplicarFiltro);
        txtHasta.addActionListener(aplicarFiltro);
        btnTodas.addActionListener(this);
        btnBoleta.addActionListener(this);
        btnModificar.addActionListener(this);
        btnAnular.addActionListener(this);
        btnGrabar.addActionListener(this);
        btnVolver.addActionListener(this);

        tablaVentas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    cargarVentaSeleccionada();
                }
            }
        });

        if (!Sesion.esAdmin()) {
            btnAnular.setEnabled(false);
            btnAnular.setToolTipText("Solo el administrador puede anular una venta.");
        }

        limpiarDerecha();
        refrescar();
    }

    private JLabel monto(String titulo) {
        JLabel l = new JLabel(titulo);
        l.setFont(new java.awt.Font(Estilo.FUENTE, java.awt.Font.PLAIN, 14));
        l.setForeground(Estilo.SECUNDARIO);
        l.setHorizontalAlignment(JLabel.RIGHT);
        l.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        return l;
    }

    private JPanel panelMontos() {
        JPanel p = new JPanel(new GridLayout(5, 2, 8, 0));
        p.setOpaque(false);
        p.add(Estilo.etiqueta("Subtotal"));
        p.add(lblSubtotal);
        p.add(Estilo.etiqueta("Descuento"));
        p.add(lblDescuento);
        JLabel titulo = Estilo.etiqueta("TOTAL");
        titulo.setFont(new java.awt.Font(Estilo.FUENTE, java.awt.Font.BOLD, 14));
        p.add(titulo);
        lblTotalVenta.setFont(new java.awt.Font(Estilo.FUENTE, java.awt.Font.BOLD, 18));
        lblTotalVenta.setForeground(Estilo.ACENTO);
        p.add(lblTotalVenta);
        p.add(Estilo.etiqueta("Monto pagado"));
        p.add(lblPagado);
        p.add(Estilo.etiqueta("Vuelto"));
        p.add(lblVuelto);
        return p;
    }

    public void refrescar() {
        modeloVentas.setRowCount(0);
        String numero = Validador.limpio(txtNumero.getText());
        String desde = Validador.limpio(txtDesde.getText());
        String hasta = Validador.limpio(txtHasta.getText());
        if (!numero.isEmpty() && !Validador.esEntero(numero)) {
            errorFiltro("El número de venta debe ser numérico.");
            return;
        }
        if (!desde.isEmpty() && !Fecha.esFecha(desde)) {
            errorFiltro("La fecha 'Desde' no es válida. Use dd/mm/aaaa.");
            return;
        }
        if (!hasta.isEmpty() && !Fecha.esFecha(hasta)) {
            errorFiltro("La fecha 'Hasta' no es válida. Use dd/mm/aaaa.");
            return;
        }
        LocalDate f1 = Fecha.aFecha(desde);
        LocalDate f2 = Fecha.aFecha(hasta);
        if (f1 != null && f2 != null && f1.isAfter(f2)) {
            errorFiltro("La fecha 'Desde' no puede ser mayor que la fecha 'Hasta'.");
            return;
        }
        ArrayList<Venta> lista;
        if (!numero.isEmpty()) {
            lista = new ArrayList<Venta>();
            Venta v = sis.getVentas().buscar(Validador.entero(numero, -1));
            if (v != null && esVisible(v)) {
                lista.add(v);
            }
        } else if (!desde.isEmpty() || !hasta.isEmpty()) {
            lista = filtrarPorPerfil(sis.getVentas().ventasEntre(desde, hasta));
        } else {
            lista = filtrarPorPerfil(sis.getVentas().todasPorFechaDescendente());
        }
        for (int i = 0; i < lista.size(); i++) {
            Venta v = lista.get(i);
            Cliente c = v.clienteRegistrado() ? sis.getClientes().buscar(v.getDni()) : null;
            modeloVentas.addRow(new Object[]{
                v.numeroTexto(), v.getFecha(), v.getHora(),
                c == null ? "Público general" : c.nombreCompleto(),
                v.getMetodo(), Dinero.texto(v.getTotal())});
        }
        lblTotal.setText(lista.isEmpty()
                ? "No hay ventas que cumplan el filtro."
                : lista.size() + " venta(s) · total " + Dinero.soles(sis.getReporte().totalVentas(lista)));
    }

    private void errorFiltro(String mensaje) {
        lblTotal.setText(mensaje);
        limpiarDerecha();
    }

    private ArrayList<Venta> filtrarPorPerfil(ArrayList<Venta> lista) {
        if (Sesion.esAdmin()) {
            return lista;
        }
        ArrayList<Venta> r = new ArrayList<Venta>();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getUsuario().equals(Sesion.codigo())) {
                r.add(lista.get(i));
            }
        }
        return r;
    }

    private boolean esVisible(Venta v) {
        return Sesion.esAdmin() || v.getUsuario().equals(Sesion.codigo());
    }

    private void cargarVentaSeleccionada() {
        int fila = tablaVentas.getSelectedRow();
        if (fila < 0) {
            limpiarDerecha();
            return;
        }
        String numero = (String) modeloVentas.getValueAt(fila, 0);
        Venta v = sis.getVentas().buscar(Validador.entero(numero, -1));
        ventaActual = v;
        if (v == null) {
            limpiarDerecha();
            return;
        }
        Cliente c = v.clienteRegistrado() ? sis.getClientes().buscar(v.getDni()) : null;
        lblTituloVenta.setText("Venta N. " + v.numeroTexto());
        modeloDetalle.setRowCount(0);
        ArrayList<DetalleVenta> d = sis.getDetalles().detalleDeVenta(v.getNumero());
        for (int i = 0; i < d.size(); i++) {
            DetalleVenta x = d.get(i);
modeloDetalle.addRow(new Object[]{
                x.getCodigoLibro(), x.getNombreLibro(), x.getCantidad(),
                Dinero.texto(x.getPrecioUnitario()), Dinero.texto(x.getSubtotal())});
        }
        lblSubtotal.setText(Dinero.soles(v.getSubtotal()));
        lblDescuento.setText(Dinero.soles(v.getDescuento()));
        lblTotalVenta.setText(Dinero.soles(v.getTotal()));
        lblPagado.setText(Dinero.soles(v.getMontoPagado()));
        lblVuelto.setText(Dinero.soles(v.getVuelto()));
        txtDniCliente.setText(v.clienteRegistrado() ? v.getDni() : "");
        cmbPago.setSelectedItem(v.getMetodo());
    }

    private void limpiarDerecha() {
        ventaActual = null;
        lblTituloVenta.setText("Venta seleccionada");
        modeloDetalle.setRowCount(0);
        lblSubtotal.setText("");
        lblDescuento.setText("");
        lblTotalVenta.setText("");
        lblPagado.setText("");
        lblVuelto.setText("");
        txtDniCliente.setText("");
    }

    private void verBoleta() {
        if (ventaActual == null) {
            Estilo.error(this, "Seleccione una venta del listado.");
            return;
        }
        Cliente c = ventaActual.clienteRegistrado()
                ? sis.getClientes().buscar(ventaActual.getDni()) : null;
        String texto = Boleta.generar(ventaActual, c,
                sis.getDetalles().detalleDeVenta(ventaActual.getNumero()));
        new DlgBoleta(ventaActual, texto).setVisible(true);
    }

    private void modificar() {
        if (ventaActual == null) {
            Estilo.error(this, "Seleccione una venta del listado.");
            return;
        }
        String dni = Validador.limpio(txtDniCliente.getText());
        if (!dni.isEmpty()) {
            if (!Validador.dni(dni)) {
                Estilo.error(this, "El DNI debe tener 8 dígitos numéricos.");
                return;
            }
            if (!sis.getClientes().existeDni(dni)) {
                Estilo.error(this, "El DNI " + dni + " no está registrado.");
                return;
            }
            ventaActual.setDni(dni);
        } else {
            ventaActual.setDni(Venta.PUBLICO);
        }
        ventaActual.setMetodo(cmbPago.getMetodo());
        int numero = ventaActual.getNumero();
        sis.grabarTodo();

        refrescar();
        seleccionar(numero);
        Estilo.info(this, "Venta N. " + String.format(java.util.Locale.US, "%06d", numero)
                + " actualizada.");
    }

    private void seleccionar(int numero) {
        String texto = String.format(java.util.Locale.US, "%06d", numero);
        for (int i = 0; i < modeloVentas.getRowCount(); i++) {
            if (texto.equals(modeloVentas.getValueAt(i, 0))) {
                tablaVentas.setRowSelectionInterval(i, i);
                return;
            }
        }
        limpiarDerecha();
    }

    private void anular() {
        if (ventaActual == null) {
            Estilo.error(this, "Seleccione una venta del listado.");
            return;
        }
        if (!Sesion.esAdmin()) {
            Estilo.error(this, "Solo el administrador puede anular una venta.");
            return;
        }
        boolean acepta = Estilo.confirmar(this,
                "¿Anular la venta N. " + ventaActual.numeroTexto() + "?\n"
                + "Se eliminará de la lista y se devolverá el stock de los libros.");
        if (!acepta) {
            return;
        }
        sis.getVentas().anular(ventaActual.getNumero());
        sis.grabarTodo();
        limpiarDerecha();
        refrescar();
        if (padre != null) {
            padre.refrescarLibro();
        }
        Estilo.info(this, "La venta fue anulada y el stock devuelto.");
    }

    private void verTodas() {
        txtNumero.setText("");
        txtDesde.setText("");
        txtHasta.setText("");
        limpiarDerecha();
        refrescar();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "Buscar":
                refrescar();
                break;
            case "Ver todas":
                verTodas();
                break;
            case "Ver boleta":
                verBoleta();
                break;
            case "Modificar":
                modificar();
                break;
            case "Anular venta":
                anular();
                break;
            case "Grabar cambios":
                if (sis.grabarTodo()) {
                    Estilo.info(this, "Los cambios se grabaron en los archivos de texto.");
                } else {
                    Estilo.error(this, "No se pudieron escribir los archivos.\nRevise la carpeta "
                            + sis.getCarpeta());
                }
                break;
            case "Volver":
                dispose();
                break;
            default:
                break;
        }
    }
}
