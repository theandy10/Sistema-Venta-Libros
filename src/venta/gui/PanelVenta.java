package venta.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import venta.control.Sesion;
import venta.datos.Boleta;
import venta.datos.Sistema;
import venta.modelo.Cliente;
import venta.modelo.DetalleVenta;
import venta.modelo.Libro;
import venta.modelo.Venta;
import venta.util.Dinero;
import venta.util.Fecha;
import venta.util.Validador;

public class PanelVenta extends JPanel implements ActionListener, VentaListener {

    private static final long serialVersionUID = 1L;

    private final Sistema sis;
    private final ArrayList<DetalleVenta> detalle = new ArrayList<DetalleVenta>();
    private Cliente cliente;

    private final CampoSugerido txtDni;
    private final JTextField txtNombre;
    private final JTextField txtCelular;
    private final JCheckBox chkRapida;

    private final DefaultTableModel modeloDetalle;
    private final JTable tablaDetalle;

private final JTextField txtSubtotal;
    private final JTextField txtDescuento;
    private final JTextField txtTotal;
    private final JComboBoxPago cmbPago;
    private final JTextField txtMontoPagado;
    private final JTextField txtVuelto;
private final JLabel lblMensaje;
    private final JButton btnQuitar;

    private Runnable alRegistrar;

    public PanelVenta(Sistema sis) {
        this.sis = sis;
        this.cliente = null;
        setLayout(new BorderLayout(0, 10));
        setBackground(Estilo.FONDO);

        txtDni = new CampoSugerido(9);
        txtDni.setAlElegir(new Runnable() {
            @Override
            public void run() {
                buscarCliente();
            }
        });
        refrescarSugerencias();
        txtNombre = Estilo.soloLectura(18);
        txtCelular = Estilo.soloLectura(10);
        chkRapida = new JCheckBox("Venta rápida (público general, sin cliente registrado)");
        chkRapida.setFont(new Font(Estilo.FUENTE, Font.PLAIN, 13));
        chkRapida.setBackground(Estilo.TARJETA);
        chkRapida.setForeground(Estilo.SECUNDARIO);
        chkRapida.setFocusPainted(false);

        JButton btnBuscar = Estilo.secundario("Buscar");
        btnBuscar.addActionListener(this);

JPanel pCliente = Estilo.tarjeta("Cliente de la venta");
        pCliente.setLayout(new BorderLayout(10, 8));

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fila.setOpaque(false);
        fila.add(Estilo.conEtiqueta("DNI del cliente", txtDni));
        fila.add(Estilo.conEtiqueta("Cliente", txtNombre));
        fila.add(Estilo.conEtiqueta("Celular", txtCelular));
        pCliente.add(fila, BorderLayout.CENTER);
        JPanel opciones = new JPanel(new BorderLayout());
        opciones.setOpaque(false);
        opciones.add(chkRapida, BorderLayout.WEST);
        opciones.add(btnBuscar, BorderLayout.EAST);
        pCliente.add(opciones, BorderLayout.SOUTH);

        lblMensaje = Estilo.mensaje("");
        lblMensaje.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 4, 2, 4));

        modeloDetalle = Estilo.modelo(
                new String[]{"Código", "Descripción", "Cantidad", "P. unitario", "Subtotal"}, 2);
        tablaDetalle = Estilo.tabla(modeloDetalle);
        tablaDetalle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDetalle.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaDetalle.getColumnModel().getColumn(1).setPreferredWidth(200);
        Estilo.alinearDerecha(tablaDetalle, 2, 3, 4);
JButton btnQuitar = Estilo.peligro("Quitar seleccionado");
        this.btnQuitar = btnQuitar;
        btnQuitar.addActionListener(this);

        tablaDetalle.getColumnModel().getColumn(2).setCellEditor(new EditorCantidad());
        JPanel pDetalle = Estilo.tarjeta("Detalle de la venta");
        pDetalle.setLayout(new BorderLayout(0, 8));
        pDetalle.add(Estilo.scroll(tablaDetalle), BorderLayout.CENTER);
        JPanel accionesDetalle = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        accionesDetalle.setOpaque(false);
        accionesDetalle.add(Estilo.etiqueta(
                "Doble clic en Cantidad para cambiarla con las flechas"));
        accionesDetalle.add(btnQuitar);
        pDetalle.add(accionesDetalle, BorderLayout.SOUTH);
        tablaDetalle.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (!e.getValueIsAdjusting()) {
                            btnQuitar.setEnabled(tablaDetalle.getSelectedRow() >= 0);
                        }
                    }
                });

        txtSubtotal = Estilo.monto(8, false);
        txtDescuento = Estilo.monto(8, true);
        txtTotal = Estilo.monto(9, false);
        txtTotal.setFont(new Font(Estilo.FUENTE, Font.BOLD, 16));
        txtTotal.setForeground(Estilo.ACENTO);
        cmbPago = new JComboBoxPago();
        txtMontoPagado = Estilo.monto(8, true);
txtVuelto = Estilo.monto(8, false);

        JButton btnLimpiar = Estilo.secundario("Limpiar");
        JButton btnRegistrar = Estilo.primario("Registrar venta");
        btnLimpiar.addActionListener(this);
        btnRegistrar.addActionListener(this);

JPanel pCobro = Estilo.tarjeta("Cobro");
        pCobro.setLayout(new BorderLayout(0, 10));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        fila1.setOpaque(false);
        fila1.add(Estilo.conEtiqueta("Subtotal", txtSubtotal));
        fila1.add(Estilo.conEtiqueta("Descuento", txtDescuento));
        fila1.add(Estilo.conEtiqueta("TOTAL", txtTotal));
        fila1.add(Estilo.conEtiqueta("Método de pago", cmbPago));
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        fila2.setOpaque(false);
        fila2.add(Estilo.conEtiqueta("Monto pagado", txtMontoPagado));
        fila2.add(Estilo.conEtiqueta("Vuelto", txtVuelto));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnLimpiar);
        botones.add(btnRegistrar);
        pCobro.add(fila1, BorderLayout.NORTH);
        pCobro.add(fila2, BorderLayout.CENTER);
        pCobro.add(botones, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(pDetalle, BorderLayout.CENTER);
        centro.add(pCobro, BorderLayout.SOUTH);

JPanel cabecera = new JPanel(new BorderLayout(0, 8));
        cabecera.setOpaque(false);
        cabecera.add(pCliente, BorderLayout.NORTH);
        cabecera.add(lblMensaje, BorderLayout.SOUTH);
        add(cabecera, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        DocumentListener recalculo = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                recalcular();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                recalcular();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                recalcular();
            }
        };
        txtDescuento.getDocument().addDocumentListener(recalculo);
        txtMontoPagado.getDocument().addDocumentListener(recalculo);
        chkRapida.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarVentaRapida();
            }
        });

        cmbPago.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean efectivo = cmbPago.esEfectivo();
                txtMontoPagado.setEditable(efectivo);
                if (!efectivo) {

                    txtMontoPagado.setText(Dinero.texto(totalActual()));
                }
                recalcular();
            }
        });

        limpiar();
    }

    public void refrescarSugerencias() {
        ArrayList<String[]> pares = sis.getClientes().paraSugerencias();
        ArrayList<String> aMostrar = new ArrayList<String>();
        ArrayList<String> aEscribir = new ArrayList<String>();
        for (int i = 0; i < pares.size(); i++) {
            aEscribir.add(pares.get(i)[0]);
            aMostrar.add(pares.get(i)[1]);
        }
        txtDni.setSugerencias(aMostrar, aEscribir);
    }

    public void ejecutar(String comando) {
        actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, comando));
    }

    public void setAlRegistrar(Runnable r) {
        this.alRegistrar = r;
    }

    public void limpiar() {
        detalle.clear();
        cliente = null;
        chkRapida.setSelected(false);
        txtDni.setEnabled(true);
        txtDni.setEditable(true);
        txtDni.setText("");
        txtNombre.setText("");
        txtCelular.setText("");
        txtDescuento.setText("");
        txtMontoPagado.setText("");
        cmbPago.setSelectedIndex(0);
        modeloDetalle.setRowCount(0);
        recalcular();
    }

    private void cambiarVentaRapida() {
        boolean rapida = chkRapida.isSelected();
        txtDni.setEnabled(!rapida);
        txtDni.setEditable(!rapida);
        txtDni.setText("");
        txtNombre.setText("");
        txtCelular.setText("");
        cliente = null;
    }

    @Override
    public void agregarItem(Libro libro, int cantidad, BigDecimal precioAplicado) {
        for (int i = 0; i < detalle.size(); i++) {
            DetalleVenta d = detalle.get(i);
            if (d.getCodigoLibro() == libro.getCodigo()) {

                d.setCantidad(d.getCantidad() + cantidad);
                d.setPrecioUnitario(precioAplicado);
                listarDetalle();
                return;
            }
        }
        detalle.add(new DetalleVenta(0, libro.getCodigo(), libro.getNombre(),
                cantidad, precioAplicado));
        listarDetalle();
    }

    @Override
    public int cantidadEnDetalle(int codigoLibro) {
        for (int i = 0; i < detalle.size(); i++) {
            if (detalle.get(i).getCodigoLibro() == codigoLibro) {
                return detalle.get(i).getCantidad();
            }
        }
        return 0;
    }

    @Override
    public String nombreCliente() {
        if (chkRapida.isSelected() || cliente == null) {
            return "Público general";
        }
        return cliente.nombreCompleto() + " (DNI " + cliente.getDni() + ")";
    }

    private void aviso(String texto) {
        lblMensaje.setText(texto);
        lblMensaje.setForeground(Estilo.PELIGRO);
    }

    private void info(String texto) {
        lblMensaje.setText(texto);
        lblMensaje.setForeground(Estilo.TEXTO);
    }

    private void buscarCliente() {
        String dni = Validador.limpio(txtDni.getText());
        if (dni.isEmpty()) {
            aviso("Ingrese el DNI del cliente.");
            return;
        }
        if (!Validador.dni(dni)) {
            aviso("El DNI debe tener 8 dígitos numéricos.");
            txtDni.selectAll();
            return;
        }
        Cliente x = sis.getClientes().buscar(dni);
        if (x == null) {
aviso("El DNI " + dni + " no está registrado. Use la opción "
                    + "Venta rápida o registre al cliente desde Gestión.");
            txtDni.setText("");
            return;
        }
        chkRapida.setSelected(false);
        cliente = x;
        txtNombre.setText(x.nombreCompleto());
        txtCelular.setText(String.valueOf(x.getCelular()));
    }

private void quitar() {
        int fila = tablaDetalle.getSelectedRow();
        if (fila < 0) {
            aviso("Seleccione una fila del detalle.");
            return;
        }
        detalle.remove(fila);
        listarDetalle();
    }

    private int disponibleDe(DetalleVenta d) {
        int otras = 0;
        for (int i = 0; i < detalle.size(); i++) {
            DetalleVenta o = detalle.get(i);
            if (o.getCodigoLibro() == d.getCodigoLibro() && o != d) {
                otras += o.getCantidad();
            }
        }
        Libro x = sis.getLibros().buscar(d.getCodigoLibro());
        return (x == null ? 0 : x.getStock()) - otras;
    }

    private boolean aplicarCantidad(int indice, int cantidad) {
        if (indice < 0 || indice >= detalle.size() || cantidad < 1) {
            return false;
        }
        DetalleVenta d = detalle.get(indice);
        int disponible = disponibleDe(d);
        if (cantidad > disponible) {
            aviso("No hay stock suficiente de " + d.getNombreLibro()
                    + ". Disponible: " + Math.max(0, disponible) + " unidad(es).");
            return false;
        }
        d.setCantidad(cantidad);

        modeloDetalle.setValueAt(cantidad, indice, 2);
        modeloDetalle.setValueAt(Dinero.texto(d.getSubtotal()), indice, 4);
        recalcular();
        info(d.getNombreLibro() + ": " + cantidad + " unidad(es) x "
                + Dinero.texto(d.getPrecioUnitario()) + ".");
        return true;
    }

    private class EditorCantidad extends AbstractCellEditor implements TableCellEditor {

        private final JSpinner spinner;

        EditorCantidad() {
            spinner = Estilo.cantidad(1, 9999);
        }

        @Override
        public Component getTableCellEditorComponent(JTable tabla, Object valor,
                                                     boolean esSeleccion, int fila, int columna) {
            int indice = tabla.convertRowIndexToModel(fila);
            if (indice >= 0 && indice < detalle.size()) {
                DetalleVenta d = detalle.get(indice);

                spinner.setModel(new SpinnerNumberModel(d.getCantidad(), 1,
                        Math.max(1, disponibleDe(d)), 1));
            }
            return spinner;
        }

        @Override
        public Object getCellEditorValue() {
            int fila = tablaDetalle.getEditingRow();
            int indice = fila < 0 ? -1 : tablaDetalle.convertRowIndexToModel(fila);
            int cantidad = ((Number) spinner.getValue()).intValue();
            if (!aplicarCantidad(indice, cantidad) && indice >= 0
                    && indice < detalle.size()) {

                DetalleVenta d = detalle.get(indice);
                modeloDetalle.setValueAt(d.getCantidad(), indice, 1);
                return d.getCantidad();
            }
            return cantidad;
        }
    }

    private void registrar() {
        if (detalle.isEmpty()) {
            aviso("La venta debe tener al menos un libro en el detalle.");
            return;
        }
        if (cliente == null && !chkRapida.isSelected()) {
            aviso("Busque un cliente por DNI o marque la opción de venta rápida.");
            return;
        }
        BigDecimal subtotal = subtotalActual();
BigDecimal descuento = Dinero.de(txtDescuento.getText());
        if (descuento == null) {
            aviso("El descuento debe ser numérico.");
            return;
        }
        if (descuento.compareTo(Dinero.CERO) < 0) {
            aviso("El descuento no puede ser negativo.");
            return;
        }
        if (Dinero.mayor(descuento, subtotal)) {
aviso("El descuento no puede ser mayor al subtotal ("
                    + Dinero.soles(subtotal) + ").");
            return;
        }
        BigDecimal total = Dinero.resta(subtotal, descuento);
        String metodo = cmbPago.getMetodo();
        BigDecimal pagado = total;
if (cmbPago.esEfectivo()) {
            if (txtMontoPagado.getText().trim().isEmpty()) {
                aviso("Escriba el monto que paga el cliente.");
                return;
            }
            BigDecimal escrito = Dinero.de(txtMontoPagado.getText());
            if (escrito == null) {
                aviso("El monto pagado debe ser numérico.");
                return;
            }
            pagado = escrito;
            if (!Dinero.mayorOIgual(pagado, total)) {
aviso("El monto pagado (" + Dinero.soles(pagado)
                        + ") es menor al total de la venta (" + Dinero.soles(total) + ").");
                return;
            }
        }

        String dni = chkRapida.isSelected() ? Venta.PUBLICO : cliente.getDni();
        Venta v = sis.getVentas().registrar(
                Sesion.codigo(), dni, new ArrayList<DetalleVenta>(detalle),
                Fecha.hoy(), Fecha.ahora(), descuento, metodo, pagado);

        boolean ok = sis.grabarTodo();
        Cliente c = v.clienteRegistrado() ? sis.getClientes().buscar(v.getDni()) : null;
        ArrayList<DetalleVenta> lineas = sis.getDetalles().detalleDeVenta(v.getNumero());
        String textoBoleta = Boleta.generar(v, c, lineas);
        Boleta.guardar(textoBoleta, sis.getCarpeta(), v.getNumero());

limpiar();
        info("Venta N. " + v.numeroTexto() + " registrada. Boleta N. "
                + v.numeroTexto() + " guardada en datos/boletas.");
        if (alRegistrar != null) {
            alRegistrar.run();
        }
        new DlgBoleta(v, textoBoleta).setVisible(true);
        if (!ok) {
            Estilo.error(this, "La venta se registró, pero algún archivo no se pudo escribir.\n"
                    + "Revise la carpeta " + sis.getCarpeta());
        }
    }

private void listarDetalle() {
        modeloDetalle.setRowCount(0);
        for (int i = 0; i < detalle.size(); i++) {
            DetalleVenta d = detalle.get(i);
            modeloDetalle.addRow(new Object[]{
                d.getCodigoLibro(), d.getNombreLibro(), d.getCantidad(),
                Dinero.texto(d.getPrecioUnitario()), Dinero.texto(d.getSubtotal())});
        }

        tablaDetalle.clearSelection();
        btnQuitar.setEnabled(false);
        recalcular();
    }

    private BigDecimal subtotalActual() {
        BigDecimal t = Dinero.CERO;
        for (int i = 0; i < detalle.size(); i++) {
            t = Dinero.suma(t, detalle.get(i).getSubtotal());
        }
        return t;
    }

    private BigDecimal descuentoActual() {
        BigDecimal d = Dinero.de(txtDescuento.getText());
        return d == null ? Dinero.CERO : d;
    }

    private BigDecimal totalActual() {
        BigDecimal d = descuentoActual();
        return Dinero.resta(subtotalActual(), d);
    }

    private void recalcular() {
        BigDecimal subtotal = subtotalActual();
        BigDecimal total = totalActual();
        txtSubtotal.setText(Dinero.texto(subtotal));
        txtTotal.setText(Dinero.texto(total));
if (!cmbPago.esEfectivo()) {
            txtMontoPagado.setText(Dinero.texto(total));
            txtVuelto.setText(Dinero.texto(Dinero.CERO));
            txtVuelto.setFont(new Font(Estilo.FUENTE, Font.PLAIN, Estilo.TAMANO));
            return;
        }

        if (txtMontoPagado.getText().trim().isEmpty()) {
            txtVuelto.setText(Dinero.texto(Dinero.CERO));
            txtVuelto.setFont(new Font(Estilo.FUENTE, Font.PLAIN, Estilo.TAMANO));
            return;
        }
        BigDecimal pagado = Dinero.de(txtMontoPagado.getText());
        BigDecimal vuelto = pagado == null ? Dinero.CERO : Dinero.resta(pagado, total);
        txtVuelto.setText(Dinero.texto(vuelto));

        txtVuelto.setFont(new Font(Estilo.FUENTE,
                Dinero.mayorOIgual(vuelto, Dinero.CERO) ? Font.PLAIN : Font.BOLD,
                Estilo.TAMANO));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "Buscar":
                buscarCliente();
                break;
case "Quitar seleccionado":
                quitar();
                break;
            case "Registrar venta":
                registrar();
                break;
            case "Limpiar":
                limpiar();
                break;
            default:
                break;
        }
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    static class JComboBoxPago extends javax.swing.JComboBox<String> {
        private static final long serialVersionUID = 1L;

        JComboBoxPago() {
            super(new String[]{"EFECTIVO", "TARJETA", "YAPE / PLIN"});
setFont(new Font(Estilo.FUENTE, Font.PLAIN, 14));
            setBackground(Estilo.TARJETA);
            setPreferredSize(new Dimension(150, Estilo.ALTO_CAMPO));
        }

        String getMetodo() {
            return (String) getSelectedItem();
        }

        boolean esEfectivo() {
            return Venta.EFECTIVO.equals(getMetodo());
        }
    }
}
