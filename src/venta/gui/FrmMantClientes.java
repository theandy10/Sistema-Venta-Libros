package venta.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
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
import venta.datos.Sistema;
import venta.modelo.Cliente;
import venta.util.Validador;

public class FrmMantClientes extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final Sistema sis;

    private final JTextField txtCodigo;
    private final JTextField txtDni;
    private final JTextField txtNombres;
    private final JTextField txtApellidos;
    private final JTextField txtCelular;
    private final JLabel lblMensaje;
    private final JTextField txtBuscar;
    private final JLabel lblCantidad;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JButton btnModificar;
    private final JButton btnEliminar;
    private ArrayList<Cliente> filtrados = new ArrayList<Cliente>();

    public FrmMantClientes(Sistema sis) {
        this.sis = sis;
        setTitle("Gestión · Clientes");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new Dimension(1120, 620));
        setLocationRelativeTo(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                sis.grabarTodo();
            }
        });

        txtCodigo = Estilo.soloLectura(10);
        txtDni = Estilo.campo(10);
        txtNombres = Estilo.campo(18);
        txtApellidos = Estilo.campo(18);
        txtCelular = Estilo.monto(10, true);
        lblMensaje = Estilo.mensaje("");

        JButton btnAdicionar = Estilo.primario("Adicionar");
        btnModificar = Estilo.secundario("Modificar");
        btnEliminar = Estilo.peligro("Eliminar");
        JButton btnLimpiar = Estilo.secundario("Limpiar");
        JButton btnGrabar = Estilo.secundario("Grabar cambios");
        JButton btnVolver = Estilo.volver();

        JPanel formulario = Estilo.tarjeta("Datos del cliente");
        formulario.setLayout(new BorderLayout(0, 10));
        JPanel campos = Estilo.columna(
                Estilo.fila("Código", txtCodigo),
                Estilo.fila("DNI", txtDni),
                Estilo.fila("Nombres", txtNombres),
                Estilo.fila("Apellidos", txtApellidos),
                Estilo.fila("Celular", txtCelular));
        JPanel mensajes = new JPanel(new BorderLayout());
        mensajes.setOpaque(false);
        mensajes.setBorder(javax.swing.BorderFactory.createTitledBorder("Mensajes"));
        mensajes.add(lblMensaje, BorderLayout.CENTER);
        JPanel zonaMensajes = new JPanel(new BorderLayout(0, 10));
        zonaMensajes.setOpaque(false);
        zonaMensajes.add(campos, BorderLayout.CENTER);
        zonaMensajes.add(mensajes, BorderLayout.SOUTH);
        formulario.add(zonaMensajes, BorderLayout.CENTER);

        JPanel botones = Estilo.grillaBotones(2, 3, btnAdicionar, btnModificar,
                btnEliminar, btnLimpiar, btnGrabar);
        formulario.add(botones, BorderLayout.SOUTH);
        formulario.setPreferredSize(new Dimension(420, 10));

        txtBuscar = Estilo.campo(20);
        lblCantidad = Estilo.etiqueta("0 clientes");
        modelo = Estilo.modelo(new String[]{"Nombres", "Apellidos", "DNI", "Celular",
            "Compras", "Código"});
        tabla = Estilo.tabla(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(140);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(70);
        Estilo.alinearDerecha(tabla, 3, 4, 5);
        Estilo.alinearCentro(tabla, 2);

        JPanel tablaPanel = Estilo.tarjeta("Clientes registrados");
        tablaPanel.setLayout(new BorderLayout(0, 8));
        JPanel busqueda = new JPanel(new BorderLayout(8, 0));
        busqueda.setOpaque(false);
        busqueda.add(Estilo.etiqueta("Buscar por DNI, nombre o código"), BorderLayout.WEST);
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
        getContentPane().add(Estilo.cabecera("Gestión · Clientes", derechaBarra), BorderLayout.NORTH);
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

    private void filtrar() {
        filtrados = sis.getClientes().buscarPorTexto(txtBuscar.getText());
        modelo.setRowCount(0);
        for (int i = 0; i < filtrados.size(); i++) {
            Cliente x = filtrados.get(i);
            modelo.addRow(new Object[]{
                x.getNombres(), x.getApellidos(), x.getDni(), x.getCelular(),
                sis.getClientes().numeroVentas(x), x.getCodigo()});
        }
        int total = sis.getClientes().tamaño();
        lblCantidad.setText(filtrados.size() == total
                ? total + " clientes"
                : filtrados.size() + " de " + total + " clientes");
    }

    private void cargarFormulario() {
        int fila = tabla.getSelectedRow();
        boolean hay = fila >= 0;
        btnModificar.setEnabled(hay);
        btnEliminar.setEnabled(hay);
        if (!hay) {
            return;
        }
        Cliente x = filtrados.get(tabla.convertRowIndexToModel(fila));
        txtCodigo.setText(String.valueOf(x.getCodigo()));
        txtDni.setText(x.getDni());
        txtDni.setEditable(false);
        txtDni.setToolTipText("El DNI no se puede modificar.");
        txtNombres.setText(x.getNombres());
        txtApellidos.setText(x.getApellidos());
        txtCelular.setText(String.valueOf(x.getCelular()));
        Estilo.mensaje(lblMensaje, "Cliente " + x.getDni() + " seleccionado.", false);
    }

    private void limpiar() {
        tabla.clearSelection();
        txtCodigo.setText("");
        txtDni.setText("");
        txtDni.setEditable(true);
        txtDni.setToolTipText(null);
        txtNombres.setText("");
        txtApellidos.setText("");
        txtCelular.setText("");
        Estilo.mensaje(lblMensaje, "", false);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private String validar() {
        if (!Validador.dni(txtDni.getText())) {
            return "El DNI debe tener 8 dígitos numéricos.";
        }
        if (Validador.vacio(txtNombres.getText())) {
            return "Complete los nombres del cliente.";
        }
        if (Validador.vacio(txtApellidos.getText())) {
            return "Complete los apellidos del cliente.";
        }
        if (!Validador.celular(txtCelular.getText())) {
            return "El celular debe tener 9 dígitos numéricos.";
        }
        return null;
    }

    private void adicionar() {
        String error = validar();
        if (error != null) {
            Estilo.mensaje(lblMensaje, error, true);
            return;
        }
        String dni = Validador.limpio(txtDni.getText());
        if (sis.getClientes().existeDni(dni)) {
            Estilo.mensaje(lblMensaje, "Ya existe un cliente registrado con ese DNI.", true);
            return;
        }
        int codigo = sis.getClientes().generarCodigo();
        Cliente x = new Cliente(codigo, dni, Validador.limpio(txtNombres.getText()),
                Validador.limpio(txtApellidos.getText()), Validador.entero(txtCelular.getText(), 0));
        sis.getClientes().adicionar(x);
        limpiar();
        filtrar();
        Estilo.mensaje(lblMensaje, "Cliente adicionado con el código " + codigo
                + ". No olvide presionar Grabar cambios.", false);
    }

    private void modificar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Estilo.mensaje(lblMensaje, "Seleccione un cliente de la tabla.", true);
            return;
        }
        if (!Validador.celular(txtCelular.getText())) {
            Estilo.mensaje(lblMensaje, "El celular debe tener 9 dígitos numéricos.", true);
            return;
        }
        if (Validador.vacio(txtNombres.getText()) || Validador.vacio(txtApellidos.getText())) {
            Estilo.mensaje(lblMensaje, "Complete los nombres y apellidos del cliente.", true);
            return;
        }
        Cliente x = filtrados.get(tabla.convertRowIndexToModel(fila));

        x.setNombres(Validador.limpio(txtNombres.getText()));
        x.setApellidos(Validador.limpio(txtApellidos.getText()));
        x.setCelular(Validador.entero(txtCelular.getText(), 0));
        limpiar();
        filtrar();
        Estilo.mensaje(lblMensaje, "Cliente modificado. No olvide presionar Grabar cambios.", false);
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Estilo.mensaje(lblMensaje, "Seleccione un cliente de la tabla.", true);
            return;
        }
        Cliente x = filtrados.get(tabla.convertRowIndexToModel(fila));
        if (sis.getClientes().tieneVentas(x)) {
            Estilo.mensaje(lblMensaje, "El cliente con DNI " + x.getDni()
                    + " no se puede eliminar porque tiene " + sis.getClientes().numeroVentas(x)
                    + " venta(s) registradas.", true);
            return;
        }
        if (!Estilo.confirmar(this, "¿Eliminar al cliente " + x.nombreCompleto()
                + " (DNI " + x.getDni() + ")?\nLa eliminación es definitiva.")) {
            return;
        }
        sis.getClientes().eliminar(x);
        limpiar();
        filtrar();
        Estilo.mensaje(lblMensaje, "Cliente eliminado. No olvide presionar Grabar cambios.", false);
    }

    private void grabar() {
        if (sis.getClientes().grabar()) {
            Estilo.mensaje(lblMensaje, "Cambios grabados en " + sis.getClientes().getArchivo(), false);
        } else {
            Estilo.mensaje(lblMensaje, "No se pudo escribir el archivo de clientes.", true);
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
