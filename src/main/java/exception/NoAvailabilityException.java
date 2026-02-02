package exception;

public class NoAvailabilityException extends RuntimeException{
    public NoAvailabilityException(final String message){
        super(message);
    }
}
