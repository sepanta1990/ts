package se.atg.service.harrykart.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String message;
    private List<String> details;

    public ErrorResponse() {
    }

    public ErrorResponse(String message, List<String> details) {
        this.message = message;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
