package venta.datos;

import java.io.File;
import venta.util.Archivo;

public final class Sistema {

    private static Sistema instancia;

    private final String carpeta;
    private final ArregloDetalle arrDet;
    private final ArregloLibros arrLib;
    private final ArregloVentas arrVen;
    private final ArregloClientes arrCli;
    private final ReporteVentas rep;

    private Sistema(String carpeta) {
        this.carpeta = carpeta;
        this.arrDet = new ArregloDetalle(carpeta);
        this.arrLib = new ArregloLibros(carpeta, arrDet);
        this.arrVen = new ArregloVentas(carpeta, arrLib, arrDet);
        this.arrCli = new ArregloClientes(carpeta, arrVen);
        this.rep = new ReporteVentas(arrLib, arrCli, arrVen, arrDet);
    }

    public static Sistema get() {
        if (instancia == null) {
            File f = new File("datos");
            Archivo.crearCarpeta(f.getAbsolutePath());
            instancia = new Sistema(f.getAbsolutePath());
        }
        return instancia;
    }

    public static void reiniciar() {
        instancia = null;
    }

    public boolean grabarTodo() {
        boolean ok = arrDet.grabar();
        ok = arrLib.grabar() && ok;
        ok = arrVen.grabar() && ok;
        ok = arrCli.grabar() && ok;
        return ok;
    }

    public ArregloLibros getLibros() {
        return arrLib;
    }

    public ArregloClientes getClientes() {
        return arrCli;
    }

    public ArregloVentas getVentas() {
        return arrVen;
    }

    public ArregloDetalle getDetalles() {
        return arrDet;
    }

    public ReporteVentas getReporte() {
        return rep;
    }

    public String getCarpeta() {
        return carpeta;
    }
}
