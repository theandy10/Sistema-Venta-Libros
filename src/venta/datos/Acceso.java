package venta.datos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import venta.control.Sesion;
import venta.modelo.Usuario;
import venta.util.Archivo;
import venta.util.Validador;

public class Acceso {

    private final ArrayList<Usuario> usuarios;
    private final String archivo;
    private int intentosFallidos;

    public Acceso(String carpeta) {
        this.archivo = carpeta + File.separator + "usuarios.txt";
        this.usuarios = new ArrayList<Usuario>();
        this.intentosFallidos = 0;
        cargar();
    }

    public Usuario autenticar(String usuario, String clave) {
        if (Validador.vacio(usuario) || clave == null || clave.isEmpty()) {
            return null;
        }
        String u = usuario.trim();
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario x = usuarios.get(i);
            if (x.getUsuario().equalsIgnoreCase(u) && x.getClave().equals(clave)) {
                intentosFallidos = 0;
                return x;
            }
        }
        intentosFallidos++;
        return null;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public ArrayList<Usuario> usuarios() {
        return new ArrayList<Usuario>(usuarios);
    }

    private void cargar() {
        List<String> lineas = Archivo.leer(archivo);
        for (int i = 0; i < lineas.size(); i++) {
            try {
                String[] s = Archivo.partir(lineas.get(i));
                if (s.length < 4) {
                    continue;
                }
                String perfil = Sesion.ADMINISTRADOR.equalsIgnoreCase(s[3])
                        ? Sesion.ADMINISTRADOR : Sesion.VENDEDOR;
                usuarios.add(new Usuario(s[0], s[1], s[2], perfil));
            } catch (RuntimeException e) {

            }
        }
        if (usuarios.isEmpty()) {
            usuarios.add(new Usuario("admin", "admin123",
                    "Luis Torres Mendoza", Sesion.ADMINISTRADOR));
            usuarios.add(new Usuario("vendedor", "venta123",
                    "Carlos Rios Salas", Sesion.VENDEDOR));
            grabar();
        }
    }

    private void grabar() {
        List<String> lineas = new ArrayList<String>();
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);
            lineas.add(Archivo.unir(u.getUsuario(), u.getClave(), u.getNombre(), u.getPerfil()));
        }
        Archivo.escribir(archivo, lineas);
    }
}
