package venta.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import venta.util.Fecha;

public class SelectorFecha extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Locale ESPANOL = Locale.forLanguageTag("es");
    private static final DateTimeFormatter MES_ANO =
            DateTimeFormatter.ofPattern("MMMM yyyy", ESPANOL);

    private final JTextField campo;
    private final JButton boton;
    private final JPopupMenu popup = new JPopupMenu();
    private final JLabel lblMes = new JLabel("", SwingConstants.CENTER);
    private final JPanel dias = new JPanel(new GridLayout(6, 7, 2, 2));

    private YearMonth mes;

    public SelectorFecha(int columnas) {
        super(new BorderLayout(4, 0));
        setOpaque(false);

        campo = Estilo.campo(columnas);
        campo.setToolTipText("dd/mm/aaaa");

        boton = new JButton(new IconoCalendario());
        boton.setPreferredSize(new Dimension(34, Estilo.ALTO_CAMPO));
        boton.setMinimumSize(new Dimension(34, Estilo.ALTO_CAMPO));
        boton.setMargin(new Insets(0, 0, 0, 0));
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setToolTipText("Elegir fecha del calendario");
        boton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alternarCalendario();
            }
        });

        add(campo, BorderLayout.CENTER);
        add(boton, BorderLayout.EAST);

        popup.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
        popup.setLightWeightPopupEnabled(true);
        popup.add(crearCalendario());
    }

    private JPanel crearCalendario() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(Estilo.TARJETA);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.setPreferredSize(new Dimension(248, 224));

        JButton anterior = navegacion("<");
        anterior.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mes = mes.minusMonths(1);
                pintarMes();
            }
        });
        JButton siguiente = navegacion(">");
        siguiente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mes = mes.plusMonths(1);
                pintarMes();
            }
        });

        JPanel cabecera = new JPanel(new BorderLayout(4, 0));
        cabecera.setOpaque(false);
        cabecera.add(anterior, BorderLayout.WEST);
        cabecera.add(lblMes, BorderLayout.CENTER);
        cabecera.add(siguiente, BorderLayout.EAST);
        lblMes.setFont(new Font(Estilo.FUENTE, Font.BOLD, 13));
        lblMes.setForeground(Estilo.TEXTO);

        JPanel semana = new JPanel(new GridLayout(1, 7, 2, 0));
        semana.setOpaque(false);
        String[] iniciales = {"Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do"};
        for (int i = 0; i < iniciales.length; i++) {
            JLabel l = new JLabel(iniciales[i], SwingConstants.CENTER);
            l.setFont(new Font(Estilo.FUENTE, Font.BOLD, 11));
            l.setForeground(Estilo.SECUNDARIO);
            semana.add(l);
        }

        JPanel cuerpo = new JPanel(new BorderLayout(0, 4));
        cuerpo.setOpaque(false);
        cuerpo.add(semana, BorderLayout.NORTH);
        cuerpo.add(dias, BorderLayout.CENTER);
        dias.setOpaque(false);

        panel.add(cabecera, BorderLayout.NORTH);
        panel.add(cuerpo, BorderLayout.CENTER);
        return panel;
    }

    private JButton navegacion(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font(Estilo.FUENTE, Font.BOLD, 13));
        b.setPreferredSize(new Dimension(30, 24));
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void alternarCalendario() {
        if (popup.isVisible()) {
            popup.setVisible(false);
            return;
        }
        LocalDate actual = Fecha.aFecha(campo.getText());
        if (actual == null) {
            actual = LocalDate.now();
        }
        mes = YearMonth.from(actual);
        pintarMes();
        popup.show(campo, 0, campo.getHeight());
    }

    private void pintarMes() {
        String texto = mes.atDay(1).format(MES_ANO);
        lblMes.setText(texto.substring(0, 1).toUpperCase(ESPANOL) + texto.substring(1));

        dias.removeAll();
        LocalDate seleccion = Fecha.aFecha(campo.getText());
        LocalDate hoy = LocalDate.now();
        LocalDate primero = mes.atDay(1);
        LocalDate inicio = primero.minusDays(primero.getDayOfWeek().getValue() - 1);

        for (int i = 0; i < 42; i++) {
            final LocalDate fecha = inicio.plusDays(i);
            JButton b = new JButton(String.valueOf(fecha.getDayOfMonth()));
            b.setFont(new Font(Estilo.FUENTE, Font.PLAIN, 12));
            b.setMargin(new Insets(0, 0, 0, 0));
            b.setFocusPainted(false);
            b.setRolloverEnabled(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            boolean delMes = YearMonth.from(fecha).equals(mes);
            boolean esSeleccion = fecha.equals(seleccion);
            boolean esHoy = fecha.equals(hoy);

            if (esSeleccion) {
                b.setOpaque(true);
                b.setContentAreaFilled(true);
                b.setBackground(Estilo.ACENTO);
                b.setForeground(Color.WHITE);
                b.setBorder(BorderFactory.createLineBorder(Estilo.ACENTO));
            } else if (esHoy) {
                b.setOpaque(true);
                b.setContentAreaFilled(true);
                b.setBackground(Estilo.FONDO);
                b.setForeground(Estilo.ACENTO);
                b.setBorder(BorderFactory.createLineBorder(Estilo.ACENTO));
            } else {
                b.setOpaque(false);
                b.setContentAreaFilled(false);
                b.setForeground(delMes ? Estilo.TEXTO : Estilo.SECUNDARIO);
                b.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            }

            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    campo.setText(fecha.format(Fecha.FECHA));
                    popup.setVisible(false);
                    campo.postActionEvent();
                }
            });
            dias.add(b);
        }
        dias.revalidate();
        dias.repaint();
    }

    public String getText() {
        return campo.getText();
    }

    public void setText(String texto) {
        campo.setText(texto);
    }

    public void setFecha(LocalDate fecha) {
        campo.setText(fecha == null ? "" : fecha.format(Fecha.FECHA));
    }

    public JTextField getCampo() {
        return campo;
    }

    public void addActionListener(ActionListener l) {
        campo.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        campo.removeActionListener(l);
    }

    private static class IconoCalendario implements Icon {

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Estilo.ACENTO);
            g2.drawRect(x + 1, y + 3, 13, 11);
            g2.fillRect(x + 1, y + 3, 13, 3);
            g2.drawLine(x + 4, y, x + 4, y + 5);
            g2.drawLine(x + 11, y, x + 11, y + 5);
            g2.dispose();
        }
    }
}
