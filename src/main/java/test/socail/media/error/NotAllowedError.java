package test.socail.media.error;

public class NotAllowedError extends RuntimeException{
    public NotAllowedError(String message) {
        super(message);
    }
}
