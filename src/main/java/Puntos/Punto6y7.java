package Puntos;

import codigo.connections.PoolMongoDB;
import codigo.exceptions.ErrorConnectionMongoException;
import codigo.logica.pago.MedioPago;
import codigo.logica.pago.metodosPago.Efectivo;
import codigo.logica.pedidos.Pedido;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class Punto6y7 {
    public static void main(String[] args) {

        try {
            MongoCollection<Document> pedidos = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("pedidos");

            MongoCollection<Document> pagos = PoolMongoDB.getInstancia().getConnection("tpo").getCollection("pagos");


            Document pedidoDocument = pedidos.find(Filters.eq("idPedido", 2)).first();

            Pedido pedido = Pedido.fromDocument(pedidoDocument);

            pedido.getArticulos();

            int montoTotal = pedido.getMontoTotal();

            MedioPago pago = new Efectivo(montoTotal);
            pago.pagar();

            Document pagoDocument = new Document("idPago", 1).append("idPedido", pedido.getId()).append("monto", pedido.getMontoTotal())
                    .append("metodoPago", pago.getMetodoPago()).append("fechaPago", pago.getFechaPago()).append("horaPago", pago.getHoraPago());

            pagos.insertOne(pagoDocument);









        } catch (ErrorConnectionMongoException e) {
            e.printStackTrace();

        } finally {
            PoolMongoDB.closeConnection();
        }
    }
}
