package codigo.logica.pedidos;

import codigo.logica.Usuario;
import codigo.logica.pago.Pago;
import org.bson.Document;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pedido {
    String promocion;
    Usuario comprador;
    LocalDate fechaPedido;
    private ArrayList<DetalleCarrito> listaArticulos;
    int idPedido;


    public Pedido (int idPedido, Usuario comprador ,String promocion, LocalDate fechaPedido) {
        this.idPedido = idPedido;
        this.comprador = comprador;
        this.promocion = promocion;
        this.fechaPedido = fechaPedido;
    }

    public void agregarArticulo(String nombreArticulo, int cantidad, int precioUnitario) {
        if (listaArticulos == null) {
            listaArticulos = new ArrayList<DetalleCarrito>();
        }
        DetalleCarrito detalle = new DetalleCarrito(nombreArticulo, cantidad, precioUnitario);
        listaArticulos.add(detalle);
    }

    public static Pedido fromDocument(Document document){
        Map<String, String> pedidoData = new HashMap<>();
        pedidoData.put("idPedido", document.getInteger("idPedido").toString());
        pedidoData.put("promocion", document.getString("promocion"));
        pedidoData.put("fechaPedido", document.getDate("fechaPedido").toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());
        Usuario comprador = Usuario.fromDocument((Document) document.get("usuario"));
        Pedido pedido = new Pedido(Integer.parseInt(pedidoData.get("idPedido")), comprador, pedidoData.get("promocion"), LocalDate.parse(pedidoData.get("fechaPedido")));

        List<Document> articulos = (List<Document>) document.get("articulos");

        for (Document articulo : articulos) {
            pedido.agregarArticulo(articulo.getString("nombreArticulo"), articulo.getInteger("cantidad"), articulo.getInteger("precioUnitario"));
        }

        return pedido;

    }

    public void getArticulos() {
        for (DetalleCarrito detalle : listaArticulos) {
            System.out.println(detalle.getNombreArticulo() + " " + detalle.getCantidad() + " " + detalle.getPrecioUnitario() + " " + detalle.getPrecioTotal());
        }
    }

    public int getMontoTotal() {
        int montoTotal = 0;
        for (DetalleCarrito detalle : listaArticulos) {
            montoTotal += detalle.getPrecioTotal();
        }
        return montoTotal;
    }

    public int getId() {
        return idPedido;
    }

    public Usuario getComprador() {
        return comprador;
    }

    public LocalDate getFechaPedido() {
        return fechaPedido;
    }

    public String getPromocion() {
        return promocion;
    }

    public List<Document> adaptarArticulos() {
        List<Document> articulosAdaptados = new ArrayList<>();
        for (DetalleCarrito detalle : listaArticulos) {
            Document detalleDocument = new Document("nombreArticulo", detalle.getNombreArticulo())
                    .append("cantidad", detalle.getCantidad())
                    .append("precioUnitario", detalle.getPrecioUnitario())
                    .append("precioTotal", detalle.getPrecioTotal());
            articulosAdaptados.add(detalleDocument);
        }
        return articulosAdaptados;
    }




}
