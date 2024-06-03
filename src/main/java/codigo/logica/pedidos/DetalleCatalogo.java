package codigo.logica.pedidos;

import org.bson.Document;

public class DetalleCatalogo {
    private String nombre;
    private String descripcion;
    private int precio;
    private String video;
    private String imagen;

    private String nombreAnterior;
    private String descripcionAnterior;
    private int precioAnterior;
    private String videoAnterior;
    private String imagenAnterior;

    public DetalleCatalogo(String nombre, String descripcion, int precio, String video, String imagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.video = video;
        this.imagen = imagen;
    }

    public static DetalleCatalogo fromDocument(Document document) {
        return new DetalleCatalogo(
                document.getString("nombre"),
                document.getString("descripcion"),
                document.getInteger("precio"),
                document.getString("video"),
                document.getString("imagen")
        );
    }

    public String getNombre() {
        return nombre;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecioAnterior(int precio) {
        this.precioAnterior = precio;
    }

    public int getPrecioAnterior() {
        return precioAnterior;
    }
    public String getVideoAnterior() {
        return videoAnterior;
    }
    public String getVideo() {
        return video;
    }
    public void setVideoAnterior(String video) {
        this.videoAnterior = video;
    }
    public String setVideo(String video) {
        return this.video = video;
    }
}
