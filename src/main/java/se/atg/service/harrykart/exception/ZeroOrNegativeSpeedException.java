package se.atg.service.harrykart.exception;

public class ZeroOrNegativeSpeedException extends IllegalArgumentException {

    public ZeroOrNegativeSpeedException(String message){
        super(message);
    }
}
