package codigo.logica.pedidos;

import codigo.logica.Usuario;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Carrito {
    private int promocion;
    private ArrayList<DetalleCarrito> carrito;
    private Usuario comprador;
    private int idCarrito;
    private LocalDate fechaCreacion;

    public Carrito(int idCarrito,  Usuario comprador, int promocion, LocalDate fechaCreacion) {
        this.idCarrito = idCarrito;
        this.promocion = promocion;
        this.comprador = comprador;
        this.fechaCreacion = fechaCreacion;
    }

    public int getId(){
        return idCarrito;
    }

    public void crearCarrito() {
        carrito = new ArrayList<DetalleCarrito>();
    }

    public void agregarArticulo(int idArticulo, String nombreArticulo, int cantidad, int precioUnitario) {
        if (carrito == null) {
            crearCarrito();
        }
        DetalleCarrito detalle = new DetalleCarrito(idArticulo, nombreArticulo, cantidad, precioUnitario);
        carrito.add(detalle);
    }

    public void eliminarArticulo(int idProducto) {
        for (DetalleCarrito detalle : carrito) {
            if (detalle.getIdProducto() ==(idProducto)) {
                carrito.remove(detalle);
                break;
            }
        }
    }

    public List<Document> adaptarCarrito() {
        List<Document> carritoAdaptado = new ArrayList<>();
        for (DetalleCarrito detalle : carrito) {
            Document detalleDocument = new Document("nombreArticulo", detalle.getNombreArticulo())
                    .append("idProducto", detalle.getIdProducto())
                    .append("cantidad", detalle.getCantidad())
                    .append("precioUnitario", detalle.getPrecioUnitario())
                    .append("precioTotal", detalle.getPrecioTotal());
            carritoAdaptado.add(detalleDocument);
        }
        return carritoAdaptado;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public int getPromocion() {
        return promocion;
    }

    public List<String> getListadoArticulos() {
        List<String> listado = new ArrayList<>();
        for (DetalleCarrito detalle : carrito) {
            listado.add(detalle.getIdProducto()+"-"+detalle.getNombreArticulo());
        }
        return listado;
    }

    public void getArticulos() {
        for (DetalleCarrito detalle : carrito) {
            System.out.println(detalle.getNombreArticulo() + " " + detalle.getCantidad() + " " + detalle.getPrecioUnitario() + " " + detalle.getPrecioTotal());
        }
    }

    public static Carrito fromDocument(Document document, Usuario comprador) {
        Map<String, String> carritoData = new HashMap<>();
        carritoData.put("idCarrito", document.getString("idCarrito"));
        carritoData.put("promocion", document.getString("promocion").substring(0,document.getString("promocion").length()-1 ));
        carritoData.put("fechaCreacion", document.getDate("fechaCreacion").toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());
        Carrito carro = new Carrito(Integer.parseInt(carritoData.get("idCarrito")),  comprador, Integer.parseInt(carritoData.get("promocion")), LocalDate.parse(carritoData.get("fechaCreacion")));

        //List<DetalleCarrito> articulos = carro.articulosFromDocument((Document) document.get("articulos"));


        List<Document> articulos = (List<Document>) document.get("articulos");


        for (Document articulo : articulos) {
            carro.agregarArticulo(articulo.getInteger("idProducto"),  articulo.getString("nombreArticulo"), articulo.getInteger("cantidad"), articulo.getInteger("precioUnitario"));
        }

        return carro;
    }


    public void recuperarEstado(Document document) {
        List<Document> articulos = (List<Document>) document.get("articulos");
        carrito.clear();
        for (Document articulo : articulos) {
            DetalleCarrito detalle = new DetalleCarrito(articulo.getInteger("idProducto"), articulo.getString("nombreArticulo"), articulo.getInteger("cantidad"), articulo.getInteger("precioUnitario"));
            agregarArticulo(detalle.getIdProducto(), detalle.getNombreArticulo(), detalle.getCantidad(), detalle.getPrecioUnitario());
        }
    }

    public boolean promoValida(){
        Long diferenciaEnDias = ChronoUnit.DAYS.between(fechaCreacion, LocalDate.now());
        if (diferenciaEnDias > 3){
            return false;
        }
        else{
            return true;
        }
    }

    public Pedido confirmarPedido() {
        Pedido pedido;
        if (promoValida()==true) {
            pedido = new Pedido(idCarrito, comprador, promocion, LocalDate.now());
        } else {
            pedido = new Pedido(idCarrito, comprador, 0,LocalDate.now());
        }

        for (DetalleCarrito detalle : carrito) {
            pedido.agregarArticulo(detalle.getIdProducto(), detalle.getNombreArticulo(), detalle.getCantidad(), detalle.getPrecioUnitario());
        }
        return pedido;
    }
}
