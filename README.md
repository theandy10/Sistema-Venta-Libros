# Sistema de Venta de Libros - Libreria Virtual

Proyecto de investigacion aplicada del curso **Algoritmos y Estructura de Datos** (Cibertec). Sistema de escritorio en Java (Swing) para la gestion comercial de una libreria: catalogo de libros, clientes, ventas con detalle, cobro, boleta y reportes.

## Funcionalidades

- Mantenimiento de libros y clientes (altas, consultas, modificaciones y bajas con confirmacion).
- Registro de ventas con detalle, descuento, metodo de pago y vuelto automatico.
- Anulacion de ventas con devolucion de stock.
- Consulta de libros, clientes y ventas con sugerencias al escribir.
- Boleta de venta en texto, con IGV incluido, guardada en `datos/boletas/` e imprimible.
- Reportes: ventas por dia, stock minimo, libros mas vendidos, ventas por cliente e ingresos por categoria.
- Persistencia en archivos de texto dentro de `datos/`.

## Requisitos

- JDK 8 o superior (probado con JDK 26).

## Compilar y ejecutar (Windows)

Desde la carpeta del proyecto:

```bat
javac -encoding UTF-8 -d build src\venta\modelo\*.java src\venta\util\*.java src\venta\control\*.java src\venta\datos\*.java src\venta\gui\*.java
java -Dfile.encoding=UTF-8 -cp build venta.gui.Login
```

Tambien se puede abrir como proyecto Java en Eclipse o IntelliJ.

## Usuarios de prueba

| Usuario  | Contrasena | Perfil        |
|----------|------------|---------------|
| admin    | admin123   | Administrador |
| vendedor | venda123   | Vendedor      |

## Estructura

```
src/venta/modelo    Clases del dominio: Libro, Cliente, Venta, DetalleVenta, Usuario
src/venta/datos     Arreglos ArrayList, persistencia, boleta y reportes
src/venta/control   Sesion del usuario
src/venta/util      Utilidades de archivo, dinero, fecha y validacion
src/venta/gui       Ventanas Swing
datos               Archivos de texto con los datos y las boletas
Documentacion       Informe del proyecto
```

## Documentacion

El informe del proyecto esta en `Documentacion/Informe.docx`.
