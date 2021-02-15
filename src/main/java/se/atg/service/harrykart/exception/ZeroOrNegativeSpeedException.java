package se.atg.service.harrykart.exception;

/**
 * @author Ali Fathizadeh 2021-02-15
 */
public class ZeroOrNegativeSpeedException extends IllegalArgumentException {

    public ZeroOrNegativeSpeedException(String message){
        super(message);
    }
}
