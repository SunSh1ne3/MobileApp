package org.example.DTO.Response;

import org.springframework.stereotype.Component;

@Component
public class AuthResponse {
    private String token;
    public AuthResponse() {}
    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
