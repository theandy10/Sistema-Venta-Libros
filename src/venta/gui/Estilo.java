package venta.gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public final class Estilo {

    public static final int TAMANO = 13;

    public static final String FUENTE = Font.DIALOG;

    public static Color ACENTO;
    public static Color FONDO;
    public static Color TARJETA;
    public static Color TEXTO;
    public static Color SECUNDARIO;
    public static Color LECTURA;
    public static Color BORDE;
    public static Color PELIGRO;
    public static Color STOCK_BAJO;
    public static Color STOCK_BAJO_TEXTO;
    public static Color LECTURA_TEXTO;

    public static final int ALTO_CAMPO = 28;

    private static final int ALTO_BOTON = 30;

    private Estilo() {

    }

    public static void aplicar() {
        try {

            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            try {

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception otro) {

            }
        }
        Color texto = color("Label.foreground", Color.BLACK);
        Color panel = color("Panel.background", Color.LIGHT_GRAY);

        ACENTO = texto;
        TEXTO = texto;
        SECUNDARIO = texto;
        PELIGRO = texto;
        STOCK_BAJO_TEXTO = texto;
        FONDO = panel;
        TARJETA = panel;
        STOCK_BAJO = panel;
        LECTURA = color("TextField.inactiveBackground", Color.LIGHT_GRAY);

        LECTURA_TEXTO = Color.GRAY;
        BORDE = color("Panel.border", Color.GRAY);

        Font f = new Font(Font.DIALOG, Font.PLAIN, TAMANO);
        UIManager.put("Label.font", f);
        UIManager.put("Button.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("PasswordField.font", f);
        UIManager.put("ComboBox.font", f);
        UIManager.put("CheckBox.font", f);
        UIManager.put("Table.font", f);
        UIManager.put("TableHeader.font", new Font(Font.DIALOG, Font.BOLD, TAMANO - 1));
        UIManager.put("TabbedPane.font", f);
        UIManager.put("TitledBorder.font", new Font(Font.DIALOG, Font.BOLD, TAMANO - 1));
        UIManager.put("List.font", f);
        UIManager.put("OptionPane.messageFont", f);
    }

    private static Color color(String clave, Color reserva) {
        Color c = UIManager.getColor(clave);
        return c == null ? reserva : c;
    }

    private static Color textoPorDefecto() {
        return color("Label.foreground", Color.BLACK);
    }

    private static Color panelPorDefecto() {
        return color("Panel.background", Color.LIGHT_GRAY);
    }

    public static JLabel titulo(String contenido) {
        JLabel l = new JLabel(contenido);
        l.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
        return l;
    }

    public static JLabel ficha(String contenido) {
        JLabel l = new JLabel(contenido, JLabel.CENTER);
        l.setFont(new Font(Font.DIALOG, Font.BOLD, 26));
        return l;
    }

    public static JLabel etiqueta(String contenido) {
        JLabel l = new JLabel(contenido);
        l.setFont(new Font(Font.DIALOG, Font.PLAIN, TAMANO - 1));
        return l;
    }

    public static JLabel texto(String contenido) {
        JLabel l = new JLabel(contenido);
        l.setFont(new Font(Font.DIALOG, Font.PLAIN, TAMANO));
        return l;
    }

    public static JButton boton(String contenido, boolean principal) {
        JButton b = new JButton(contenido);
        b.setFont(new Font(Font.DIALOG, principal ? Font.BOLD : Font.PLAIN, TAMANO));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(b.getPreferredSize().width + 16, ALTO_BOTON));
        return b;
    }

    public static JButton primario(String contenido) {
        return boton(contenido, true);
    }

    public static JButton secundario(String contenido) {
        return boton(contenido, false);
    }

    public static JButton peligro(String contenido) {
        return boton(contenido, false);
    }

    public static JButton compacto(String contenido) {
        return compacto(contenido, 120);
    }

    public static JButton compacto(String contenido, int ancho) {
        JButton b = boton(contenido, false);
        b.setPreferredSize(new Dimension(ancho, 28));
        b.setMinimumSize(new Dimension(60, 28));
        return b;
    }

    public static JTextField campo(int columnas) {
        JTextField t = new JTextField(columnas);
        t.setFont(new Font(Font.DIALOG, Font.PLAIN, TAMANO));
        t.setPreferredSize(new Dimension(100, ALTO_CAMPO));
        return t;
    }

    public static JTextField soloLectura(int columnas) {
        JTextField t = campo(columnas);
        t.setEditable(false);
        t.setBackground(LECTURA);
        t.setDisabledTextColor(LECTURA_TEXTO);
        return t;
    }

    public static JTextField monto(int columnas, boolean editable) {
        JTextField t = editable ? campo(columnas) : soloLectura(columnas);
        t.setHorizontalAlignment(JTextField.RIGHT);
        return t;
    }

    public static JSpinner cantidad(int valor, int maximo) {
        JSpinner s = new JSpinner(new SpinnerNumberModel(valor, 1,
                Math.max(1, maximo), 1));
        s.setFont(new Font(Font.DIALOG, Font.PLAIN, TAMANO));
        s.setPreferredSize(new Dimension(70, ALTO_CAMPO));

        javax.swing.JSpinner.DefaultEditor ed =
                (javax.swing.JSpinner.DefaultEditor) s.getEditor();
        ed.getTextField().setEditable(false);
        ed.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
        s.setEditor(ed);
        return s;
    }

    public static JComboBox<String> combo(String[] items) {
        JComboBox<String> c = new JComboBox<String>(items);
        c.setFont(new Font(Font.DIALOG, Font.PLAIN, TAMANO));
        c.setPreferredSize(new Dimension(140, ALTO_CAMPO));
        return c;
    }

public static JPanel conEtiqueta(String contenido, JComponent campo) {
        JPanel p = new JPanel(new java.awt.BorderLayout(4, 2));
        p.add(etiqueta(contenido), java.awt.BorderLayout.NORTH);
        p.add(campo, java.awt.BorderLayout.CENTER);
        sinEstirar(p);
        return p;
    }

    public static JPanel fila(String contenido, JComponent campo) {
        return fila(contenido, campo, 360);
    }

    public static JPanel fila(String contenido, JComponent campo, int ancho) {
        JPanel p = new JPanel(new java.awt.BorderLayout(8, 0));
        JLabel l = etiqueta(contenido);
        l.setPreferredSize(new Dimension(150, ALTO_CAMPO));
        p.add(l, java.awt.BorderLayout.WEST);
        p.add(campo, java.awt.BorderLayout.CENTER);
        p.setPreferredSize(new Dimension(ancho, ALTO_CAMPO + 10));
        sinEstirar(p);
        return p;
    }

    public static void sinEstirar(JComponent c) {
        Dimension alto = c.getPreferredSize();
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, alto.height));
    }

    public static JPanel columna(JComponent... filas) {
        JPanel p = new JPanel();
        p.setLayout(new javax.swing.BoxLayout(p, javax.swing.BoxLayout.Y_AXIS));
        for (int i = 0; i < filas.length; i++) {
            filas[i].setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            p.add(filas[i]);
            if (i < filas.length - 1) {
                p.add(javax.swing.Box.createVerticalStrut(4));
            }
        }
        return p;
    }

    public static JPanel relleno() {
        return new JPanel();
    }

    public static JPanel filaBotones(JComponent... botones) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        for (int i = 0; i < botones.length; i++) {
            p.add(botones[i]);
        }
        return p;
    }

    public static JPanel filaBotonesDerecha(JComponent... botones) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        for (int i = 0; i < botones.length; i++) {
            p.add(botones[i]);
        }
        return p;
    }

    public static JPanel grillaBotones(int filas, int columnas, JComponent... botones) {
        JPanel p = new JPanel(new java.awt.GridBagLayout());
        p.setOpaque(false);
        java.awt.GridBagConstraints g = new java.awt.GridBagConstraints();
        g.insets = new java.awt.Insets(3, 3, 3, 3);
        g.anchor = java.awt.GridBagConstraints.NORTHWEST;
        g.fill = java.awt.GridBagConstraints.NONE;
        g.weightx = 0;
        g.weighty = 0;
        for (int i = 0; i < botones.length; i++) {
            g.gridx = i % columnas;
            g.gridy = i / columnas;
            p.add(botones[i], g);
        }
        int alto = filas * (ALTO_BOTON + 6);
        p.setPreferredSize(new Dimension(10, alto));
        sinEstirar(p);
        return p;
    }

    public static JPanel tarjeta(String titulo) {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(titulo),
                BorderFactory.createEmptyBorder(2, 6, 6, 6)));
        return p;
    }

    public static JPanel fondo() {
        return new JPanel();
    }

public static DefaultTableModel modelo(String[] columnas) {
        return modelo(columnas, new int[0]);
    }

    public static DefaultTableModel modelo(String[] columnas, int... editables) {
        final int[] lista = editables == null ? new int[0] : editables;
        return new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                for (int i = 0; i < lista.length; i++) {
                    if (lista[i] == column) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static JTable tabla(DefaultTableModel modelo) {
        JTable t = new JTable(modelo);
        t.setFont(new Font(Font.DIALOG, Font.PLAIN, TAMANO));
        t.setRowHeight(24);
        t.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        return t;
    }

    public static JScrollPane scroll(JComponent c) {
        return new JScrollPane(c);
    }

    public static void alinearDerecha(JTable t, int... columnas) {
        javax.swing.table.TableCellRenderer r = new javax.swing.table.DefaultTableCellRenderer();
        ((javax.swing.table.DefaultTableCellRenderer) r).setHorizontalAlignment(JLabel.RIGHT);
        for (int i = 0; i < columnas.length; i++) {
            if (columnas[i] < t.getColumnCount()) {
                t.getColumnModel().getColumn(columnas[i]).setCellRenderer(r);
            }
        }
    }

    public static void alinearCentro(JTable t, int... columnas) {
        javax.swing.table.TableCellRenderer r = new javax.swing.table.DefaultTableCellRenderer();
        ((javax.swing.table.DefaultTableCellRenderer) r).setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < columnas.length; i++) {
            if (columnas[i] < t.getColumnCount()) {
                t.getColumnModel().getColumn(columnas[i]).setCellRenderer(r);
            }
        }
    }

    public static JPanel barra() {
        JPanel p = new JPanel(new java.awt.BorderLayout(10, 0));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return p;
    }

    public static JLabel deBarra(String contenido, int size, boolean fuerte) {
        JLabel l = new JLabel(contenido);
        l.setFont(new Font(Font.DIALOG, fuerte ? Font.BOLD : Font.PLAIN, size));
        return l;
    }

    public static JButton botonBarra(String contenido) {
        JButton b = new JButton(contenido);
        b.setFont(new Font(Font.DIALOG, Font.PLAIN, TAMANO));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton volver() {
        return botonBarra("Volver");
    }

    public static JPanel cabecera(String titulo, JComponent derecha) {
        JPanel p = new JPanel(new java.awt.BorderLayout(10, 0));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        JLabel l = deBarra(titulo, 16, true);
        p.add(l, java.awt.BorderLayout.WEST);
        if (derecha != null) {
            JPanel d = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            d.add(derecha);
            p.add(d, java.awt.BorderLayout.EAST);
        }
        return p;
    }

    public static JLabel mensaje(String contenido) {
        JLabel l = new JLabel(contenido.isEmpty() ? " " : contenido);
        l.setFont(new Font(Font.DIALOG, Font.PLAIN, TAMANO - 1));
        l.setForeground(textoPorDefecto());
        return l;
    }

    public static void mensaje(JLabel l, String contenido, boolean error) {
        l.setText(contenido.isEmpty() ? " " : contenido);
        l.setFont(new Font(Font.DIALOG, error ? Font.BOLD : Font.PLAIN, TAMANO - 1));
        l.setForeground(textoPorDefecto());
    }

    public static void info(java.awt.Component padre, String contenido) {
        JOptionPane.showMessageDialog(padre, contenido, "Book Center",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(java.awt.Component padre, String contenido) {
        JOptionPane.showMessageDialog(padre, contenido, "Revise los datos",
                JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirmar(java.awt.Component padre, String contenido) {
        int r = JOptionPane.showConfirmDialog(padre, contenido, "Confirmacion",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return r == JOptionPane.YES_OPTION;
    }
}
