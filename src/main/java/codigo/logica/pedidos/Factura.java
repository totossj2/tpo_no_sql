package codigo.logica.pedidos;

import java.time.LocalDate;
import java.util.List;

public abstract class Factura {
    int numeroFactura;
    String nombreCompleto;
    LocalDate fechaEmision;
    LocalDate fechaVencimiento;
    int subtotal;
    List<DetalleCarrito> productos;
    int promocion;
    int total;


    public Factura(int numeroFactura, String nombreCompleto, List<DetalleCarrito> productos, int promocion) {
        this.numeroFactura = numeroFactura;
        this.nombreCompleto = nombreCompleto;
        this.fechaEmision = LocalDate.now();
        this.fechaVencimiento = fechaEmision.plusMonths(1);
        this.productos = productos;
        this.promocion = promocion;


    }

    public void pagar() {
        System.out.println("Se recibio un pago de: "+total);
    }

    public int calcularSubtotal(List<DetalleCarrito> productos) {
        int x = 0;
        for (DetalleCarrito detalle : productos) {
            x += detalle.getPrecioUnitario() * detalle.getCantidad();
        }
        return x;
    }

    protected int aplicarDescuento(int subtotal, int descuento) {
        int x;
        if (descuento == 0) {
            x = subtotal;
        } else {
            float porcentaje = descuento / 100;
            int descontar = (int) (porcentaje * subtotal);
            x = subtotal - descontar;
        }
        return x;
    }
    public void mostrarFactura(){}

    public int getId(){
        return numeroFactura;
    }
    
    public String getTipo(){
        String tipo = "";
        return tipo;
    }

    public int getPromocion(){
        return promocion;
    }

    public LocalDate getFechaEmision(){return fechaEmision;}

    public LocalDate getFechaVencimiento (){
        return fechaVencimiento;
    }
    public int getTotal (){
        return total;
    }


}
