package venta.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import venta.control.Sesion;
import venta.datos.Sistema;
import venta.modelo.Libro;
import venta.util.Dinero;
import venta.util.Validador;

public class FrmMantLibros extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String[] GENEROS = {
        "Matematicas", "Fisica", "Programacion", "Sistemas",
        "Literatura", "Historia", "Geografia", "Otros"};

    private final Sistema sis;

    private final JTextField txtCodigo;
    private final JComboBox<String> cboGenero;
    private final JTextField txtNombre;
    private final JTextField txtAutor;
    private final JTextField txtStock;
    private final JTextField txtCompra;
    private final JTextField txtVenta;
    private final JTextField txtMinimo;
    private final JLabel lblMensaje;
    private final JTextField txtBuscar;
    private final JLabel lblCantidad;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JButton btnModificar;
    private final JButton btnEliminar;
    private ArrayList<Libro> filtrados = new ArrayList<Libro>();

    public FrmMantLibros(Sistema sis) {
        this.sis = sis;
        setTitle("Mantenimiento · Libros");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new Dimension(1120, 620));
        setLocationRelativeTo(null);

        txtCodigo = Estilo.soloLectura(10);
        cboGenero = Estilo.combo(GENEROS);
        txtNombre = Estilo.campo(18);
        txtAutor = Estilo.campo(18);
        txtStock = Estilo.monto(10, true);
        txtCompra = Estilo.monto(10, true);
        txtVenta = Estilo.monto(10, true);
        txtMinimo = Estilo.monto(10, true);
        lblMensaje = Estilo.mensaje("");
        lblMensaje.setName("lblMensaje");

        JPanel formulario = Estilo.tarjeta("Datos del libro");
        formulario.setLayout(new BorderLayout(0, 10));

        JPanel campos = Estilo.columna(
                Estilo.fila("Código", txtCodigo),
                Estilo.fila("Género", cboGenero),
                Estilo.fila("Nombre del libro", txtNombre),
                Estilo.fila("Autor", txtAutor),
                Estilo.fila("Stock", txtStock),
                Estilo.fila("Precio de compra", txtCompra),
                Estilo.fila("Precio de venta", txtVenta),
                Estilo.fila("Precio mínimo", txtMinimo));

        JPanel mensajes = new JPanel(new BorderLayout());
        mensajes.setOpaque(false);
        mensajes.setBorder(javax.swing.BorderFactory.createTitledBorder("Mensajes"));
        mensajes.add(lblMensaje, BorderLayout.CENTER);

        JPanel zonaMensajes = new JPanel(new BorderLayout(0, 10));
        zonaMensajes.setOpaque(false);
        zonaMensajes.add(campos, BorderLayout.CENTER);
        zonaMensajes.add(mensajes, BorderLayout.SOUTH);
        formulario.add(zonaMensajes, BorderLayout.CENTER);

        JButton btnAdicionar = Estilo.primario("Adicionar");
        btnModificar = Estilo.secundario("Modificar");
        btnEliminar = Estilo.peligro("Eliminar");
        JButton btnLimpiar = Estilo.secundario("Limpiar");
        JButton btnGrabar = Estilo.secundario("Grabar cambios");
        JButton btnVolver = Estilo.volver();

        JPanel botones = Estilo.grillaBotones(2, 3, btnAdicionar, btnModificar,
                btnEliminar, btnLimpiar, btnGrabar);
        formulario.add(botones, BorderLayout.SOUTH);
        formulario.setPreferredSize(new Dimension(420, 10));

        txtBuscar = Estilo.campo(20);
        lblCantidad = Estilo.etiqueta("0 libros");

        modelo = Estilo.modelo(new String[]{"Libro", "Autor", "Genero", "Stock",
            "P. compra", "P. venta", "P. minimo", "Codigo"});
        tabla = Estilo.tabla(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(140);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(7).setPreferredWidth(70);
        Estilo.alinearDerecha(tabla, 3, 4, 5, 6, 7);

        JPanel tablaPanel = Estilo.tarjeta("Libros registrados");
        tablaPanel.setLayout(new BorderLayout(0, 8));
        JPanel busqueda = new JPanel(new BorderLayout(8, 0));
        busqueda.setOpaque(false);
        busqueda.add(Estilo.etiqueta("Buscar por nombre, autor o código"), BorderLayout.WEST);
        busqueda.add(txtBuscar, BorderLayout.CENTER);
        busqueda.add(lblCantidad, BorderLayout.EAST);
        tablaPanel.add(busqueda, BorderLayout.NORTH);
        tablaPanel.add(Estilo.scroll(tabla), BorderLayout.CENTER);

        JPanel contenido = new JPanel(new BorderLayout(16, 0));
        contenido.setBackground(Estilo.FONDO);
        contenido.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 16, 12, 16));
        contenido.add(formulario, BorderLayout.WEST);
        contenido.add(tablaPanel, BorderLayout.CENTER);

        JPanel derechaBarra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derechaBarra.setOpaque(false);
        derechaBarra.add(btnVolver);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(Estilo.cabecera("Mantenimiento · Libros", derechaBarra), BorderLayout.NORTH);
        getContentPane().add(contenido, BorderLayout.CENTER);

        btnAdicionar.addActionListener(this);
        btnModificar.addActionListener(this);
        btnEliminar.addActionListener(this);
        btnLimpiar.addActionListener(this);
        btnGrabar.addActionListener(this);
        btnVolver.addActionListener(this);

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
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
        });
        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    cargarFormulario();
                }
            }
        });

        limpiar();
        filtrar();
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    public static boolean permitido() {
        return Sesion.esAdmin();
    }

    private void filtrar() {
        filtrados = sis.getLibros().buscarPorTexto(txtBuscar.getText());
        modelo.setRowCount(0);
        for (int i = 0; i < filtrados.size(); i++) {
            Libro x = filtrados.get(i);
            modelo.addRow(new Object[]{
                x.getNombre(), x.getAutor(), x.getGenero(),
                x.getStock(), Dinero.texto(x.getPrecioCompra()),
                Dinero.texto(x.getPrecioVenta()), Dinero.texto(x.getPrecioMinimo()),
                x.getCodigo()});
        }
        int total = sis.getLibros().tamaño();
        lblCantidad.setText(filtrados.size() == total
                ? total + " libros"
                : filtrados.size() + " de " + total + " libros");
    }

    private void cargarFormulario() {
        int fila = tabla.getSelectedRow();
        boolean hay = fila >= 0;
        btnModificar.setEnabled(hay);
        btnEliminar.setEnabled(hay);
        if (!hay) {
            return;
        }
        int indice = tabla.convertRowIndexToModel(fila);
        Libro x = filtrados.get(indice);
        txtCodigo.setText(String.valueOf(x.getCodigo()));
        cboGenero.setSelectedItem(x.getGenero());
        txtNombre.setText(x.getNombre());
        txtAutor.setText(x.getAutor());
        txtStock.setText(String.valueOf(x.getStock()));
        txtCompra.setText(Dinero.texto(x.getPrecioCompra()));
        txtVenta.setText(Dinero.texto(x.getPrecioVenta()));
        txtMinimo.setText(Dinero.texto(x.getPrecioMinimo()));
        Estilo.mensaje(lblMensaje, "Libro " + x.getCodigo() + " seleccionado.", false);
    }

    private void limpiar() {
        tabla.clearSelection();
        txtCodigo.setText("");
        cboGenero.setSelectedIndex(0);
        txtNombre.setText("");
        txtAutor.setText("");
        txtStock.setText("");
        txtCompra.setText("");
        txtVenta.setText("");
        txtMinimo.setText("");
        Estilo.mensaje(lblMensaje, "", false);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private String validar() {
        if (Validador.vacio(txtNombre.getText())) {
            return "Complete el nombre del libro.";
        }
        if (Validador.vacio(txtAutor.getText())) {
            return "Complete el autor del libro.";
        }
        if (!Validador.esEntero(txtStock.getText()) || Validador.entero(txtStock.getText(), -1) < 0) {
            return "El stock debe ser un número entero mayor o igual que cero.";
        }
        BigDecimal compra = Dinero.de(txtCompra.getText());
        BigDecimal venta = Dinero.de(txtVenta.getText());
        BigDecimal minimo = Dinero.de(txtMinimo.getText());
        if (compra == null || venta == null || minimo == null) {
            return "Los tres precios deben ser numéricos.";
        }
        if (!Dinero.positivo(compra) || !Dinero.positivo(venta) || !Dinero.positivo(minimo)) {
            return "Los precios deben ser mayores que cero.";
        }
        if (Dinero.mayor(minimo, venta)) {
            return "El precio mínimo no puede ser mayor al precio de venta.";
        }
        if (Dinero.mayorOIgual(compra, minimo)) {
            return "El precio de compra debe ser menor al precio mínimo.";
        }
        return null;
    }

    private void adicionar() {
        String error = validar();
        if (error != null) {
            Estilo.mensaje(lblMensaje, error, true);
            return;
        }
        String nombre = Validador.limpio(txtNombre.getText());
        if (sis.getLibros().buscarPorNombre(nombre) != null) {
            Estilo.mensaje(lblMensaje, "Ya existe un libro registrado con ese nombre.", true);
            return;
        }
        int codigo = sis.getLibros().generarCodigo();
        Libro x = new Libro(codigo, nombre, Validador.limpio(txtAutor.getText()),
                (String) cboGenero.getSelectedItem(),
                Validador.entero(txtStock.getText(), 0),
                Dinero.de(txtCompra.getText()), Dinero.de(txtVenta.getText()),
                Dinero.de(txtMinimo.getText()));
        sis.getLibros().adicionar(x);
        limpiar();
        filtrar();
        Estilo.mensaje(lblMensaje, "Libro adicionado con el código " + codigo
                + ". No olvide presionar Grabar cambios.", false);
    }

    private void modificar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Estilo.mensaje(lblMensaje, "Seleccione un libro de la tabla.", true);
            return;
        }
        String error = validar();
        if (error != null) {
            Estilo.mensaje(lblMensaje, error, true);
            return;
        }
        Libro x = filtrados.get(tabla.convertRowIndexToModel(fila));
        String nombre = Validador.limpio(txtNombre.getText());
        Libro repetido = sis.getLibros().buscarPorNombre(nombre);
        if (repetido != null && repetido != x) {
            Estilo.mensaje(lblMensaje, "Ya existe otro libro registrado con ese nombre.", true);
            return;
        }
        x.setNombre(nombre);
        x.setAutor(Validador.limpio(txtAutor.getText()));
        x.setGenero((String) cboGenero.getSelectedItem());
        x.setStock(Validador.entero(txtStock.getText(), 0));
        x.setPrecioCompra(Dinero.de(txtCompra.getText()));
        x.setPrecioVenta(Dinero.de(txtVenta.getText()));
        x.setPrecioMinimo(Dinero.de(txtMinimo.getText()));
        sis.getLibros().ordenarPorCodigo();
        limpiar();
        filtrar();

        Estilo.mensaje(lblMensaje, "Libro modificado. No olvide presionar Grabar cambios.", false);
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Estilo.mensaje(lblMensaje, "Seleccione un libro de la tabla.", true);
            return;
        }
        Libro x = filtrados.get(tabla.convertRowIndexToModel(fila));
        if (sis.getLibros().tieneVentas(x)) {
            Estilo.mensaje(lblMensaje, "El libro " + x.getCodigo()
                    + " no se puede eliminar porque tiene ventas registradas.", true);
            return;
        }
        if (!Estilo.confirmar(this, "¿Eliminar el libro " + x.getNombre()
                + " (código " + x.getCodigo() + ")?\nLa eliminación es definitiva.")) {
            return;
        }
        sis.getLibros().eliminar(x);
        limpiar();
        filtrar();
        Estilo.mensaje(lblMensaje, "Libro eliminado. No olvide presionar Grabar cambios.", false);
    }

    private void grabar() {
        if (sis.getLibros().grabar()) {
            Estilo.mensaje(lblMensaje, "Cambios grabados en " + sis.getLibros().getArchivo(), false);
        } else {
            Estilo.mensaje(lblMensaje, "No se pudo escribir el archivo de libros.", true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "Adicionar":
                adicionar();
                break;
            case "Modificar":
                modificar();
                break;
            case "Eliminar":
                eliminar();
                break;
            case "Limpiar":
                limpiar();
                filtrar();
                break;
            case "Grabar cambios":
                grabar();
                break;
            case "Volver":
                sis.grabarTodo();
                dispose();
                break;
            default:
                break;
        }
    }
}
