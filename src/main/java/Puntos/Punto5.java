package Puntos;

import codigo.connections.PoolMongoDB;
import codigo.connections.PoolRedis;
import codigo.exceptions.ErrorConnectionMongoException;
import codigo.exceptions.ErrorConnectionRedisException;
import codigo.logica.Usuario;
import codigo.logica.pedidos.Carrito;
import codigo.logica.pedidos.Pedido;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;


public class Punto5 {
    public static void main(String[] args) {
        try {
            MongoCollection<Document> pedidos = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("pedidos");

            Usuario comprador = new Usuario(3, "Lucas", "Ellero", 45111234, "comprador@gmail.com", null);
            Carrito carrito = new Carrito(2, comprador, "10% off", LocalDate.now());

            comprador.logIn();


            carrito.agregarArticulo("Guantes de boxeo", 1, 15000);
            carrito.agregarArticulo("Bolsa de boxeo", 1, 50000);

            Pedido pedido = carrito.confirmarPedido();

            comprador.logOut();

            Document usuarioDocument = new Document("idUsuario", comprador.getIdUsuario()).append("nombre", comprador.getNombre()).append("apellido", comprador.getApellido()).
                    append("dni", comprador.getDni()).append("mail", comprador.getMail()).append("duracionSesion", comprador.getSesion());



            Document pedidoDocument = new Document("idPedido", pedido.getId()).append("usuario", usuarioDocument).append("articulos", pedido.adaptarArticulos()).
                    append("fechaPedido", pedido.getFechaPedido()).append("promocion", pedido.getPromocion()).append("total", pedido.getMontoTotal());

            pedidos.insertOne(pedidoDocument);


        } catch (ErrorConnectionMongoException e) {
            e.printStackTrace();
        } finally {
            PoolMongoDB.closeConnection();
        }
    }
}
