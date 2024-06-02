package codigo.logica.pago;

import java.time.LocalDate;
import java.time.LocalTime;

public interface MedioPago {
    public void pagar();
    public String getMetodoPago();

    public LocalDate getFechaPago();
    LocalTime getHoraPago();
}
