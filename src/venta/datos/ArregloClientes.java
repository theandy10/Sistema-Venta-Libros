package venta.datos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import venta.modelo.Cliente;
import venta.util.Archivo;
import venta.util.Validador;

public class ArregloClientes {

    public static final int CODIGO_INICIAL = 1001;

    private ArrayList<Cliente> cli;
    private final String archivo;
    private final ArregloVentas arrVen;

    public ArregloClientes(String carpeta, ArregloVentas arrVen) {
        this.archivo = carpeta + File.separator + "clientes.txt";
        this.arrVen = arrVen;
        this.cli = new ArrayList<Cliente>();
        cargar();
        ordenarPorCodigo();
    }

    public void adicionar(Cliente x) {
        cli.add(x);
        ordenarPorCodigo();
    }

    public Cliente eliminar(Cliente x) {
        int pos = cli.indexOf(x);
        return pos >= 0 ? cli.remove(pos) : null;
    }

    public int tamaño() {
        return cli.size();
    }

    public Cliente obtener(int pos) {
        return cli.get(pos);
    }

    public Cliente buscar(String dni) {
        String d = Validador.limpio(dni);
        for (int i = 0; i < cli.size(); i++) {
            if (cli.get(i).getDni().equals(d)) {
                return cli.get(i);
            }
        }
        return null;
    }

    public boolean existeDni(String dni) {
        return buscar(dni) != null;
    }

    public ArrayList<String[]> paraSugerencias() {
        ArrayList<String[]> r = new ArrayList<String[]>();
        for (int i = 0; i < cli.size(); i++) {
            Cliente x = cli.get(i);
            r.add(new String[]{x.getDni(), x.getDni() + " · " + x.nombreCompleto()});
        }
        return r;
    }

    public Cliente buscarPorCodigo(int codigo) {
        for (int i = 0; i < cli.size(); i++) {
            if (cli.get(i).getCodigo() == codigo) {
                return cli.get(i);
            }
        }
        return null;
    }

    public ArrayList<Cliente> buscarPorTexto(String texto) {
        String t = Validador.limpio(texto).toLowerCase();
        ArrayList<Cliente> r = new ArrayList<Cliente>();
        if (t.isEmpty()) {
            return todos();
        }
        for (int i = 0; i < cli.size(); i++) {
            Cliente x = cli.get(i);
            if (x.getDni().contains(t)
                    || x.getNombres().toLowerCase().contains(t)
                    || x.getApellidos().toLowerCase().contains(t)) {
                r.add(x);
            }
        }
        return r;
    }

    public ArrayList<Cliente> todos() {
        return new ArrayList<Cliente>(cli);
    }

    public int generarCodigo() {
        if (cli.isEmpty()) {
            return CODIGO_INICIAL;
        }
        int max = cli.get(0).getCodigo();
        for (int i = 1; i < cli.size(); i++) {
            if (cli.get(i).getCodigo() > max) {
                max = cli.get(i).getCodigo();
            }
        }
        return max + 1;
    }

    public void ordenarPorCodigo() {
        for (int i = 1; i < cli.size(); i++) {
            Cliente actual = cli.get(i);
            int j = i - 1;
            while (j >= 0 && cli.get(j).getCodigo() > actual.getCodigo()) {
                cli.set(j + 1, cli.get(j));
                j--;
            }
            cli.set(j + 1, actual);
        }
    }

    public boolean tieneVentas(Cliente x) {
        return arrVen != null && arrVen.tieneVentas(x.getDni());
    }

    public int numeroVentas(Cliente x) {
        return arrVen == null ? 0 : arrVen.ventasDeCliente(x.getDni()).size();
    }

    private void cargar() {
        List<String> lineas = Archivo.leer(archivo);
        for (int i = 0; i < lineas.size(); i++) {
            try {
                String[] s = Archivo.partir(lineas.get(i));
                if (s.length < 5) {
                    continue;
                }
                int codigo = Integer.parseInt(s[0]);
                int celular = Integer.parseInt(s[4]);
                cli.add(new Cliente(codigo, s[1], s[2], s[3], celular));
            } catch (NumberFormatException e) {

            }
        }
    }

    public boolean grabar() {
        List<String> lineas = new ArrayList<String>();
        for (int i = 0; i < cli.size(); i++) {
            Cliente x = cli.get(i);
            lineas.add(Archivo.unir(
                    String.valueOf(x.getCodigo()),
                    x.getDni(),
                    x.getNombres(),
                    x.getApellidos(),
                    String.valueOf(x.getCelular())));
        }
        return Archivo.escribir(archivo, lineas);
    }

    public String getArchivo() {
        return archivo;
    }
}
