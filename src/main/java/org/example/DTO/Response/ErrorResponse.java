package org.example.DTO.Response;

import org.springframework.stereotype.Component;

@Component
public class ErrorResponse {
    private String message;
    public ErrorResponse() {}
    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}