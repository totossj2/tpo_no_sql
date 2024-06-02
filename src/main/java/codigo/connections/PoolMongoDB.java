package codigo.connections;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import codigo.exceptions.ErrorConnectionMongoException;

public class PoolMongoDB {

    private static PoolMongoDB instancia;
    private String url ;
    private static MongoClient mongoClient;

    private PoolMongoDB() {
        url = "mongodb://127.0.01:27017";
        mongoClient = MongoClients.create(url);
    }

    public static PoolMongoDB getInstancia(){
        if(instancia == null)
            instancia = new PoolMongoDB();
        return instancia;
    }

    public MongoDatabase getConnection(String database) throws ErrorConnectionMongoException {
        try {
            MongoDatabase db = mongoClient.getDatabase(database);
            return db;
        }
        catch (Exception e) {
            throw new ErrorConnectionMongoException("Error en la coneccion a MongoDB");
        }
    }

    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Conexión a MongoDB cerrada.");
        }
    }

}