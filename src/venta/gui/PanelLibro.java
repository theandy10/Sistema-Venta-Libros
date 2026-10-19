package venta.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import venta.datos.Sistema;
import venta.modelo.Libro;
import venta.util.Dinero;
import venta.util.Validador;

public class PanelLibro extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final Sistema sis;
    private VentaListener listener;
    private Libro libroActual;

private final JTextField txtBuscarCodigo;
    private final JTextField txtBuscarNombre;
    private final JButton btnLimpiarBusqueda;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private ArrayList<Libro> resultados = new ArrayList<Libro>();

    private final JLabel lblTitulo;
    private final JLabel lblSubtitulo;
    private final JLabel lblCodigo;
    private final JLabel lblStockBajo;
    private final JTextField txtPrecioVenta;
    private final JTextField txtPrecioMinimo;
    private final JTextField txtStock;
    private final JTextField txtPrecioCompra;

private final JLabel lblResumen;
    private final JTextField txtCantidad;
    private final JTextField txtPrecioAplicado;
    private final JTextField txtSubtotalItem;
    private final JLabel lblNota;
    private final JButton btnAgregar;
    private final JLabel lblPortada;
    private final JPanel ficha;
    private final JPanel sur;
    private final JPanel vender;

    public PanelLibro(Sistema sis) {
        this.sis = sis;
        this.libroActual = null;
        setLayout(new BorderLayout(0, 10));
        setBackground(Estilo.FONDO);

        JLabel lblTituloPanel = Estilo.texto("Libro a vender");
        lblTituloPanel.setFont(new java.awt.Font(Estilo.FUENTE, java.awt.Font.BOLD, 14));

        txtBuscarCodigo = Estilo.campo(7);
        txtBuscarNombre = Estilo.campo(18);
        btnLimpiarBusqueda = Estilo.secundario("Limpiar búsqueda");
        btnLimpiarBusqueda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarBusqueda();
            }
        });

        modelo = Estilo.modelo(new String[]{"Código", "Libro", "Autor", "Stock", "P. venta"});
        tabla = Estilo.tabla(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(230);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(80);
        Estilo.alinearDerecha(tabla, 3, 4);

        JPanel pBuscar = new JPanel(new BorderLayout(8, 8));
        pBuscar.setBackground(Estilo.FONDO);
        pBuscar.setBorder(BorderFactory.createTitledBorder("Buscar libro"));

        JPanel campos = Estilo.columna(
                Estilo.fila("Código", txtBuscarCodigo, 420),
                Estilo.fila("Nombre del libro", txtBuscarNombre, 420));
        JPanel botonLimpiar = new JPanel(new BorderLayout());
        botonLimpiar.setOpaque(false);
        botonLimpiar.add(btnLimpiarBusqueda, BorderLayout.NORTH);
        JPanel barra = new JPanel(new BorderLayout(8, 0));
        barra.setOpaque(false);
        barra.add(campos, BorderLayout.CENTER);
        barra.add(botonLimpiar, BorderLayout.EAST);

        pBuscar.add(barra, BorderLayout.NORTH);
        pBuscar.add(Estilo.scroll(tabla), BorderLayout.CENTER);

        lblTitulo = Estilo.ficha(" ");
        lblSubtitulo = Estilo.texto("Seleccione un libro de la lista");
        lblSubtitulo.setHorizontalAlignment(JLabel.CENTER);
        lblSubtitulo.setForeground(Estilo.SECUNDARIO);

        lblCodigo = new JLabel(" ", JLabel.CENTER);
        lblCodigo.setFont(new java.awt.Font(Estilo.FUENTE, java.awt.Font.PLAIN, 11));
        lblCodigo.setForeground(Estilo.SECUNDARIO);
        lblStockBajo = new JLabel("STOCK BAJO", JLabel.CENTER);
        lblStockBajo.setFont(new java.awt.Font(Estilo.FUENTE, java.awt.Font.BOLD, 12));
        lblStockBajo.setVisible(false);

        txtPrecioVenta = Estilo.monto(8, false);
        txtPrecioMinimo = Estilo.monto(8, false);
        txtStock = Estilo.soloLectura(8);
        txtPrecioCompra = Estilo.monto(8, false);

ficha = Estilo.tarjeta("Ficha del libro");

ficha.setLayout(new BorderLayout(10, 0));
JPanel nombres = new JPanel();
        nombres.setOpaque(false);
        nombres.setLayout(new BoxLayout(nombres, BoxLayout.Y_AXIS));
        nombres.add(lblTitulo);
        nombres.add(Box.createVerticalStrut(2));
        nombres.add(lblSubtitulo);
        nombres.add(lblCodigo);
        nombres.add(Box.createVerticalStrut(4));
        nombres.add(lblStockBajo);

        lblPortada = new JLabel("Portada", JLabel.CENTER);
        lblPortada.setPreferredSize(new Dimension(74, 76));
        lblPortada.setOpaque(true);
        lblPortada.setBackground(Estilo.LECTURA);
        lblPortada.setForeground(Estilo.SECUNDARIO);
        lblPortada.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
        lblPortada.setFont(new java.awt.Font(Estilo.FUENTE, java.awt.Font.ITALIC, 11));

        JPanel valores = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        valores.setOpaque(false);
        valores.add(Estilo.conEtiqueta("Precio de venta", txtPrecioVenta));
        valores.add(Estilo.conEtiqueta("Precio minimo de venta", txtPrecioMinimo));
        valores.add(Estilo.conEtiqueta("Stock disponible", txtStock));
        valores.add(Estilo.conEtiqueta("Precio de compra", txtPrecioCompra));
        JPanel centroFicha = new JPanel(new BorderLayout(0, 4));
        centroFicha.setOpaque(false);
        centroFicha.add(nombres, BorderLayout.NORTH);
        centroFicha.add(valores, BorderLayout.CENTER);
        ficha.add(lblPortada, BorderLayout.WEST);
        ficha.add(centroFicha, BorderLayout.CENTER);

        lblResumen = Estilo.texto("Seleccione un libro de la lista");
        lblResumen.setHorizontalAlignment(JLabel.CENTER);
        lblResumen.setFont(new java.awt.Font(Estilo.FUENTE, java.awt.Font.BOLD, 14));

        txtCantidad = Estilo.monto(4, true);
        txtCantidad.setText("1");
        txtPrecioAplicado = Estilo.monto(8, true);
        txtSubtotalItem = Estilo.monto(8, false);

        lblNota = Estilo.texto("Se valida que la cantidad no pase del stock y que el precio no baje del minimo.");
        lblNota.setFont(new java.awt.Font(Estilo.FUENTE, java.awt.Font.ITALIC, 12));
        lblNota.setForeground(Estilo.SECUNDARIO);

        JButton btnCancelar = Estilo.secundario("Cancelar");
        btnAgregar = Estilo.primario("Agregar a la venta");
        btnAgregar.setEnabled(false);

        vender = new JPanel();
        vender.setLayout(new BoxLayout(vender, BoxLayout.Y_AXIS));
        vender.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(), "Vas a vender",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP),
                BorderFactory.createEmptyBorder(2, 8, 8, 8)));
        JPanel resumen = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        resumen.setOpaque(false);
        resumen.add(lblResumen);
        JPanel datos = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        datos.setOpaque(false);
        datos.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        datos.add(campoVender("Cantidad", txtCantidad));
        datos.add(campoVender("Precio aplicado", txtPrecioAplicado));
        datos.add(campoVender("Subtotal", txtSubtotalItem));
        JPanel nota = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        nota.setOpaque(false);
        nota.add(lblNota);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnCancelar);
        botones.add(btnAgregar);
        vender.add(resumen);
        vender.add(datos);
        vender.add(nota);
        vender.add(botones);

add(lblTituloPanel, BorderLayout.NORTH);
        sur = new JPanel(new BorderLayout(0, 8));
sur.add(ficha, BorderLayout.NORTH);
        sur.add(vender, BorderLayout.CENTER);

        pBuscar.setPreferredSize(new Dimension(420, 230));
        add(pBuscar, BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);

        DocumentListener filtro = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        };
        txtBuscarCodigo.getDocument().addDocumentListener(filtro);
        txtBuscarNombre.getDocument().addDocumentListener(filtro);
        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    cargarFicha();
                }
            }
        });
        DocumentListener recalculo = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calcularSubtotal();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                calcularSubtotal();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                calcularSubtotal();
            }
        };
        txtCantidad.getDocument().addDocumentListener(recalculo);
        txtPrecioAplicado.getDocument().addDocumentListener(recalculo);

        btnAgregar.addActionListener(this);
        btnCancelar.addActionListener(this);
        filtrar();
        limpiarFicha();
    }

    private JPanel campoVender(String titulo, JTextField campo) {
        JPanel p = new JPanel(new BorderLayout(4, 2));
        p.setOpaque(false);
JLabel l = Estilo.etiqueta(titulo);
        p.add(l, BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    public void setVentaListener(VentaListener l) {
        this.listener = l;
    }

    public void refrescar() {
        filtrar();
    }

    public void refrescarResumen() {
        if (libroActual == null) {
            lblResumen.setText("Seleccione un libro de la lista");
            return;
        }
        String quien = listener == null ? "Público general" : listener.nombreCliente();
        lblResumen.setText(libroActual.getNombre() + "  a  " + quien);
    }

    private void filtrar() {
        String cod = Validador.limpio(txtBuscarCodigo.getText());
        String nom = Validador.limpio(txtBuscarNombre.getText()).toLowerCase();
        ArrayList<Libro> todos = sis.getLibros().todos();
        resultados = new ArrayList<Libro>();
        for (int i = 0; i < todos.size(); i++) {
            Libro x = todos.get(i);
            boolean okCod = cod.isEmpty() || String.valueOf(x.getCodigo()).contains(cod);
            boolean okNom = nom.isEmpty()
                    || x.getNombre().toLowerCase().contains(nom)
                    || x.getAutor().toLowerCase().contains(nom);
            if (okCod && okNom) {
                resultados.add(x);
            }
        }
        modelo.setRowCount(0);
        for (int i = 0; i < resultados.size(); i++) {
            Libro x = resultados.get(i);
            modelo.addRow(new Object[]{
                x.getCodigo(), x.getNombre(), x.getAutor(), x.getStock(),
                Dinero.texto(x.getPrecioVenta())});
        }
        limpiarFicha();
    }

    private void limpiarBusqueda() {
        txtBuscarCodigo.setText("");
        txtBuscarNombre.setText("");
        filtrar();
        txtBuscarNombre.requestFocusInWindow();
    }

    private void cargarFicha() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            limpiarFicha();
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(fila);
        int codigo = (Integer) modelo.getValueAt(filaModelo, 0);
        Libro x = sis.getLibros().buscar(codigo);
        if (x == null) {
            limpiarFicha();
            return;
        }
        libroActual = x;
        pintarFicha(x);

        ficha.setVisible(true);
        refrescarResumen();
info("Máximo " + x.getStock() + " unidad(es). Precio mínimo "
                + Dinero.soles(x.getPrecioMinimo()) + ".");
    }

    private void pintarFicha(Libro x) {
        lblTitulo.setText(x.getNombre());
        lblSubtitulo.setText(x.getAutor() + " · " + x.getGenero());
        lblCodigo.setText("Código " + x.getCodigo());
        lblStockBajo.setVisible(x.stockBajo(Libro.UMBRAL_STOCK_BAJO));
        lblStockBajo.setText("STOCK BAJO · quedan " + x.getStock());
        txtPrecioVenta.setText(Dinero.texto(x.getPrecioVenta()));
        txtPrecioMinimo.setText(Dinero.texto(x.getPrecioMinimo()));
        txtStock.setText(x.getStock() + " unidades");
        txtPrecioCompra.setText(Dinero.texto(x.getPrecioCompra()));
        txtCantidad.setText("1");
        txtPrecioAplicado.setText(Dinero.texto(x.getPrecioVenta()));
        btnAgregar.setEnabled(true);
        calcularSubtotal();
    }

    private void calcularSubtotal() {
        int c = Validador.entero(txtCantidad.getText(), 0);
        BigDecimal p = Dinero.de(txtPrecioAplicado.getText());
        if (c <= 0 || p == null) {
            txtSubtotalItem.setText(Dinero.texto(Dinero.CERO));
            return;
        }
        txtSubtotalItem.setText(Dinero.texto(Dinero.multiplica(p, c)));
    }

private void limpiarFicha() {
        libroActual = null;
        lblTitulo.setText(" ");
        lblSubtitulo.setText("Seleccione un libro de la lista");
        lblCodigo.setText(" ");
        lblStockBajo.setVisible(false);
        txtPrecioVenta.setText("");
        txtPrecioMinimo.setText("");
        txtStock.setText("");
        txtPrecioCompra.setText("");
        txtCantidad.setText("1");
        txtPrecioAplicado.setText("");
        txtSubtotalItem.setText(Dinero.texto(Dinero.CERO));
        lblResumen.setText("Seleccione un libro de la lista");
info("Se valida que la cantidad no pase del stock y que el precio no baje del mínimo.");
        btnAgregar.setEnabled(false);
        ficha.setVisible(false);
        revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("Agregar a la venta")) {
            agregar();
        } else if (cmd.equals("Cancelar")) {
            tabla.clearSelection();
            filtrar();
            txtBuscarNombre.requestFocusInWindow();
        }
    }

private void agregar() {
        if (libroActual == null) {
            aviso("Seleccione un libro de la lista.");
            return;
        }
        if (!Validador.esEntero(txtCantidad.getText())) {
            aviso("La cantidad debe ser un número entero.");
            return;
        }
        int cantidad = Validador.entero(txtCantidad.getText(), 0);
        if (cantidad <= 0) {
            aviso("La cantidad debe ser mayor que cero.");
            return;
        }
        int yaEnDetalle = listener == null ? 0 : listener.cantidadEnDetalle(libroActual.getCodigo());
        if (cantidad + yaEnDetalle > libroActual.getStock()) {
            aviso("No hay stock suficiente. Disponible: " + libroActual.getStock()
                    + " unidad(es) y en la venta ya hay " + yaEnDetalle + ".");
            return;
        }
        BigDecimal precio = Dinero.de(txtPrecioAplicado.getText());
        if (precio == null) {
            aviso("El precio aplicado debe ser numérico.");
            return;
        }
        if (!Dinero.mayorOIgual(precio, libroActual.getPrecioMinimo())) {
            aviso("El precio aplicado no puede ser menor al precio mínimo ("
                    + Dinero.soles(libroActual.getPrecioMinimo()) + ").");
            return;
        }
        listener.agregarItem(libroActual, cantidad, precio);

        Libro agregado = libroActual;
        tabla.clearSelection();
        filtrar();
        libroActual = agregado;
        pintarFicha(agregado);
        ficha.setVisible(true);
        refrescarResumen();
        revalidate();
info("Se agregó " + cantidad + " unidad(es) de " + agregado.getNombre()
                + " a la venta.");
        txtBuscarNombre.requestFocusInWindow();
    }

    private void aviso(String texto) {
        lblNota.setText(texto);
        lblNota.setForeground(Estilo.PELIGRO);
    }

    private void info(String texto) {
        lblNota.setText(texto);
        lblNota.setForeground(Estilo.SECUNDARIO);
    }

@Override
    public Dimension getPreferredSize() {
        return new Dimension(560, 640);
    }
}
