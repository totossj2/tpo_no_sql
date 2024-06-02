package codigo.logica.pago.metodosPago;

import codigo.logica.pago.MedioPago;

import java.time.LocalDate;
import java.time.LocalTime;

public class Tarjeta implements MedioPago {
    public Tarjeta() {
    }
    public void pagar() {
        System.out.println("Pago con tarjeta");
    }
    public String getMetodoPago() {
        return "Tarjeta";
    }
    public LocalDate getFechaPago() {
        return LocalDate.now();
    }
    public LocalTime getHoraPago() {
        return LocalTime.now();
    }
}
