package codigo.logica;

import codigo.logica.pedidos.Pedido;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bson.Document;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String apellido;
    private String mail;
    private int dni;
    private List<Pedido> historialPedidos;
    private LocalDateTime horaInicioSesion;
    private LocalDateTime horaFinSesion;
    private int duracionSesion;

    public Usuario(int idUsuario, String nombre, String apellido, int dni, String mail, List<Pedido> historialPedidos) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.mail = mail;
        this.dni = dni;
        this.historialPedidos = historialPedidos;
    }

    public int getIdUsuario() {
        return idUsuario;
    }
    public String getNombre() {
        return nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public String getMail() {
        return mail;
    }
    public int getDni() {
        return dni;
    }
    public List<Pedido> getHistorialPedidos() {
        return historialPedidos;
    }

    public static Usuario fromMap (Map<String, String> map) {
        int idUser = Integer.parseInt(map.get("id"));
        String nombre = map.get("nombre");
        String apellido = map.get("apellido");
        int dni = Integer.parseInt(map.get("dni"));
        String direccion = map.get("direccion");
        List<Pedido> historialPedidos = null;

        return new Usuario(idUser, nombre, apellido, dni, direccion, historialPedidos);

    }
    public void logIn() {
        horaInicioSesion = LocalDateTime.now();
        System.out.println("Usuario logueado "+horaInicioSesion);
    }

    public void logOut() {
        horaFinSesion = LocalDateTime.now();
        System.out.println("Usuario deslogueado "+horaInicioSesion);
        System.out.println("Duracion de la sesion: "+calcularDuracionSesion());
        System.out.println("Sesion acumulada:"+duracionSesion);
    }

    public Document getLog(){
        Document log = new Document("idUsuario", idUsuario)
                .append("horaInicio", horaInicioSesion)
                .append("horaFin", horaFinSesion)
                .append("duracion", calcularDuracionSesion());
        return log;
    }

    public int getSesion() {
        return duracionSesion;
    }

    private int calcularDuracionSesion() {
        duracionSesion += (int) ChronoUnit.MINUTES.between(horaInicioSesion, horaFinSesion);
        return (int) ChronoUnit.MINUTES.between(horaInicioSesion, horaFinSesion);
    }

    public String getCategoria(){
        long duracionSesion = ChronoUnit.MINUTES.between(horaInicioSesion, horaFinSesion);
        if (duracionSesion > 240) {
            return "TOP";
        } else if (duracionSesion > 120) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    public static Usuario fromDocument (Document d){
        String Sid = d.get("idUsuario").toString();
        int id = Integer.parseInt(Sid);
        String nombre = d.get("nombre").toString();
        String apellido = d.get("apellido").toString();
        String Sdni = d.get("dni").toString();
        int dni = Integer.parseInt(Sdni);
        String mail = d.get("mail").toString();
        int duracionSesion = Integer.parseInt(d.get("duracionSesion").toString());
        return new Usuario(id, nombre, apellido, dni, mail, null);
    }

    // no lo uso pero funciona bien
    public static Usuario fromJson (String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> map = gson.fromJson(json, type);
        return new Usuario(Integer.parseInt(map.get("id")), map.get("nombre"), map.get("apellido"), Integer.parseInt(map.get("dni")), map.get("mail"), null);
    }


}
