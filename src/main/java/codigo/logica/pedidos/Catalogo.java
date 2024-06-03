package codigo.logica.pedidos;

import java.util.ArrayList;
import java.util.List;

public class Catalogo {
    List<DetalleCatalogo> catalogo;

    public Catalogo() {
        catalogo = new ArrayList<>();
    }

    public void agregarArticulo(DetalleCatalogo articulo) {
        catalogo.add(articulo);
    }

    public void actualizarPrecio(String nombreArticulo, int nuevoPrecio) {
        for (DetalleCatalogo detalle : catalogo) {
            if (detalle.getNombre().equals(nombreArticulo)) {
                detalle.setPrecioAnterior(detalle.getPrecio());
                detalle.setPrecio(nuevoPrecio);
            }
        }
    }

    public int getPrecio(String proteina) {
        for (DetalleCatalogo detalle : catalogo) {
            if (detalle.getNombre().equals(proteina)) {
                return detalle.getPrecio();
            }
        }
        return -1;

    }

    public void actualizarVideo(String proteina, String url) {
        for (DetalleCatalogo detalle : catalogo) {
            if (detalle.getNombre().equals(proteina)) {
                detalle.setVideoAnterior(detalle.getVideo());
                detalle.setVideo(url);
            }
        }
    }
}
