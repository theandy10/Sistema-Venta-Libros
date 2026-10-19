package venta.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import venta.datos.Boleta;
import venta.datos.Sistema;
import venta.modelo.Venta;

public class DlgBoleta extends JDialog {

    private static final long serialVersionUID = 1L;

    public DlgBoleta(Venta v, String texto) {
        super((JFrame) null, "Boleta de venta N. " + v.numeroTexto(), true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));

        area.setBackground(Estilo.LECTURA);
        area.setForeground(Estilo.TEXTO);
        area.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));

        JButton btnGuardar = Estilo.secundario("Guardar");
        JButton btnImprimir = Estilo.secundario("Imprimir");
        JButton btnCerrar = Estilo.primario("Cerrar");

        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardar(v, texto);
            }
        });
        btnImprimir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Boleta.imprimir(texto, v.getNumero());
                } catch (Exception ex) {
                    Estilo.error(DlgBoleta.this,
                            "No se pudo imprimir: la impresora no esta disponible.");
                }
            }
        });
        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        botones.setBackground(Estilo.FONDO);
        botones.add(btnGuardar);
        botones.add(btnImprimir);
        botones.add(btnCerrar);

        getContentPane().setLayout(new BorderLayout(0, 8));
        getContentPane().setBackground(Estilo.FONDO);
        getContentPane().add(scroll, BorderLayout.CENTER);
        getContentPane().add(botones, BorderLayout.SOUTH);

        setSize(new Dimension(680, 620));
        setLocationRelativeTo(null);
    }

    private void guardar(Venta v, String texto) {
        String ruta = Boleta.rutaArchivo(Sistema.get().getCarpeta(), v.getNumero());
        if (Boleta.guardar(texto, Sistema.get().getCarpeta(), v.getNumero())) {
            Estilo.info(this, "Boleta guardada en:\n" + ruta);
        } else {
            Estilo.error(this, "No se pudo guardar la boleta en:\n" + ruta);
        }
    }
}
