package Puntos;

import codigo.connections.PoolRedis;
import codigo.exceptions.ErrorConnectionRedisException;
import codigo.logica.Usuario;
import redis.clients.jedis.Jedis;


//redis-cli para conectarse a redis

public class Punto1 {/**
    public static void main(String[] args) {
        System.out.println("Accedemos a Redis\n\n");
        try {

            Jedis j = PoolRedis.getInstancia().getConnection();
            Usuario cliente1 = new Usuario(1,"Lorenzo", "Ferrario", 45152083, "loferrario@gmail.com", null);
            Usuario cliente2 = new Usuario(2,"Juan", "Felizia", 45000000, "juan@gmail.com", null);


            //cargo el usuario con id 1
            String usuarioKey = "usuario:" + cliente1.getIdUsuario();
            j.hset(usuarioKey, "id", String.valueOf(cliente1.getIdUsuario()));
            j.hset(usuarioKey, "nombre", cliente1.getNombre());
            j.hset(usuarioKey, "apellido", cliente1.getApellido());
            j.hset(usuarioKey, "dni", String.valueOf(cliente1.getDni()));
            j.hset(usuarioKey, "mail", cliente1.getMail());
            j.hset(usuarioKey, "sesion", String.valueOf(cliente1.getSesion()));


        }
        catch (ErrorConnectionRedisException e) {
            e.printStackTrace();
        } finally {
            PoolRedis.closeConnection();
        }
    }**/
}
