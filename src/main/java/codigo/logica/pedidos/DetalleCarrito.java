package codigo.logica.pedidos;

public class DetalleCarrito {
    private String nombreArticulo;
    private int cantidad;
    private int precioUnitario;
    private int precioTotal;

    public DetalleCarrito(String nombreArticulo, int cantidad, int precioUnitario) {
        this.nombreArticulo = nombreArticulo;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.precioTotal = cantidad * precioUnitario;
    }

    public String getNombreArticulo() {
        return nombreArticulo;
    }
    public int getCantidad() {
        return cantidad;
    }
    public int getPrecioUnitario() {
        return precioUnitario;
    }
    public int getPrecioTotal() {
        return precioTotal;
    }




}
