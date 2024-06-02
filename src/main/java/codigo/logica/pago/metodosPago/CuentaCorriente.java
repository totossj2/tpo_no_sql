package codigo.logica.pago.metodosPago;

import codigo.logica.pago.MedioPago;

import java.time.LocalDate;
import java.time.LocalTime;


public class CuentaCorriente implements MedioPago{
    public CuentaCorriente() {
    }
    public void pagar() {
        System.out.println("Pago con cuenta corriente");
    }
    public String getMetodoPago() {
        return "Cuenta Corriente";
    }
    public LocalDate getFechaPago() {
        return LocalDate.now();
    }
    public LocalTime getHoraPago() {
        return LocalTime.now();
    }
}
