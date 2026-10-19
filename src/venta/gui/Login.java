package venta.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import venta.control.Sesion;
import venta.datos.Acceso;
import venta.datos.Sistema;
import venta.modelo.Usuario;
import venta.util.Validador;

public class Login extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final Sistema sis;
    private final Acceso acceso;
    private final JTextField txtUsuario;
    private final JPasswordField txtClave;
    private final JLabel lblEstado;

    public Login(Sistema sis) {
        this.sis = sis;
        this.acceso = new Acceso(sis.getCarpeta());
        setTitle("Book Center · Iniciar sesión");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(460, 420));
        setLocationRelativeTo(null);
        setResizable(false);

        JLabel lblSistema = new JLabel("Book Center", JLabel.CENTER);
        lblSistema.setFont(new Font(Estilo.FUENTE, Font.BOLD, 30));
        lblSistema.setForeground(Estilo.ACENTO);
        JLabel lblSub = new JLabel("Sistema de venta de libros", JLabel.CENTER);
        lblSub.setForeground(Estilo.SECUNDARIO);

        txtUsuario = Estilo.campo(16);
        txtClave = new JPasswordField(16);

        txtClave.setFont(new Font(Estilo.FUENTE, Font.PLAIN, Estilo.TAMANO));
        txtClave.setPreferredSize(new Dimension(200, Estilo.ALTO_CAMPO));
        lblEstado = new JLabel(" ", JLabel.CENTER);
        lblEstado.setForeground(Estilo.PELIGRO);
        lblEstado.setFont(new Font(Estilo.FUENTE, Font.PLAIN, 13));

        JButton btnIngresar = Estilo.primario("Iniciar sesión");
        btnIngresar.addActionListener(this);
        JButton btnSalir = Estilo.secundario("Salir");
        btnSalir.addActionListener(this);

        JPanel formulario = Estilo.tarjeta("Ingrese sus credenciales");
        formulario.setLayout(new BorderLayout());

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = 0;
        campos.add(Estilo.etiqueta("Usuario"), g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        campos.add(txtUsuario, g);
        g.gridx = 0;
        g.gridy = 1;
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
        campos.add(Estilo.etiqueta("Contraseña"), g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        campos.add(txtClave, g);
        formulario.add(campos, BorderLayout.NORTH);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        botones.setOpaque(false);
        botones.add(btnIngresar);
        botones.add(btnSalir);

        JPanel cabecera = new JPanel();
        cabecera.setOpaque(false);
        cabecera.setLayout(new javax.swing.BoxLayout(cabecera, javax.swing.BoxLayout.Y_AXIS));

        cabecera.setBorder(BorderFactory.createEmptyBorder(18, 26, 16, 26));
        cabecera.add(lblSistema);
        cabecera.add(javax.swing.Box.createVerticalStrut(4));
        cabecera.add(lblSub);
        cabecera.add(javax.swing.Box.createVerticalStrut(16));

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(0, 26, 0, 26));
        centro.add(formulario, BorderLayout.CENTER);
        centro.add(lblEstado, BorderLayout.SOUTH);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);
        sur.setBorder(BorderFactory.createEmptyBorder(0, 26, 18, 26));
        sur.add(botones, BorderLayout.CENTER);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Estilo.FONDO);
        raiz.add(cabecera, BorderLayout.NORTH);
        raiz.add(centro, BorderLayout.CENTER);
        raiz.add(sur, BorderLayout.SOUTH);
        setContentPane(raiz);

        txtClave.addActionListener(this);
        txtUsuario.addActionListener(this);
    }

    private void ingresar() {
        String usuario = Validador.limpio(txtUsuario.getText());
        String clave = new String(txtClave.getPassword());
        if (Validador.vacio(usuario) || clave.isEmpty()) {
            lblEstado.setText("Complete el usuario y la contraseña.");
            return;
        }
        Usuario u = acceso.autenticar(usuario, clave);
        if (u == null) {
            lblEstado.setText("Usuario o contraseña incorrectos. Intentos fallidos: "
                    + acceso.getIntentosFallidos() + ".");
            txtClave.setText("");
            txtClave.requestFocusInWindow();
            return;
        }
        Sesion.iniciar(u);
        sis.grabarTodo();
        dispose();
        new FrmInicio(sis).setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("Iniciar sesión")) {
            ingresar();
        } else if (cmd.equals("Salir")) {
            sis.grabarTodo();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        Estilo.aplicar();
        final Sistema sis = Sistema.get();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Login(sis).setVisible(true);
            }
        });
    }
}
