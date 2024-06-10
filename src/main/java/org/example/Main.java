package org.example;

import codigo.connections.PoolMongoDB;
import codigo.connections.PoolRedis;
import codigo.exceptions.ErrorConnectionMongoException;
import codigo.exceptions.ErrorConnectionRedisException;
import codigo.logica.Usuario;
import codigo.logica.pago.MedioPago;
import codigo.logica.pago.metodosPago.Efectivo;
import codigo.logica.pedidos.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import redis.clients.jedis.Jedis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;


public class Main {

    public static void main(String[] args) {
        try {

            //Punto 1
            Jedis j = PoolRedis.getInstancia().getConnection();
            Usuario cliente1 = new Usuario(1,"Lorenzo", "Ferrario", 45152083, "loferrario@gmail.com", null);
            Usuario cliente2 = new Usuario(2,"Juan", "Felizia", 45000000, "juan@gmail.com", null);


            String usuarioKey;
            //cargo el usuario con id 1
            usuarioKey = "usuario:" + cliente1.getIdUsuario();
            j.hset(usuarioKey, "id", String.valueOf(cliente1.getIdUsuario()));
            j.hset(usuarioKey, "nombre", cliente1.getNombre());
            j.hset(usuarioKey, "apellido", cliente1.getApellido());
            j.hset(usuarioKey, "dni", String.valueOf(cliente1.getDni()));
            j.hset(usuarioKey, "mail", cliente1.getMail());
            j.hset(usuarioKey, "sesion", String.valueOf(cliente1.getSesion()));

            //cargo el usuario con id 2
            usuarioKey = "usuario:" + cliente2.getIdUsuario();
            j.hset(usuarioKey, "id", String.valueOf(cliente2.getIdUsuario()));
            j.hset(usuarioKey, "nombre", cliente2.getNombre());
            j.hset(usuarioKey, "apellido", cliente2.getApellido());
            j.hset(usuarioKey, "dni", String.valueOf(cliente2.getDni()));
            j.hset(usuarioKey, "mail", cliente2.getMail());
            j.hset(usuarioKey, "sesion", String.valueOf(cliente2.getSesion()));

            //Punto 2
            MongoCollection<Document> logs = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("logs");

            Document log;

            usuarioKey = "usuario:1";
            //recupero desde redis el usuario con id 1
            Map<String, String> userData = j.hgetAll(usuarioKey);

            //lo hago objeto
            // aclaracion: hago una copia del cliente 1 para demostrar como recuperar objetos de una DB redis.
            Usuario copiaCliente1 = Usuario.fromMap(userData);

            // demuestro que se recupero correctamente el objeto
            System.out.println(copiaCliente1.getNombre());

            copiaCliente1.logIn();
            copiaCliente1.logOut();

            log = copiaCliente1.getLog();
            logs.insertOne(log);

            System.out.println("Categoria del cliente:"+copiaCliente1.getCategoria());


            //Punto 3
            MongoCollection<Document> carritos = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("carritos");
            MongoCollection<Document> logsCarrito = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("logsCarrito");

            // armo carrito y productos

            Articulo proteina = new Articulo("Proteina", 1000);
            Articulo creatina = new Articulo("Creatina", 1500);
            Articulo glutamina = new Articulo("Glutamina", 2000);
            Articulo preEntreno = new Articulo("Pre Entreno", 2500);

            Carrito carrito = new Carrito(1, cliente1, "10% off", LocalDate.now());
            carrito.crearCarrito();

            // a medida que se agregan productos al carrito, se actualiza el carrito en la DB y queda registrado cada log
            Document carritoDocument = new Document("idCarrito", 1).append("usuario", userData).append("articulos", carrito.adaptarCarrito()).
                    append("fechaCreacion", carrito.getFechaCreacion()).append("promocion", carrito.getPromocion());

            Document logsDocument = new Document("idLog", 1).append("idCarrito", 1).append("articulos", carrito.adaptarCarrito()).
                    append("horaActualizacion", LocalDateTime.now());

            carritos.insertOne(carritoDocument);
            logsCarrito.insertOne(logsDocument);

            //agrego proteina cantidad 2
            carrito.agregarArticulo(proteina.getNombre(), 2, proteina.getPrecio());
            carritos.updateOne(Filters.eq("idCarrito", 1), new Document("$set", new Document("articulos", carrito.adaptarCarrito())));

            logsDocument = new Document("idLog", 2).append("idCarrito", 1).append("articulos", carrito.adaptarCarrito()).
                    append("horaActualizacion", LocalDateTime.now());
            logsCarrito.insertOne(logsDocument);

            //agrego creatina cantidad 1
            carrito.agregarArticulo(creatina.getNombre(), 1, creatina.getPrecio());
            carritos.updateOne(Filters.eq("idCarrito", 1), new Document("$set", new Document("articulos", carrito.adaptarCarrito())));

            logsDocument = new Document("idLog", 3).append("idCarrito", 1).append("articulos", carrito.adaptarCarrito()).
                    append("horaActualizacion", LocalDateTime.now());
            logsCarrito.insertOne(logsDocument);

            //agrego glutamina cantidad 1
            carrito.agregarArticulo(glutamina.getNombre(), 1, glutamina.getPrecio());
            carritos.updateOne(Filters.eq("idCarrito", 1), new Document("$set", new Document("articulos", carrito.adaptarCarrito())));

            logsDocument = new Document("idLog", 4).append("idCarrito", 1).append("articulos", carrito.adaptarCarrito()).
                    append("horaActualizacion", LocalDateTime.now());
            logsCarrito.insertOne(logsDocument);

            //agrego pre entreno cantidad 1
            carrito.agregarArticulo(preEntreno.getNombre(), 1, preEntreno.getPrecio());
            carritos.updateOne(Filters.eq("idCarrito", 1), new Document("$set", new Document("articulos", carrito.adaptarCarrito())));

            logsDocument = new Document("idLog", 5).append("idCarrito", 1).append("articulos", carrito.adaptarCarrito()).
                    append("horaActualizacion", LocalDateTime.now());
            logsCarrito.insertOne(logsDocument);

            //elimino proteina
            carrito.eliminarArticulo(proteina.getNombre());
            carritos.updateOne(Filters.eq("idCarrito", 1), new Document("$set", new Document("articulos", carrito.adaptarCarrito())));

            logsDocument = new Document("idLog", 6).append("idCarrito", 1).append("articulos", carrito.adaptarCarrito()).
                    append("horaActualizacion", LocalDateTime.now());
            logsCarrito.insertOne(logsDocument);

            //printeo el carrito como quedo
            carrito.getArticulos();


            //punto 4

            System.out.println("Carrito actualmente:");
            carrito.getArticulos();

            //busco el documento con idLog 5 (ultimo estado del carrito)
            logsDocument = logsCarrito.find(Filters.eq("idLog", 5)).first();

            //recupero el estado anterior del carrito
            carrito.recuperarEstado(logsDocument);

            System.out.println("Carrito recuperado:");
            carrito.getArticulos();


            //punto 5

            MongoCollection<Document> pedidos = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("pedidos");

            Usuario comprador = new Usuario(3, "Lucas", "Ellero", 45111234, "comprador@gmail.com", null);
            comprador.logIn();
            usuarioKey = "usuario:" + comprador.getIdUsuario();
            j.hset(usuarioKey, "id", String.valueOf(comprador.getIdUsuario()));
            j.hset(usuarioKey, "nombre", comprador.getNombre());
            j.hset(usuarioKey, "apellido", comprador.getApellido());
            j.hset(usuarioKey, "dni", String.valueOf(comprador.getDni()));
            j.hset(usuarioKey, "mail", comprador.getMail());
            j.hset(usuarioKey, "sesion", String.valueOf(comprador.getSesion()));


            Carrito carrito2 = new Carrito(2, comprador, "10% off", LocalDate.now());

            carrito2.agregarArticulo("Guantes de boxeo", 1, 15000);
            carrito2.agregarArticulo("Bolsa de boxeo", 1, 50000);

            Pedido pedido = carrito2.confirmarPedido();

            comprador.logOut();

            Document usuarioDocument = new Document("idUsuario", comprador.getIdUsuario()).append("nombre", comprador.getNombre()).append("apellido", comprador.getApellido()).
                    append("dni", comprador.getDni()).append("mail", comprador.getMail()).append("duracionSesion", comprador.getSesion());

            Document pedidoDocument = new Document("idPedido", pedido.getId()).append("usuario", usuarioDocument).append("articulos", pedido.adaptarArticulos()).
                    append("fechaPedido", pedido.getFechaPedido()).append("promocion", pedido.getPromocion()).append("total", pedido.getMontoTotal());

            pedidos.insertOne(pedidoDocument);

            //punto 6 y 7
            MongoCollection<Document> pagos = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("pagos");
            pedido.getArticulos();
            int montoTotal = pedido.getMontoTotal();

            MedioPago pago = new Efectivo(montoTotal);
            pago.pagar();

            Document pagoDocument = new Document("idPago", 1).append("idPedido", pedido.getId()).append("monto", pedido.getMontoTotal())
                    .append("metodoPago", pago.getMetodoPago()).append("fechaPago", pago.getFechaPago()).append("horaPago", pago.getHoraPago());

            pagos.insertOne(pagoDocument);


            //punto 9
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

            //punto 10 y 11
            MongoCollection logsCatalogo = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("logsCatalogo");

            //recupero los documentos de los productos del catalogo
            proteinaDocument = (Document) catalogo.find(Filters.eq("nombre", "Proteina")).first();
            creatinaDocument = (Document) catalogo.find(Filters.eq("nombre", "Creatina")).first();
            glutaminaDocument = (Document) catalogo.find(Filters.eq("nombre", "Glutamina")).first();
            Document preEntrenoDocument = (Document) catalogo.find(Filters.eq("nombre", "Pre Entreno")).first();

            //creo los objetos de los productos
            DetalleCatalogo proteinaDetalle = DetalleCatalogo.fromDocument(proteinaDocument);
            DetalleCatalogo creatinaDetalle = DetalleCatalogo.fromDocument(creatinaDocument);
            DetalleCatalogo glutaminaDetalle = DetalleCatalogo.fromDocument(glutaminaDocument);
            DetalleCatalogo preEntrenoDetalle = DetalleCatalogo.fromDocument(preEntrenoDocument);

            //instancio el catalogo
            Catalogo catalogoProductos = new Catalogo();
            //cargo el catalogo
            catalogoProductos.agregarArticulo(proteinaDetalle);
            catalogoProductos.agregarArticulo(creatinaDetalle);
            catalogoProductos.agregarArticulo(glutaminaDetalle);
            catalogoProductos.agregarArticulo(preEntrenoDetalle);

            //cambio el precio de la proteina en el catalogo y guardo el log
            catalogoProductos.actualizarPrecio("Proteina", 3000);
            catalogo.updateOne(Filters.eq("nombre", "Proteina"), new Document("$set", new Document("precio", 3000)));
            logsCatalogo.insertOne(new Document("nombre", proteinaDetalle.getNombre())
                    .append("tipo", "actualizacion precio")
                    .append("precio anterior", proteinaDetalle.getPrecioAnterior())
                    .append("precio nuevo", proteinaDetalle.getPrecio()));

            //cambio el precio de la creatina y guardo el log
            catalogoProductos.actualizarPrecio("Creatina", 2500);
            catalogo.updateOne(Filters.eq("nombre", "Creatina"), new Document("$set", new Document("precio", 2500)));
            logsCatalogo.insertOne(new Document("nombre", creatinaDetalle.getNombre())
                    .append("tipo", "actualizacion precio")
                    .append("precio anterior", creatinaDetalle.getPrecioAnterior())
                    .append("precio nuevo", creatinaDetalle.getPrecio()));

            //cambio el precio de la proteina y guardo el log
            catalogoProductos.actualizarPrecio("Proteina", 2000);
            catalogo.updateOne(Filters.eq("nombre", "Proteina"), new Document("$set", new Document("precio", 2000)));
            logsCatalogo.insertOne(new Document("nombre", proteinaDetalle.getNombre())
                    .append("tipo", "actualizacion precio")
                    .append("precio anterior", proteinaDetalle.getPrecioAnterior())
                    .append("precio nuevo", proteinaDetalle.getPrecio()));


            //cambio el video de la proteina y guardo el log
            catalogoProductos.actualizarVideo("Proteina", "https://www.youtube.com/watch?v=9vKqVkMQHKk");
            catalogo.updateOne(Filters.eq("nombre", "Proteina"), new Document("$set", new Document("video", "https://www.youtube.com/watch?v=9vKqVkMQHKk")));
            logsCatalogo.insertOne(new Document("nombre", proteina.getNombre())
                    .append("tipo", "actualizacion video")
                    .append("video anterior", proteinaDetalle.getVideoAnterior())
                    .append("video nuevo", proteinaDetalle.getVideo()));




        } catch (ErrorConnectionMongoException | ErrorConnectionRedisException e) {
            e.printStackTrace();
        } finally {
            PoolMongoDB.closeConnection();
            PoolRedis.closeConnection();
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