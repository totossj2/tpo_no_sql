package codigo.logica.pedidos;

import codigo.logica.Usuario;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Carrito {
    private String promocion;
    private ArrayList<DetalleCarrito> carrito;
    private Usuario comprador;
    private int idCarrito;
    private LocalDate fechaCreacion;

    public Carrito(int idCarrito, Usuario comprador, String promocion, LocalDate fechaCreacion) {
        this.idCarrito = idCarrito;
        this.promocion = promocion;
        this.comprador = comprador;
        this.fechaCreacion = fechaCreacion;
    }

    public void crearCarrito() {
        carrito = new ArrayList<DetalleCarrito>();
    }

    public void agregarArticulo(String nombreArticulo, int cantidad, int precioUnitario) {
        if (carrito == null) {
            crearCarrito();
        }
        DetalleCarrito detalle = new DetalleCarrito(nombreArticulo, cantidad, precioUnitario);
        carrito.add(detalle);
    }

    public void eliminarArticulo(String nombreArticulo) {
        for (DetalleCarrito detalle : carrito) {
            if (detalle.getNombreArticulo().equals(nombreArticulo)) {
                carrito.remove(detalle);
                break;
            }
        }
    }

    public List<Document> adaptarCarrito() {
        List<Document> carritoAdaptado = new ArrayList<>();
        for (DetalleCarrito detalle : carrito) {
            Document detalleDocument = new Document("nombreArticulo", detalle.getNombreArticulo())
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

    public String getPromocion() {
        return promocion;
    }

    public void getArticulos() {
        for (DetalleCarrito detalle : carrito) {
            System.out.println(detalle.getNombreArticulo() + " " + detalle.getCantidad() + " " + detalle.getPrecioUnitario() + " " + detalle.getPrecioTotal());
        }
    }

    public static Carrito fromDocument(Document document) {
        Map<String, String> carritoData = new HashMap<>();
        carritoData.put("idCarrito", document.getInteger("idCarrito").toString());
        carritoData.put("promocion", document.getString("promocion"));
        carritoData.put("fechaCreacion", document.getDate("fechaCreacion").toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());
        //Usuario comprador = Usuario.fromJson(document.getString("usuario"));
        Usuario comprador = Usuario.fromDocument((Document) document.get("usuario"));
        Carrito carro = new Carrito(Integer.parseInt(carritoData.get("idCarrito")), comprador, carritoData.get("promocion"), LocalDate.parse(carritoData.get("fechaCreacion")));

        //List<DetalleCarrito> articulos = carro.articulosFromDocument((Document) document.get("articulos"));


        List<Document> articulos = (List<Document>) document.get("articulos");


        for (Document articulo : articulos) {
            carro.agregarArticulo(articulo.getString("nombreArticulo"), articulo.getInteger("cantidad"), articulo.getInteger("precioUnitario"));
        }

        return carro;
    }


    public void recuperarEstado(Document document) {
        List<Document> articulos = (List<Document>) document.get("articulos");
        carrito.clear();
        for (Document articulo : articulos) {
            DetalleCarrito detalle = new DetalleCarrito(articulo.getString("nombreArticulo"), articulo.getInteger("cantidad"), articulo.getInteger("precioUnitario"));
            agregarArticulo(detalle.getNombreArticulo(), detalle.getCantidad(), detalle.getPrecioUnitario());
        }
    }

    public Pedido confirmarPedido() {
        Pedido pedido = new Pedido(idCarrito, comprador, promocion, LocalDate.now());
        for (DetalleCarrito detalle : carrito) {
            pedido.agregarArticulo(detalle.getNombreArticulo(), detalle.getCantidad(), detalle.getPrecioUnitario());
        }
        return pedido;
    }
}
