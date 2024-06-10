package Puntos;

import codigo.connections.PoolMongoDB;
import codigo.exceptions.ErrorConnectionMongoException;
import codigo.logica.pedidos.Catalogo;
import codigo.logica.pedidos.DetalleCatalogo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class Punto10y11 {
    public static void main(String[] args) {

        try {
            MongoCollection catalogo = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("catalogo");
            MongoCollection logsCatalogo = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("logsCatalogo");



            //recupero los documentos de los productos del catalogo
            Document proteinaDocument = (Document) catalogo.find(Filters.eq("nombre", "Proteina")).first();
            Document creatinaDocument = (Document) catalogo.find(Filters.eq("nombre", "Creatina")).first();
            Document glutaminaDocument = (Document) catalogo.find(Filters.eq("nombre", "Glutamina")).first();
            Document preEntrenoDocument = (Document) catalogo.find(Filters.eq("nombre", "Pre Entreno")).first();

            //creo los objetos de los productos
            DetalleCatalogo proteina = DetalleCatalogo.fromDocument(proteinaDocument);
            DetalleCatalogo creatina = DetalleCatalogo.fromDocument(creatinaDocument);
            DetalleCatalogo glutamina = DetalleCatalogo.fromDocument(glutaminaDocument);
            DetalleCatalogo preEntreno = DetalleCatalogo.fromDocument(preEntrenoDocument);

            //instancio el catalogo
            Catalogo catalogoProductos = new Catalogo();
            //cargo el catalogo
            catalogoProductos.agregarArticulo(proteina);
            catalogoProductos.agregarArticulo(creatina);
            catalogoProductos.agregarArticulo(glutamina);
            catalogoProductos.agregarArticulo(preEntreno);

            //cambio el precio de la proteina en el catalogo y guardo el log
            catalogoProductos.actualizarPrecio("Proteina", 3000);
            catalogo.updateOne(Filters.eq("nombre", "Proteina"), new Document("$set", new Document("precio", 3000)));
            logsCatalogo.insertOne(new Document("nombre", proteina.getNombre())
                    .append("tipo", "actualizacion precio")
                    .append("precio anterior", proteina.getPrecioAnterior())
                    .append("precio nuevo", proteina.getPrecio()));

            //cambio el precio de la creatina y guardo el log
            catalogoProductos.actualizarPrecio("Creatina", 2500);
            catalogo.updateOne(Filters.eq("nombre", "Creatina"), new Document("$set", new Document("precio", 2500)));
            logsCatalogo.insertOne(new Document("nombre", creatina.getNombre())
                    .append("tipo", "actualizacion precio")
                    .append("precio anterior", creatina.getPrecioAnterior())
                    .append("precio nuevo", creatina.getPrecio()));

            //cambio el precio de la proteina y guardo el log
            catalogoProductos.actualizarPrecio("Proteina", 2000);
            catalogo.updateOne(Filters.eq("nombre", "Proteina"), new Document("$set", new Document("precio", 2000)));
            logsCatalogo.insertOne(new Document("nombre", proteina.getNombre())
                    .append("tipo", "actualizacion precio")
                    .append("precio anterior", proteina.getPrecioAnterior())
                    .append("precio nuevo", proteina.getPrecio()));


            //cambio el video de la proteina y guardo el log
            catalogoProductos.actualizarVideo("Proteina", "https://www.youtube.com/watch?v=9vKqVkMQHKk");
            catalogo.updateOne(Filters.eq("nombre", "Proteina"), new Document("$set", new Document("video", "https://www.youtube.com/watch?v=9vKqVkMQHKk")));
            logsCatalogo.insertOne(new Document("nombre", proteina.getNombre())
                    .append("tipo", "actualizacion video")
                    .append("video anterior", proteina.getVideoAnterior())
                    .append("video nuevo", proteina.getVideo()));




        } catch (ErrorConnectionMongoException e) {
            e.printStackTrace();
        } finally {
            PoolMongoDB.closeConnection();
        }
    }
}
