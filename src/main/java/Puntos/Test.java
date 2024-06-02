package Puntos;

import codigo.connections.PoolRedis;
import codigo.exceptions.ErrorConnectionRedisException;
import codigo.logica.Usuario;
import com.google.gson.reflect.TypeToken;
import redis.clients.jedis.Jedis;
import com.google.gson.Gson;
import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        try {
            Jedis j = PoolRedis.getInstancia().getConnection();

            Map<String, String> userData = new HashMap<>();
            userData.put("id", j.hget("usuario:1", "id"));
            userData.put("nombre", j.hget("usuario:1", "nombre"));
            userData.put("apellido", j.hget("usuario:1", "apellido"));
            userData.put("email", j.hget("usuario:1", "mail"));
            userData.put("dni", j.hget("usuario:1", "dni"));
            userData.put("sesion", j.hget("usuario:1", "sesion"));

            //System.out.println(userData.get("nombre") + " " + userData.get("apellido") + " " + userData.get("dni") + " " + userData.get("email") + " " + userData.get("sesion"));

            //System.out.println(j.hgetAll("usuario:1"));


            String json = j.hgetAll("usuario:1").toString();
            System.out.println(json);

            Gson gson = new Gson();

            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> map = gson.fromJson(json, type);

            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

            Usuario user = Usuario.fromJson(json);
            System.out.println(user.getNombre() + " " + user.getApellido() + " " + user.getDni() + " " + user.getMail() + " " + user.getSesion());


            //Map<String, String> test = j.hgetAll("usuario:1");
            //System.out.println(test.get("nombre") + " " + test.get("apellido") + " " + test.get("dni") + " " + test.get("mail") + " " + test.get("sesion"));









        } catch (ErrorConnectionRedisException e) {
            throw new RuntimeException(e);
        }
    }
}
