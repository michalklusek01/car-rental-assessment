package exception;

public class ReservationNotFoundException extends RuntimeException{
    public ReservationNotFoundException(final String message) {
        super(message);
    }
}
