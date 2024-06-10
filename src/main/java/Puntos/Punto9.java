package Puntos;

import codigo.connections.PoolMongoDB;
import codigo.exceptions.ErrorConnectionMongoException;
import codigo.logica.pedidos.Articulo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Punto9 {
    public static void main(String[] args) {
        try {
            Articulo proteina = new Articulo("Proteina", 1000);
            Articulo creatina = new Articulo("Creatina", 1500);
            Articulo glutamina = new Articulo("Glutamina", 2000);
            Articulo preEntreno = new Articulo("Pre Entreno", 2500);


            MongoCollection<Document> catalogo = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("catalogo");
            MongoDatabase database = PoolMongoDB.getInstancia().getConnection("tpo");

            // Crear un bucket GridFS
            GridFSBucket gridFSBucket = GridFSBuckets.create(database);

            // Subir la imagen de proteina
            // esta parte dara error en la pc del laboratorio ya que no tiene las imagenes descargadas.
            ObjectId proteinaImageId = uploadImageToGridFS(gridFSBucket, "/home/toto/Pictures/tpo_ing_datos/proteina", "proteina");
            // Subir la imagen de creatina
            ObjectId creatinaImageId = uploadImageToGridFS(gridFSBucket, "/home/toto/Pictures/tpo_ing_datos/creatina", "creatina");
            // Subir la imagen de glutamina
            ObjectId glutaminaImageId = uploadImageToGridFS(gridFSBucket, "/home/toto/Pictures/tpo_ing_datos/glutamina", "glutamina");
            // Subir la imagen de pre entreno
            ObjectId preentrenoImageId = uploadImageToGridFS(gridFSBucket, "/home/toto/Pictures/tpo_ing_datos/preentreno", "preentreno");

            // Crear el documento de proteina
            Document proteinaDocument = new Document("nombre", proteina.getNombre())
                    .append("descripcion", "Whey protein")
                    .append("precio", proteina.getPrecio())
                    .append("video", "https://www.youtube.com/watch?v=s9eEDwZIxRY")
                    .append("imagen_id", proteinaImageId);

            // Crear el documento de creatina
            Document creatinaDocument = new Document("nombre", creatina.getNombre())
                    .append("descripcion", "Monohidrato de creatina")
                    .append("precio", creatina.getPrecio())
                    .append("video", "https://www.youtube.com/watch?v=kP8zrU_1Gls")
                    .append("imagen_id", creatinaImageId);

            // Crear el documento de glutamina
            Document glutaminaDocument = new Document("nombre", glutamina.getNombre())
                    .append("descripcion", "Glutamina Ena 300g")
                    .append("precio", glutamina.getPrecio())
                    .append("imagen_id", glutaminaImageId);

            // Crear el documento de pre entreno
            Document preentrenoDocument = new Document("nombre", preEntreno.getNombre())
                    .append("descripcion", "Pump v8 300mg Cafeina")
                    .append("precio", preEntreno.getPrecio())
                    .append("video", "https://www.youtube.com/watch?v=11XK-cyP9TI")
                    .append("imagen_id", preentrenoImageId);

            // Insertar documentos en la colección
            catalogo.insertOne(proteinaDocument);
            catalogo.insertOne(creatinaDocument);
            catalogo.insertOne(glutaminaDocument);
            catalogo.insertOne(preentrenoDocument);


            System.out.println("Documentos insertados en catalogo exitosamente.");

        } catch (ErrorConnectionMongoException e) {
            e.printStackTrace();
        } finally {
            PoolMongoDB.closeConnection();
        }
    }

    private static ObjectId uploadImageToGridFS(GridFSBucket gridFSBucket, String imagePath, String imageName) {
        ObjectId fileId = null;
        try (InputStream streamToUploadFrom = new FileInputStream(imagePath)) {
            // Opciones para subir el archivo
            GridFSUploadOptions options = new GridFSUploadOptions()
                    .metadata(new Document("type", "image")
                            .append("content_type", "image/jpeg"));

            // Subir el archivo a GridFS y obtener el ID del archivo
            fileId = gridFSBucket.uploadFromStream(imageName, streamToUploadFrom, options);
            System.out.println("Imagen subida exitosamente con ID: " + fileId.toHexString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileId;
    }
}
