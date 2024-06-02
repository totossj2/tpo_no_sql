package Puntos;

import codigo.connections.PoolMongoDB;
import codigo.connections.PoolRedis;
import codigo.exceptions.ErrorConnectionMongoException;
import codigo.exceptions.ErrorConnectionRedisException;
import codigo.logica.Usuario;
import codigo.logica.pedidos.Articulo;
import codigo.logica.pedidos.Carrito;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class Punto3 {
    public static void main(String[] args) {

        try {
            MongoCollection<Document> carritos = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("carritos");
            MongoCollection<Document> logsCarrito = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("logsCarrito");

            Jedis j = PoolRedis.getInstancia().getConnection();

            String usuarioKey = "usuario:1";
            Map<String, String> userData = j.hgetAll(usuarioKey);

            Usuario comprador = Usuario.fromMap(userData);

            Articulo proteina = new Articulo("Proteina", 1000);
            Articulo creatina = new Articulo("Creatina", 1500);
            Articulo glutamina = new Articulo("Glutamina", 2000);
            Articulo preEntreno = new Articulo("Pre Entreno", 2500);

            Carrito carrito = new Carrito(1, comprador, "10% off", LocalDate.now());
            carrito.crearCarrito();

            Document carritoDocument = new Document("idCarrito", 1).append("usuario", userData).append("articulos", carrito.adaptarCarrito()).
                    append("fechaCreacion", carrito.getFechaCreacion()).append("promocion", carrito.getPromocion());

            Document logsDocument = new Document("idLog", 1).append("idCarrito", 1).append("articulos", carrito.adaptarCarrito()).
                   append("horaActualizacion", LocalDateTime.now());

            carritos.insertOne(carritoDocument);
            logsCarrito.insertOne(logsDocument);

            carrito.agregarArticulo(proteina.getNombre(), 2, proteina.getPrecio());
            carritos.updateOne(Filters.eq("idCarrito", 1), new Document("$set", new Document("articulos", carrito.adaptarCarrito())));

            logsDocument = new Document("idLog", 2).append("idCarrito", 1).append("articulos", carrito.adaptarCarrito()).
                    append("horaActualizacion", LocalDateTime.now());
            logsCarrito.insertOne(logsDocument);

            carrito.agregarArticulo(creatina.getNombre(), 1, creatina.getPrecio());
            carritos.updateOne(Filters.eq("idCarrito", 1), new Document("$set", new Document("articulos", carrito.adaptarCarrito())));

            logsDocument = new Document("idLog", 3).append("idCarrito", 1).append("articulos", carrito.adaptarCarrito()).
                    append("horaActualizacion", LocalDateTime.now());
            logsCarrito.insertOne(logsDocument);

            carrito.agregarArticulo(glutamina.getNombre(), 1, glutamina.getPrecio());
            carritos.updateOne(Filters.eq("idCarrito", 1), new Document("$set", new Document("articulos", carrito.adaptarCarrito())));

            logsDocument = new Document("idLog", 4).append("idCarrito", 1).append("articulos", carrito.adaptarCarrito()).
                    append("horaActualizacion", LocalDateTime.now());
            logsCarrito.insertOne(logsDocument);

            carrito.agregarArticulo(preEntreno.getNombre(), 1, preEntreno.getPrecio());
            carritos.updateOne(Filters.eq("idCarrito", 1), new Document("$set", new Document("articulos", carrito.adaptarCarrito())));

            logsDocument = new Document("idLog", 5).append("idCarrito", 1).append("articulos", carrito.adaptarCarrito()).
                    append("horaActualizacion", LocalDateTime.now());
            logsCarrito.insertOne(logsDocument);

            carrito.eliminarArticulo(proteina.getNombre());
            carritos.updateOne(Filters.eq("idCarrito", 1), new Document("$set", new Document("articulos", carrito.adaptarCarrito())));

            logsDocument = new Document("idLog", 6).append("idCarrito", 1).append("articulos", carrito.adaptarCarrito()).
                    append("horaActualizacion", LocalDateTime.now());
            logsCarrito.insertOne(logsDocument);

            carrito.getArticulos();


        } catch (ErrorConnectionMongoException | ErrorConnectionRedisException e) {
            e.printStackTrace();
        } finally {
            PoolMongoDB.closeConnection();
            PoolRedis.closeConnection();
        }

    }
}
