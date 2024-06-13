import codigo.connections.PoolMongoDB;
import codigo.connections.PoolRedis;
import codigo.exceptions.ErrorConnectionMongoException;
import codigo.exceptions.ErrorConnectionRedisException;
import codigo.logica.Usuario;
import codigo.logica.pedidos.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Interfaz {
    public static void main(String[] args) {

        Scanner numerico = new Scanner(System.in);
        Scanner letras = new Scanner(System.in);
        int opcion = 0;

        System.out.println("Bienvenido al trabajo practico.");
        System.out.println("Por favor, seleccione una opcion:");
        System.out.println("1. Crear un nuevo usuario.");
        System.out.println("2. Iniciar sesion.");
        System.out.println("3. Salir.");

        opcion = numerico.nextInt();

        try {
            Jedis j = PoolRedis.getInstancia().getConnection();

            //login
            switch (opcion) {
                case 1: {
                    System.out.println("Crear un nuevo usuario.");
                    System.out.println("Nombre:");
                    String nombre = letras.nextLine();

                    System.out.println("Apellido:");
                    String apellido = letras.nextLine();

                    System.out.println("Dni:");
                    int dni = numerico.nextInt();

                    System.out.println("Email:");
                    String email = letras.nextLine();

                    System.out.println("Contraseña:");
                    String contrasena = letras.nextLine();

                    System.out.println("Cual es su posicion frente al IVA? \n1 Responsable inscripto \n2 Monotributista \n3 Consumidor Final \n4 Exento");
                    int opciones = numerico.nextInt();

                    String condicionIVA = "";
                    switch (opciones){
                        case 1:
                            condicionIVA = "ResponsableInscripto";
                            break;
                        case 2:
                            condicionIVA = "Monotributista";
                            break;
                        case 3:
                            condicionIVA = "ConsumidorFinal";
                            break;
                        case 4:
                            condicionIVA = "Exento";
                            break;
                    }

                    System.out.println("Usuario creado correctamente.");

                    j.hset("usuario:" + dni, "nombre", nombre);
                    j.hset("usuario:" + dni, "apellido", apellido);
                    j.hset("usuario:" + dni, "dni", String.valueOf(dni));
                    j.hset("usuario:" + dni, "email", email);
                    j.hset("usuario:" + dni, "contrasena", contrasena);
                    j.hset("usuario:" + dni, "sesion", String.valueOf(0));
                    j.hset("usuario:" + dni, "condicionIVA", condicionIVA);


                    Usuario user = new Usuario(nombre, apellido, dni, email, null, contrasena, 0, condicionIVA);
                    operaciones(user);
                    break;
            }


                case 2:{
                    System.out.println("Iniciar sesion.");

                    System.out.println("Ingrese Dni:");
                    int dniLogin = numerico.nextInt();


                    String usuarioKey = "usuario:" + dniLogin;
                    Map<String, String> userData = j.hgetAll(usuarioKey);
                    Usuario user = Usuario.fromMap(userData);

                    int dni = Integer.parseInt(j.hget(usuarioKey, "dni"));
                    String contrasena = j.hget(usuarioKey, "contrasena");


                    if (dniLogin == dni) {
                        System.out.println("Ingrese contraseña:");
                        String contrasenaLogin = letras.nextLine();
                        if (Objects.equals(contrasenaLogin, contrasena)) {
                            System.out.println("sesion iniciada");
                            operaciones(user);
                        } else {
                            System.out.println("Clave incorrecta :'(.");
                        }

                    } else {
                        System.out.println("Usuario no encontrado.");
                    }
                    break;
            }

                case 3: {
                    System.out.println("Salir.");
                    break;
                }
                default:{
                    System.out.println("Opcion incorrecta.");
                    break;
                    }
                }


        } catch (ErrorConnectionRedisException e) {
            e.printStackTrace();
        }
    }
    public static void operaciones(Usuario user){
        System.out.println("BienVenido:"+user.getNombre());
        user.logIn();

        Scanner numerico = new Scanner(System.in);
        Scanner letras = new Scanner(System.in);



        try {
            Jedis j = PoolRedis.getInstancia().getConnection();
            MongoCollection<Document> catalogo = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("catalogo");
            MongoCollection<Document> logs = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("logs");
            MongoCollection<Document> carritos = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("carritos");
            MongoCollection<Document> logsCarrito = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("logsCarrito");
            MongoCollection<Document> facturas = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("facturas");








            int opciones;

            while (true) {
                System.out.println("Ingrese una opcion");
                System.out.println("1- Agregar un producto");
                System.out.println("2- Crear carrito");
                System.out.println("3- Agregar producto a carrito");
                System.out.println("4- Eliminar producto de carrito");
                System.out.println("5- Confirmar carrito");
                System.out.println("6- Pagar factura");
                System.out.println("9- Salir");
                opciones = numerico.nextInt();

                switch (opciones) {
                    case 1: {
                        System.out.println("Agregar un producto");
                        System.out.println("Nombre: ");
                        String nombre = letras.nextLine();
                        System.out.println("Precio: ");
                        int precio = numerico.nextInt();
                        System.out.println("Descripcion: ");
                        String descripcion = letras.nextLine();
                        System.out.println("Video:");
                        String video = letras.nextLine();
                        System.out.println("Imagen:");
                        String imagen = letras.nextLine();
                        new DetalleCatalogo(nombre, descripcion, precio, video, imagen);

                        String valorContador = j.get("contadorProductos");
                        int ultimoID;
                        if (valorContador == null) {
                            ultimoID = 1;
                            j.set("contadorProductos", String.valueOf(ultimoID));

                        } else {
                            ultimoID = Integer.parseInt(valorContador) + 1;
                            j.set("contadorProductos", String.valueOf(ultimoID+1));
                        }

                        Document producto = new Document("id", String.valueOf(ultimoID))
                                .append("nombre", nombre)
                                .append("precio", precio)
                                .append("descripcion", descripcion)
                                .append("video", video)
                                .append("imagen", imagen);

                        catalogo.insertOne(producto);

                        System.out.println("Producto ingresado correctamente.");


                        break;
                    } //agregar a catalogo
                    case 2: {
                        System.out.println("Crear carrito.");
                        System.out.println("Tiene algun cupon de descuento (Ingrese el % o 0 si no tiene)");
                        int cupon = numerico.nextInt();

                        String valorContador = j.get("contadorCarritos");
                        int ultimoID = 0;
                        {
                            if (valorContador == null) {
                                ultimoID = 1;
                                j.set("contadorCarritos", String.valueOf(ultimoID));

                            } else {
                                ultimoID = Integer.parseInt(valorContador) + 1;
                                j.set("contadorCarritos", String.valueOf(ultimoID));
                            }
                        }

                        Carrito carrito = new Carrito(ultimoID, user, cupon, LocalDate.now());
                        carrito.crearCarrito();

                        String usuarioKey = "usuario:"+user.getDni();
                        Map<String, String> userData = j.hgetAll(usuarioKey);

                        Document carritoDocument = new Document("idCarrito", String.valueOf(ultimoID))
                                .append("usuario", userData)
                                .append("articulos", carrito.adaptarCarrito())
                                .append("fechaCreacion", carrito.getFechaCreacion())
                                .append("promocion", cupon+"%");

                         valorContador = j.get("contadorLogsCarritos");
                         int ultimaModificacion;
                        {
                            if (valorContador == null) {
                                ultimaModificacion = 1;
                                j.set("contadorLogsCarritos", String.valueOf(ultimaModificacion));

                            } else {
                                ultimaModificacion = Integer.parseInt(valorContador) + 1;
                                j.set("contadorLogsCarritos", String.valueOf(ultimaModificacion));
                            }
                        }

                        Document logsDocument = new Document("idLog", ultimaModificacion).append("idCarrito", carrito.getId()).append("articulos", carrito.adaptarCarrito()).
                                append("horaActualizacion", LocalDateTime.now());

                        carritos.insertOne(carritoDocument);
                        logsCarrito.insertOne(logsDocument);
                        break;
                    } //crear carrito
                    case 3: {
                        System.out.println("A que carrito queres agregar?");
                        FindIterable<Document> carritosDelUsuario = carritos.find(Filters.eq("usuario.id", String.valueOf(user.getDni())));
                        for (Document document : carritosDelUsuario) {
                            System.out.println("Numero de carrito: "+document.get("idCarrito"));
                        }

                        int idCarrito =numerico.nextInt();
                        Document carritoDocument = carritos.find(Filters.eq("idCarrito", String.valueOf(idCarrito))).first();
                        Carrito carrito = Carrito.fromDocument(carritoDocument, user);


                        System.out.println("Agregar producto a carrito:");
                        System.out.println("Catalogo de productos");
                        FindIterable<Document> documents = catalogo.find();
                        for (Document document : documents) {
                            System.out.println(document.get("id")+"- "+"Nombre del producto: "+document.get("nombre")+" - Precio: "+document.get("precio"));
                        }
                        System.out.println("Ingrese el id del producto que quiere agregar al carrito");
                        int idProducto = numerico.nextInt();

                        Document d = catalogo.find(Filters.eq("id", String.valueOf(idProducto))).first();
                        String nombre = d.getString("nombre");
                        int precio = d.getInteger("precio");

                        System.out.println("Ingrese la cantidad de unidades");
                        int cantidad = numerico.nextInt();

                        carrito.agregarArticulo(idProducto,nombre, cantidad, precio);

                        String usuarioKey = "usuario:"+user.getDni();
                        Map<String, String> userData = j.hgetAll(usuarioKey);

                        carritoDocument = new Document("idCarrito", String.valueOf(idCarrito))
                                .append("usuario", userData)
                                .append("articulos", carrito.adaptarCarrito())
                                .append("fechaCreacion", carrito.getFechaCreacion())
                                .append("promocion", carrito.getPromocion()+"%");

                        String valorContador = j.get("contadorLogsCarritos");
                        int ultimaModificacion;
                        {
                            if (valorContador == null) {
                                ultimaModificacion = 1;
                                j.set("contadorLogsCarritos", String.valueOf(ultimaModificacion));

                            } else {
                                ultimaModificacion = Integer.parseInt(valorContador) + 1;
                                j.set("contadorLogsCarritos", String.valueOf(ultimaModificacion));
                            }
                        }

                        Document logsDocument = new Document("idLog", ultimaModificacion).append("idCarrito", carrito.getId()).append("articulos", carrito.adaptarCarrito()).
                                append("horaActualizacion", LocalDateTime.now());

                        carritos.updateOne(Filters.eq("idCarrito", String.valueOf(idCarrito)),new Document("$set", new Document("articulos", carrito.adaptarCarrito())));
                        logsCarrito.insertOne(logsDocument);
                        break;
                    } //agregar a carrito
                    case 4: {
                        System.out.println("Eliminar producto de un carrito");
                        System.out.println("A que carrito queres eliminarle un producto?");
                        FindIterable<Document> carritosDelUsuario = carritos.find(Filters.eq("usuario.id", String.valueOf(user.getDni())));
                        for (Document document : carritosDelUsuario) {
                            System.out.println("Numero de carrito: "+document.get("idCarrito"));
                        }

                        int idCarrito =numerico.nextInt();
                        Document carritoDocument = carritos.find(Filters.eq("idCarrito", String.valueOf(idCarrito))).first();
                        Carrito carrito = Carrito.fromDocument(carritoDocument, user);

                        System.out.println("Elegi el producto a eliminar:");

                        for (String producto :carrito.getListadoArticulos()){
                            System.out.println(producto);
                        }

                        int idProducto = numerico.nextInt();

                        carrito.eliminarArticulo(idProducto);

                        String usuarioKey = "usuario:"+user.getDni();
                        Map<String, String> userData = j.hgetAll(usuarioKey);

                        carritoDocument = new Document("idCarrito", String.valueOf(idCarrito))
                                .append("usuario", userData)
                                .append("articulos", carrito.adaptarCarrito())
                                .append("fechaCreacion", carrito.getFechaCreacion())
                                .append("promocion", carrito.getPromocion()+"%");

                        String valorContador = j.get("contadorLogsCarritos");
                        int ultimaModificacion;
                        {
                            if (valorContador == null) {
                                ultimaModificacion = 1;
                                j.set("contadorLogsCarritos", String.valueOf(ultimaModificacion));

                            } else {
                                ultimaModificacion = Integer.parseInt(valorContador) + 1;
                                j.set("contadorLogsCarritos", String.valueOf(ultimaModificacion));
                            }
                        }

                        Document logsDocument = new Document("idLog", ultimaModificacion).append("idCarrito", carrito.getId()).append("articulos", carrito.adaptarCarrito()).
                                append("horaActualizacion", LocalDateTime.now());

                        carritos.updateOne(Filters.eq("idCarrito", String.valueOf(idCarrito)),new Document("$set", new Document("articulos", carrito.adaptarCarrito())));
                        logsCarrito.insertOne(logsDocument);
                        break;

                    } //eliminar producto de un carrito
                    case 5: {
                        System.out.println("Confirmar pedido");
                        System.out.println("Que carrito queres confirmar?");
                        FindIterable<Document> carritosDelUsuario = carritos.find(Filters.eq("usuario.id", String.valueOf(user.getDni())));
                        for (Document document : carritosDelUsuario) {
                            System.out.println("Numero de carrito: "+document.get("idCarrito"));
                        }

                        int idCarrito =numerico.nextInt();
                        Document carritoDocument = carritos.find(Filters.eq("idCarrito", String.valueOf(idCarrito))).first();
                        Carrito carrito = Carrito.fromDocument(carritoDocument, user);
                        Pedido pedido = carrito.confirmarPedido();

                        String valorContador = j.get("contadorFacturas");
                        int ultimoID = 0;
                        {
                            if (valorContador == null) {
                                ultimoID = 1;
                                j.set("contadorFacturas", String.valueOf(ultimoID));

                            } else {
                                ultimoID = Integer.parseInt(valorContador) + 1;
                                j.set("contadorFacturas", String.valueOf(ultimoID));
                            }
                        }
                        Factura factura;
                        System.out.println("promo:"+pedido.getPromocion());
                        if (user.getCondicionIVA().equals("ResponsableInscripto") ){
                            //hace factura A
                            factura = new FacturaTipoA(ultimoID, user.getNombre()+" "+user.getApellido(), pedido.getDetalles(), pedido.getPromocion());
                            System.out.println("Se genero una factura A con id de factura: "+factura.getId()); // no aplico descuento PERO ANDA
                        }else{
                            // hace factura B
                            factura = new FacturaTipoB(ultimoID, user.getNombre()+" "+user.getApellido(), pedido.getDetalles(), pedido.getPromocion());
                            System.out.println("Se genero una factura B con id de factura: "+factura.getId()); // no aplico descuento PERO ANDA

                        }

                        Document facturaDocument = new Document("idFactura", factura.getId())
                                .append("idComprador", user.getDni())
                                .append("promocion", factura.getPromocion())
                                .append("fechaEmision", factura.getFechaEmision())
                                .append("fechaVencimiento", factura.getFechaVencimiento())
                                .append("listado",pedido.adaptarArticulos() )
                                .append("total", factura.getTotal() )
                                .append("tipo", factura.getTipo());

                        facturas.insertOne(facturaDocument);
                        break;


                    } //confirmar carrito
                    case 6 : {
                        System.out.println("Pagar factura");
                        System.out.println("Seleccione la factura que desea abonar:");
                        FindIterable<Document> facturasDelUsuario = facturas.find(Filters.eq("idComprador", user.getDni()));
                        for (Document document : facturasDelUsuario) {
                            System.out.println("Numero de factura: "+document.get("idFactura"));
                        }
                        System.out.println("");

                        int idFactura = numerico.nextInt();

                        Document facturaDocument = facturas.find(Filters.eq("idFactura", idFactura)).first();
                        Factura factura;
                        //caso si es una factura a:
                        if (facturaDocument.get("tipo").equals("A")){
                            List<Document> articulos = (List<Document>) facturaDocument.get("listado");
                            List<DetalleCarrito> productos = new ArrayList<>();
                            for (Document document : articulos){
                                DetalleCarrito detalle = new DetalleCarrito(document.getInteger("idProducto"), document.getString("nombreArticulo"),
                                        document.getInteger("cantidad")
                                , document.getInteger("precioTotal"));
                                productos.add(detalle);
                            }


                            factura = new FacturaTipoA(facturaDocument.getInteger("idFactura"),
                                    user.getNombre()+" "+user.getApellido(),
                                    productos, facturaDocument.getInteger("promocion"));

                            factura.pagar();


                        } else {
                            List<Document> articulos = (List<Document>) facturaDocument.get("listado");
                            List<DetalleCarrito> productos = new ArrayList<>();
                            for (Document document : articulos){
                                DetalleCarrito detalle = new DetalleCarrito(document.getInteger("idProducto"), document.getString("nombreArticulo"),
                                        document.getInteger("cantidad")
                                        , document.getInteger("precioTotal"));
                                productos.add(detalle);
                            }


                            factura = new FacturaTipoB(facturaDocument.getInteger("idFactura"),
                                    user.getNombre()+" "+user.getApellido(),
                                    productos, facturaDocument.getInteger("promocion"));

                            factura.pagar();


                        }


                        break;

                    } // pagar factura
                    case 9 : {
                        int sesion = user.logOut();
                        j.hset("usuario:"+user.getDni(), "sesion", String.valueOf(sesion));
                        System.out.println("Saliendo del programa.");
                        Document log = user.getLog();

                        logs.insertOne(log);
                        return;
                    } //salir
                }
            }
        }catch (ErrorConnectionMongoException | ErrorConnectionRedisException e){

        }

    }
}
