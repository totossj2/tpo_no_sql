package Puntos;

import codigo.connections.PoolMongoDB;
import codigo.connections.PoolRedis;
import codigo.exceptions.ErrorConnectionMongoException;
import codigo.exceptions.ErrorConnectionRedisException;
import codigo.logica.pedidos.Carrito;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.Map;

public class Punto4 {
    //recuperar carritos a partir de mongodb
    //recuperar estados anteriores de carritos a partir de mongo
    public static void main(String[] args) {
        try {
            MongoCollection<Document> carritos = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("carritos");
            MongoCollection<Document> logsCarrito = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("logsCarrito");

            //recuperar carritos con idCarrito 1
            Document carritoDocument = carritos.find(Filters.eq("idCarrito", 1)).first();

            Carrito carrito = Carrito.fromDocument(carritoDocument);


            System.out.println("Carrito:");
            carrito.getArticulos();


            //recuperar estados anteriores de carritos
            System.out.println("Estado anterior:");
            Document logsDocument = logsCarrito.find(Filters.eq("idLog", 5)).first();

            //cambio al estado anterior
            carrito.recuperarEstado(logsDocument);

            carrito.getArticulos();

        } catch (ErrorConnectionMongoException e) {
            e.printStackTrace();
        } finally {
            PoolMongoDB.closeConnection();
        }
    }
}
