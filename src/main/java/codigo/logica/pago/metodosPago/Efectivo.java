package codigo.logica.pago.metodosPago;

import codigo.logica.pago.MedioPago;

import java.time.LocalDate;
import java.time.LocalTime;

public class Efectivo implements MedioPago{
    private int monto;

    public Efectivo(int monto) {
        this.monto = monto;
    }

    public int getMonto() {
        return monto;
    }

    public void pagar() {
        System.out.println("Pago con efectivo de un monto de: " + monto);
    }

    public String getMetodoPago() {
        return "Efectivo";
    }
    public LocalDate getFechaPago() {
        return LocalDate.now();
    }
    public LocalTime getHoraPago() {
        return LocalTime.now();
    }

}
