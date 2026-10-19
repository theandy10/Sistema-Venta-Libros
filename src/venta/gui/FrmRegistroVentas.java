package venta.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Locale;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import venta.datos.Sistema;
import venta.util.Fecha;

public class FrmRegistroVentas extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final Sistema sis;
    private final PanelLibro panelLibro;
    private final PanelVenta panelVenta;
    private final JLabel lblNumero;
    private final FrmVentasRegistradas frmVentas;

    public FrmRegistroVentas(Sistema sis) {
        this.sis = sis;
        setTitle("Registro de ventas");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JButton btnNueva = Estilo.botonBarra("Nueva venta");
        JButton btnRegistradas = Estilo.botonBarra("Ventas registradas");
        JButton btnVolver = Estilo.volver();
        btnNueva.addActionListener(this);
        btnRegistradas.addActionListener(this);
        btnVolver.addActionListener(this);

        lblNumero = new JLabel();
        lblNumero.setFont(new java.awt.Font(Estilo.FUENTE, java.awt.Font.BOLD, 14));
        lblNumero.setForeground(Estilo.TEXTO);
        actualizarNumero();

        JPanel derecha = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);
        derecha.add(lblNumero);
        derecha.add(btnNueva);
        derecha.add(btnRegistradas);
        derecha.add(btnVolver);

        panelLibro = new PanelLibro(sis);
        panelVenta = new PanelVenta(sis);
        panelLibro.setVentaListener(panelVenta);
        panelVenta.setAlRegistrar(new Runnable() {
            @Override
            public void run() {
                panelLibro.refrescar();
                actualizarNumero();
                frmVentas.refrescar();
            }
        });
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelLibro, panelVenta);
        split.setResizeWeight(0.5);
        split.setContinuousLayout(true);
        split.setBorder(null);
        split.setDividerSize(8);
        split.setBackground(Estilo.FONDO);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Estilo.FONDO);
        contenido.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 16, 12, 16));
        contenido.add(split, BorderLayout.CENTER);

        frmVentas = new FrmVentasRegistradas(sis, this);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(Estilo.cabecera("Registro de ventas", derecha), BorderLayout.NORTH);
        getContentPane().add(contenido, BorderLayout.CENTER);

        setSize(new Dimension(1240, 790));
        setLocationRelativeTo(null);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                split.setDividerLocation(0.5);
            }
        });
    }

    public void actualizarNumero() {

        lblNumero.setText("Venta N. "
                + String.format(Locale.US, "%06d", sis.getVentas().generarNumero())
                + " · " + Fecha.hoy());
    }

    public void refrescarLibro() {
        panelLibro.refrescar();
        panelLibro.refrescarResumen();

        panelVenta.refrescarSugerencias();
    }

    public FrmVentasRegistradas getVentasRegistradas() {
        return frmVentas;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("Nueva venta")) {
            frmVentas.setVisible(false);
        } else if (cmd.equals("Ventas registradas")) {
            frmVentas.refrescar();
            frmVentas.setLocationRelativeTo(this);
            frmVentas.setVisible(true);
            frmVentas.toFront();
        } else if (cmd.equals("Volver")) {
            sis.grabarTodo();
            dispose();
        }
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSED) {
            sis.grabarTodo();
        }
        super.processWindowEvent(e);
    }
}
