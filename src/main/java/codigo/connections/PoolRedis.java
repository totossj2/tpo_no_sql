package codigo.connections;

import codigo.exceptions.ErrorConnectionRedisException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class PoolRedis {

    private static PoolRedis instancia;
    private JedisPool pool;
    private static Jedis jedis;


    private PoolRedis() {
        pool = new JedisPool("localhost", 6379);

    }

    public static PoolRedis getInstancia() {
        if(instancia == null)
            instancia = new PoolRedis();
        return instancia;
    }

    public Jedis getConnection() throws ErrorConnectionRedisException {
        try {
            Jedis jedis = pool.getResource();
            return jedis;
        }
        catch (Exception e) {
            throw new ErrorConnectionRedisException("Error al conectarme a Redis");
        }
    }

    public static void closeConnection() {
        if (jedis != null) {
            jedis.close();
            System.out.println("Conexión a Redis cerrada.");
        }
    }
}
