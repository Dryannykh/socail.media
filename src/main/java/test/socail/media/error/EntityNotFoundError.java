package test.socail.media.error;

import lombok.Getter;

@Getter
public class EntityNotFoundError extends RuntimeException{
    public EntityNotFoundError(String message) {
        super(message);
    }
}
