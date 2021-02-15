package se.atg.service.harrykart.rest.exception.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Ali Fathizadeh 2021-02-15
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestDataException extends RuntimeException {
    public InvalidRequestDataException(String exception) {
        super(exception);
    }
}
