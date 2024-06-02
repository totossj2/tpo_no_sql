package codigo.exceptions;

public class ErrorConnectionRedisException extends Exception {

    private static final long serialVersionUID = 601128023082493198L;

    public ErrorConnectionRedisException(String mensaje) {
        super(mensaje);
    }

}