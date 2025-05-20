package org.example.DTO.Response;

import org.springframework.stereotype.Component;

@Component
public class ErrorResponse {
    private String message;
    private String error;
    private Integer code;
    public ErrorResponse() {}
    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(String message, String error, int code) {
        this.message = message;
        this.error = error;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }
}