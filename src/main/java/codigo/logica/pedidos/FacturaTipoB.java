package codigo.logica.pedidos;

import java.util.List;


public class FacturaTipoB extends Factura{
    boolean liquidaIVA = false;
    public FacturaTipoB (int numeroFactura, String nombreCompleto, List<DetalleCarrito> productos, int promocion){
        super(numeroFactura, nombreCompleto, productos , promocion);
        subtotal = calcularSubtotal(productos);
        total = aplicarDescuento(subtotal, promocion);
    }

    public String getTipo(){
        return "B";
    }



    public void mostrarFactura (){
        System.out.println("Numero de factura: "+numeroFactura);
        System.out.println("Tipo B");
        System.out.println("Nombre: "+nombreCompleto);
        System.out.println("Fecha emision: "+fechaEmision);
        System.out.println("Fecha vencimiento: "+fechaVencimiento);
        System.out.println("Detalles: ");
        for (DetalleCarrito detalle : productos) {
            System.out.println("Nombre producto: "+detalle.getNombreArticulo()+" Cantidad: "+detalle.getCantidad()+" Precio unitario: "+detalle.getPrecioUnitario()
                    +" Precio total: "+detalle.getPrecioTotal());
        }
        System.out.println("Descuento aplicado: "+(total-subtotal));
        System.out.println("Total: "+total);
    }

}
