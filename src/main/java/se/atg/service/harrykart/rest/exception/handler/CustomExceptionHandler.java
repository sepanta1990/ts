package se.atg.service.harrykart.rest.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import se.atg.service.harrykart.model.ErrorResponse;
import se.atg.service.harrykart.rest.exception.exception.InvalidRequestDataException;

import java.util.ArrayList;
import java.util.List;

public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidRequestDataException.class)
    public final ResponseEntity<Object> handleInvalidRequestDataException(InvalidRequestDataException ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("Request is not valid. ", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
