package codigo.logica.pago.metodosPago;

import codigo.logica.pago.MedioPago;

import java.time.LocalDate;
import java.time.LocalTime;


public class PuntoDeRetiro implements MedioPago{
    public PuntoDeRetiro() {
    }
    public void pagar() {
        System.out.println("Pago en punto de retiro");
    }
    public String getMetodoPago() {
        return "Punto de Retiro";
    }
    public LocalDate getFechaPago() {
        return LocalDate.now();
    }
    public LocalTime getHoraPago() {
        return LocalTime.now();
    }
}
