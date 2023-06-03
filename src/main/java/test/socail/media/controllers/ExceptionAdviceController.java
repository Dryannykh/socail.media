package test.socail.media.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import test.socail.media.error.EntityNotFoundError;
import test.socail.media.error.NotAllowedError;
import test.socail.media.error.ValidationDataError;
import test.socail.media.error.TokenError;
import test.social.media.dto.ErrorResponse;


@ControllerAdvice
public class ExceptionAdviceController {
    @ExceptionHandler(EntityNotFoundError.class)
    public ResponseEntity<ErrorResponse> handleException(EntityNotFoundError e) {
        ErrorResponse errorResponseDto = new ErrorResponse();
        errorResponseDto.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Internal server error: " + e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TokenError.class)
    public ResponseEntity<ErrorResponse> handleException(TokenError e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(498));
    }

    @ExceptionHandler(NotAllowedError.class)
    public ResponseEntity<ErrorResponse> handleException(NotAllowedError e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(405));
    }

    @ExceptionHandler(ValidationDataError.class)
    public ResponseEntity<ErrorResponse> handleException(ValidationDataError e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(422));
    }
}