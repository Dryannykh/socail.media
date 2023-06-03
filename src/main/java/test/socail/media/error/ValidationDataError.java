package test.socail.media.error;

public class ValidationDataError extends RuntimeException{
    public ValidationDataError(String message) {
        super(message);
    }
}