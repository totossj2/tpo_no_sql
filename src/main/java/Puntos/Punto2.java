package Puntos;

import codigo.connections.PoolMongoDB;
import codigo.connections.PoolRedis;
import codigo.exceptions.ErrorConnectionMongoException;
import codigo.exceptions.ErrorConnectionRedisException;
import codigo.logica.Usuario;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class Punto2 {
    public static void main(String[] args) {
        try {
            MongoCollection<Document> logs = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("logs");

            Jedis j = PoolRedis.getInstancia().getConnection();


            Document log;

            String usuarioKey = "usuario:1";
            //recupero desde redis el usuario con id 1
            Map<String, String> userData = j.hgetAll(usuarioKey);

            //lo hago objeto
            Usuario cliente1 = Usuario.fromMap(userData);

            System.out.println(cliente1.getNombre());

            cliente1.logIn();
            cliente1.logOut();

            log = cliente1.getLog();

            logs.insertOne(log);

            System.out.println(cliente1.getCategoria());


        } catch (ErrorConnectionMongoException | ErrorConnectionRedisException e) {
            e.printStackTrace();
        } finally {
            PoolMongoDB.closeConnection();
            PoolRedis.closeConnection();
        }
    }
}
