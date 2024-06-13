package codigo.logica.pedidos;

import java.util.List;


public class FacturaTipoA extends Factura{
    boolean liquidaIVA = true;
    int neto;
    int iva;
    int promocion;

    public FacturaTipoA (int numeroFactura, String nombreCompleto, List<DetalleCarrito> productos, int promocion){
        super(numeroFactura, nombreCompleto, productos , promocion);
        this.promocion = promocion;
        subtotal = calcularSubtotal(productos);
        total = (int) aplicarDescuento(subtotal, promocion);
        neto = calcularNeto(total);
        iva = total - neto;
    }

    
    private int calcularNeto(int total) {
        int x = (int) (total / 1.21);
        return x;
    }

    public String getTipo(){
        return "A";
    }


    public void mostrarFactura (){
        System.out.println("Numero de factura: "+numeroFactura);
        System.out.println("Tipo A");
        System.out.println("Nombre: "+nombreCompleto);
        System.out.println("Fecha emision: "+fechaEmision);
        System.out.println("Fecha vencimiento: "+fechaVencimiento);
        System.out.println("Detalles: ");
        for (DetalleCarrito detalle : productos) {
            System.out.println("Nombre producto: "+detalle.getNombreArticulo()+" Cantidad: "+detalle.getCantidad()+" Precio unitario: "+detalle.getPrecioUnitario()
            +" Precio total: "+detalle.getPrecioTotal());
        }
        System.out.println("Descuento aplicado: "+(total-subtotal));
        System.out.println("Precio neto: "+neto);
        System.out.println("IVA 21%: "+iva);
        System.out.println("Total: "+total);
    }
    
    



}
