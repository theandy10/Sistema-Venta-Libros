package venta.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class CampoSugerido extends JTextField {

    private static final long serialVersionUID = 1L;

    private static final int MAXIMO = 8;

    private final JPopupMenu popup = new JPopupMenu();
    private final JList<String> lista = new JList<String>();
    private final ArrayList<String> textos = new ArrayList<String>();
    private final ArrayList<String> valores = new ArrayList<String>();
    private Runnable alElegir;

    private boolean silenciar;

    public CampoSugerido(int columnas) {
        setFont(Estilo.campo(columnas).getFont());
        setForeground(Estilo.TEXTO);

        setPreferredSize(new Dimension(220, Estilo.ALTO_CAMPO));

        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFont(getFont());
        lista.setVisibleRowCount(MAXIMO);
        lista.setFixedCellHeight(26);
        popup.setBorder(javax.swing.BorderFactory.createLineBorder(Estilo.BORDE));
        popup.setLightWeightPopupEnabled(true);

        popup.setFocusable(false);
        lista.setFocusable(false);
        popup.add(lista);

        lista.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                elegir();
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                Component destino = e.getOppositeComponent();
                if (destino != null && javax.swing.SwingUtilities.isDescendingFrom(popup, destino)) {
                    return;
                }
                popup.setVisible(false);
            }
        });

        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarSugerencias();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarSugerencias();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarSugerencias();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (popup.isVisible() && lista.getSelectedIndex() >= 0) {
                        elegir();
                    }
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN && popup.isVisible()) {
                    lista.setSelectedIndex(Math.min(lista.getSelectedIndex() + 1,
                            lista.getModel().getSize() - 1));
                    e.consume();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP && popup.isVisible()) {
                    lista.setSelectedIndex(Math.max(lista.getSelectedIndex() - 1, 0));
                    e.consume();
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    popup.setVisible(false);
                }
            }
        });
    }

    public final void setSugerencias(ArrayList<String> aMostrar, ArrayList<String> aEscribir) {
        textos.clear();
        valores.clear();
        if (aMostrar != null) {
            textos.addAll(aMostrar);
        }
        if (aEscribir != null) {
            valores.addAll(aEscribir);
        }
    }

    public void setAlElegir(Runnable r) {
        this.alElegir = r;
    }

    public void cerrarSugerencias() {
        popup.setVisible(false);
    }

    private void actualizarSugerencias() {
        if (silenciar) {
            return;
        }
        String escrito = getText().trim().toLowerCase();
        DefaultListModel<String> modelo = new DefaultListModel<String>();
        for (int i = 0; i < textos.size() && modelo.size() < MAXIMO; i++) {
            String s = textos.get(i);
            if (!escrito.isEmpty() && s.toLowerCase().contains(escrito)) {
                modelo.addElement(s);
            }
        }
        lista.setModel(modelo);
        if (modelo.isEmpty()) {
            popup.setVisible(false);
            return;
        }
        lista.setSelectedIndex(0);

        int alto = modelo.size() * 26 + 8;
        popup.setSize(new Dimension(Math.max(getWidth(), 180), alto));
        popup.show(this, 0, getHeight());
    }

    private void elegir() {
        int indice = lista.getSelectedIndex();
        popup.setVisible(false);
        if (indice < 0 || indice >= lista.getModel().getSize()) {
            return;
        }

        String elegido = lista.getModel().getElementAt(indice);
        String valor = elegido;
        int i = textos.indexOf(elegido);
        if (i >= 0) {
            valor = i < valores.size() ? valores.get(i) : elegido;
        }

        silenciar = true;
        try {
            setText(valor);
        } finally {
            silenciar = false;
        }
        popup.setVisible(false);
        requestFocusInWindow();
        if (alElegir != null) {
            alElegir.run();
        }
    }

    public void limpiar() {
        setText("");
        popup.setVisible(false);
    }

    public boolean sinSugerencias() {
        return !popup.isVisible();
    }
}
