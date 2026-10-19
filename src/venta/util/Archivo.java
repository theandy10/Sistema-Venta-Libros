package venta.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class Archivo {

    private Archivo() {

    }

    public static boolean crearCarpeta(String ruta) {
        File f = new File(ruta);
        return f.exists() || f.mkdirs();
    }

    public static List<String> leer(String ruta) {
        List<String> lineas = new ArrayList<String>();
        File f = new File(ruta);
        if (!f.exists()) {
            return lineas;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new java.io.FileInputStream(f), StandardCharsets.UTF_8));
            String linea = br.readLine();
            while (linea != null) {
                if (!linea.trim().isEmpty()) {
                    lineas.add(linea);
                }
                linea = br.readLine();
            }
        } catch (IOException e) {

        } finally {
            cerrar(br);
        }
        return lineas;
    }

    public static boolean escribir(String ruta, List<String> lineas) {
        Writer w = null;
        try {
            w = new OutputStreamWriter(
                    new FileOutputStream(ruta), StandardCharsets.UTF_8);
            for (int i = 0; i < lineas.size(); i++) {
                w.write(lineas.get(i));
                w.write("\n");
            }
            w.flush();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            cerrar(w);
        }
    }

    public static String[] partir(String linea) {
        if (linea == null) {
            return null;
        }
        String[] partes = linea.split("\\|", -1);
        for (int i = 0; i < partes.length; i++) {
            partes[i] = partes[i].trim();
        }
        return partes;
    }

    public static String unir(String... partes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < partes.length; i++) {
            if (i > 0) {
                sb.append('|');
            }
            String p = partes[i] == null ? "" : partes[i];

            sb.append(p.replace('|', '-').trim());
        }
        return sb.toString();
    }

    private static void cerrar(java.io.Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {

            }
        }
    }
}
