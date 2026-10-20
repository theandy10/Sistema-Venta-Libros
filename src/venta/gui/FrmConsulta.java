package venta.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import venta.datos.Sistema;
import venta.modelo.Cliente;
import venta.modelo.DetalleVenta;
import venta.modelo.Libro;
import venta.modelo.Venta;
import venta.util.Dinero;
import venta.util.Validador;

public class FrmConsulta extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final Sistema sis;

    private final CampoSugerido txtLibro;
    private final JTextArea areaLibro;
    private final JLabel lblStockLibro;

    private final CampoSugerido txtCliente;
    private final JTextArea areaCliente;
    private final DefaultTableModel modeloCompras;

    private final CampoSugerido txtVenta;
    private final JTextArea areaVenta;
    private final DefaultTableModel modeloDetalle;

    public FrmConsulta(Sistema sis) {
        this.sis = sis;
        setTitle("Consulta");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new Dimension(960, 640));
        setLocationRelativeTo(null);

        txtLibro = campoConSugerencias(sis.getLibros().paraSugerencias());
        prepararCampo(txtLibro, "Buscar libro");
        areaLibro = area();
        lblStockLibro = new JLabel(" ");
        lblStockLibro.setFont(new Font(Estilo.FUENTE, Font.BOLD, 13));
        lblStockLibro.setOpaque(true);
        lblStockLibro.setBackground(Estilo.STOCK_BAJO);
        lblStockLibro.setForeground(Estilo.STOCK_BAJO_TEXTO);
        lblStockLibro.setVisible(false);

        txtCliente = campoConSugerencias(sis.getClientes().paraSugerencias());
        prepararCampo(txtCliente, "Buscar cliente");
        areaCliente = area();
        modeloCompras = Estilo.modelo(new String[]{"Venta", "Fecha", "Hora", "Total"});

        txtVenta = campoConSugerencias(sis.getVentas().paraSugerencias());
        prepararCampo(txtVenta, "Buscar venta");
        areaVenta = area();
        modeloDetalle = Estilo.modelo(
                new String[]{"Código", "Descripción", "Cantidad", "P. unitario", "Subtotal"});

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Libros", pestanaLibro());
        tabs.addTab("Clientes", pestanaCliente());
        tabs.addTab("Ventas", pestanaVenta());

        JButton btnVolver = Estilo.volver();
        btnVolver.addActionListener(this);
        JPanel derechaBarra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derechaBarra.setOpaque(false);
        derechaBarra.add(btnVolver);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Estilo.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        contenido.add(tabs, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(Estilo.cabecera("Consulta", derechaBarra), BorderLayout.NORTH);
        getContentPane().add(contenido, BorderLayout.CENTER);
    }

    private JTextArea area() {
        JTextArea a = new JTextArea();
        a.setEditable(false);
        a.setFont(new Font(Estilo.FUENTE, Font.PLAIN, 14));
        a.setBackground(Estilo.TARJETA);
        a.setForeground(Estilo.TEXTO);
        return a;
    }

    private CampoSugerido campoConSugerencias(ArrayList<String[]> pares) {
        CampoSugerido campo = new CampoSugerido(12);
        ArrayList<String> aMostrar = new ArrayList<String>();
        ArrayList<String> aEscribir = new ArrayList<String>();
        for (int i = 0; i < pares.size(); i++) {
            aEscribir.add(pares.get(i)[0]);
            aMostrar.add(pares.get(i)[1]);
        }
        campo.setSugerencias(aMostrar, aEscribir);
        return campo;
    }

    private void prepararCampo(final CampoSugerido campo, final String comando) {
        campo.setAlElegir(new Runnable() {
            @Override
            public void run() {
                ejecutarBusqueda(comando);
            }
        });
        campo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarBusqueda(comando);
            }
        });
    }

    private JButton botonBuscar(String texto, String comando) {
        JButton b = Estilo.primario(texto);
        b.setActionCommand(comando);
        b.addActionListener(this);
        return b;
    }

    private JPanel barraBusqueda(String etiqueta, JTextField campo, JButton boton) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        p.setOpaque(false);
        p.add(Estilo.etiqueta(etiqueta));
        p.add(campo);
        p.add(boton);
        return p;
    }

    private JPanel pestanaLibro() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Estilo.FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        p.add(barraBusqueda("Código del libro:", txtLibro, botonBuscar("Buscar", "Buscar libro")), BorderLayout.NORTH);
        JPanel datos = Estilo.tarjeta("Datos del libro");
        datos.setLayout(new BorderLayout(0, 8));
        datos.add(Estilo.scroll(areaLibro), BorderLayout.CENTER);
        datos.add(lblStockLibro, BorderLayout.SOUTH);
        p.add(datos, BorderLayout.CENTER);
        return p;
    }

    private JPanel pestanaCliente() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Estilo.FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        p.add(barraBusqueda("DNI del cliente:", txtCliente, botonBuscar("Buscar", "Buscar cliente")), BorderLayout.NORTH);
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        JPanel datos = Estilo.tarjeta("Datos del cliente");
        datos.setLayout(new BorderLayout(0, 8));
        datos.add(Estilo.scroll(areaCliente), BorderLayout.CENTER);
        centro.add(datos, BorderLayout.NORTH);
        JTable tabla = Estilo.tabla(modeloCompras);
        Estilo.alinearDerecha(tabla, 3);
        JPanel compras = Estilo.tarjeta("Compras del cliente");
        compras.setLayout(new BorderLayout());
        compras.add(Estilo.scroll(tabla), BorderLayout.CENTER);
        centro.add(compras, BorderLayout.CENTER);
        p.add(centro, BorderLayout.CENTER);
        return p;
    }

    private JPanel pestanaVenta() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Estilo.FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        p.add(barraBusqueda("Número de venta:", txtVenta, botonBuscar("Buscar", "Buscar venta")), BorderLayout.NORTH);
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        JPanel datos = Estilo.tarjeta("Datos de la venta");
        datos.setLayout(new BorderLayout(0, 8));
        datos.add(Estilo.scroll(areaVenta), BorderLayout.CENTER);
        centro.add(datos, BorderLayout.NORTH);
        JTable tabla = Estilo.tabla(modeloDetalle);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(220);
        Estilo.alinearDerecha(tabla, 2, 3, 4);
        JPanel detalle = Estilo.tarjeta("Detalle de la venta");
        detalle.setLayout(new BorderLayout());
        detalle.add(Estilo.scroll(tabla), BorderLayout.CENTER);
        centro.add(detalle, BorderLayout.CENTER);
        p.add(centro, BorderLayout.CENTER);
        return p;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "Buscar libro":
            case "Buscar cliente":
            case "Buscar venta":
                ejecutarBusqueda(cmd);
                break;
            case "Volver":
                sis.grabarTodo();
                dispose();
                break;
            default:
                break;
        }
    }

    private void ejecutarBusqueda(String comando) {
        if ("Buscar libro".equals(comando)) {
            consultarLibro();
        } else if ("Buscar cliente".equals(comando)) {
            consultarCliente();
        } else if ("Buscar venta".equals(comando)) {
            consultarVenta();
        }
    }

    private void consultarLibro() {
        String dato = Validador.limpio(txtLibro.getText());
        if (dato.isEmpty()) {
            areaLibro.setText("Escriba el código o el nombre del libro.");
            lblStockLibro.setVisible(false);
            return;
        }
        Libro x = null;
        if (dato.matches("\\d+")) {
            x = sis.getLibros().buscar(Validador.entero(dato, -1));
        }
        if (x == null) {
            x = sis.getLibros().buscarPorNombre(dato);
        }
        if (x == null) {
            areaLibro.setText("No existe un libro con ese código o nombre.");
            lblStockLibro.setVisible(false);
            return;
        }
        areaLibro.setText("");
        areaLibro.append("Código              : " + x.getCodigo() + "\n");
        areaLibro.append("Nombre              : " + x.getNombre() + "\n");
        areaLibro.append("Autor               : " + x.getAutor() + "\n");
        areaLibro.append("Género              : " + x.getGenero() + "\n");
        areaLibro.append("Stock               : " + x.getStock() + " unidades\n");
        areaLibro.append("Precio de compra    : " + Dinero.soles(x.getPrecioCompra()) + "\n");
        areaLibro.append("Precio de venta     : " + Dinero.soles(x.getPrecioVenta()) + "\n");
        areaLibro.append("Precio mínimo       : " + Dinero.soles(x.getPrecioMinimo()) + "\n");
        areaLibro.append("Unidades vendidas   : " + sis.getReporte().unidades(x.getCodigo()) + "\n");
        lblStockLibro.setText("STOCK BAJO · quedan " + x.getStock() + " unidades");
        lblStockLibro.setVisible(x.stockBajo(Libro.UMBRAL_STOCK_BAJO));
    }

    private void consultarCliente() {
        String dato = Validador.limpio(txtCliente.getText());
        if (dato.isEmpty()) {
            areaCliente.setText("Escriba el DNI del cliente.");
            modeloCompras.setRowCount(0);
            return;
        }
        Cliente c = Validador.dni(dato) ? sis.getClientes().buscar(dato) : null;
        if (c == null) {
            areaCliente.setText("El cliente no está registrado.");
            modeloCompras.setRowCount(0);
            return;
        }
        ArrayList<Venta> v = sis.getVentas().ventasDeCliente(c.getDni());
        areaCliente.setText("");
        areaCliente.append("Código              : " + c.getCodigo() + "\n");
        areaCliente.append("DNI                 : " + c.getDni() + "\n");
        areaCliente.append("Nombre completo     : " + c.nombreCompleto() + "\n");
        areaCliente.append("Celular             : " + c.getCelular() + "\n");
        areaCliente.append("Compras             : " + v.size() + "\n");
        areaCliente.append("Total gastado       : " + Dinero.soles(sis.getReporte().totalVentas(v)) + "\n");
        modeloCompras.setRowCount(0);
        for (int i = 0; i < v.size(); i++) {
            modeloCompras.addRow(new Object[]{
                v.get(i).numeroTexto(), v.get(i).getFecha(), v.get(i).getHora(),
                Dinero.soles(v.get(i).getTotal())});
        }
    }

    private void consultarVenta() {
        String dato = Validador.limpio(txtVenta.getText());
        if (dato.isEmpty()) {
            areaVenta.setText("Escriba el número de venta.");
            modeloDetalle.setRowCount(0);
            return;
        }
        if (!Validador.esEntero(dato)) {
            areaVenta.setText("El número de venta debe ser un número entero válido.");
            modeloDetalle.setRowCount(0);
            return;
        }
        Venta v = sis.getVentas().buscar(Validador.entero(dato, -1));
        if (v == null) {
            areaVenta.setText("La venta N. " + dato + " no existe.");
            modeloDetalle.setRowCount(0);
            return;
        }
        Cliente c = v.clienteRegistrado() ? sis.getClientes().buscar(v.getDni()) : null;
        areaVenta.setText("");
        areaVenta.append("Número              : " + v.numeroTexto() + "\n");
        areaVenta.append("Fecha y hora        : " + v.getFecha() + "  " + v.getHora() + "\n");
        areaVenta.append("Cliente             : " + (c == null ? "Público general" : c.nombreCompleto()) + "\n");
        areaVenta.append("DNI                 : " + (c == null ? "-" : c.getDni()) + "\n");
        areaVenta.append("Atendido por        : " + v.getUsuario() + "\n");
        areaVenta.append("Método de pago      : " + v.getMetodo() + "\n");
        areaVenta.append("Subtotal            : " + Dinero.soles(v.getSubtotal()) + "\n");
        areaVenta.append("Descuento           : " + Dinero.soles(v.getDescuento()) + "\n");
        areaVenta.append("TOTAL               : " + Dinero.soles(v.getTotal()) + "\n");
        areaVenta.append("Monto pagado        : " + Dinero.soles(v.getMontoPagado()) + "\n");
        areaVenta.append("Vuelto              : " + Dinero.soles(v.getVuelto()) + "\n");
        modeloDetalle.setRowCount(0);
        ArrayList<DetalleVenta> d = sis.getDetalles().detalleDeVenta(v.getNumero());
        for (int i = 0; i < d.size(); i++) {
            DetalleVenta x = d.get(i);
            modeloDetalle.addRow(new Object[]{
                x.getCodigoLibro(), x.getNombreLibro(), x.getCantidad(),
                Dinero.texto(x.getPrecioUnitario()), Dinero.texto(x.getSubtotal())});
        }
    }
}
