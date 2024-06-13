package codigo.logica;

import codigo.logica.pedidos.Pedido;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class Usuario {
    private String nombre;
    private String contrasena;
    private String apellido;
    private String mail;
    private int dni;
    private List<Pedido> historialPedidos;
    private LocalDateTime horaInicioSesion;
    private LocalDateTime horaFinSesion;
    private String condicionIVA;
    private int duracionSesion;
    private int sesionAcumulada;

    public Usuario( String nombre, String apellido, int dni, String mail, List<Pedido> historialPedidos, String contrasena, int sesionAcumulada ,String condicionIVA) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.mail = mail;
        this.dni = dni;
        this.historialPedidos = historialPedidos;
        this.contrasena = contrasena;
        this.sesionAcumulada = sesionAcumulada;
        this.condicionIVA = condicionIVA;
    }

    public String getNombre() {
        return nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public String getContrasena(){
        return contrasena;
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
        String nombre = map.get("nombre");
        String apellido = map.get("apellido");
        int dni = Integer.parseInt(map.get("dni"));
        String direccion = map.get("direccion");
        List<Pedido> historialPedidos = null;
        String contrasena = map.get("contrasena");
        int sesionAcumulada = Integer.parseInt(map.get("sesion"));
        String condicionIVA = map.get("condicionIVA");

        return new Usuario(nombre, apellido, dni, direccion, historialPedidos, contrasena, sesionAcumulada, condicionIVA);

    }
    public void logIn() {
        horaInicioSesion = LocalDateTime.now();
        System.out.println("Usuario logueado "+horaInicioSesion);
    }
    public String getCondicionIVA(){return condicionIVA;}
    public int logOut() {

        horaFinSesion = LocalDateTime.now();
        System.out.println("Usuario deslogueado "+horaInicioSesion);
        System.out.println("Duracion de la sesion: "+calcularDuracionSesion());
        sesionAcumulada += calcularDuracionSesion();
        System.out.println("Sesion acumulada:"+sesionAcumulada);
        return sesionAcumulada;
    }

    public Document getLog(){
        Document log = new Document("dni", dni)
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
        long duracionSesion =0;//= ChronoUnit.MINUTES.between(horaInicioSesion, horaFinSesion);
        if (duracionSesion > 240) {
            return "TOP";
        } else if (duracionSesion > 120) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    public static Usuario fromDocument (Document d){
        String nombre = d.get("nombre").toString();
        String apellido = d.get("apellido").toString();
        String Sdni = d.get("dni").toString();
        int dni = Integer.parseInt(Sdni);
        String mail = d.get("mail").toString();
        int duracionSesion = Integer.parseInt(d.get("duracionSesion").toString());
        String contrasena = d.get("contrasena").toString();
        String condicionIVA = d.get("condicionIVA").toString();
        return new Usuario( nombre, apellido, dni, mail, null,contrasena, duracionSesion, condicionIVA);
    }

    // no lo uso pero funciona bien
    public static Usuario fromJson (String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> map = gson.fromJson(json, type);
        return new Usuario( map.get("nombre"), map.get("apellido"), Integer.parseInt(map.get("dni")), map.get("mail"), null, map.get("contrasena"), -1000, map.get("condicionIVA"));
    }


}
