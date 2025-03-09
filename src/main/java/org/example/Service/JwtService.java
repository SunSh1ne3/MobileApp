package org.example.Service;

import io.jsonwebtoken.SignatureAlgorithm;
import org.example.Model.User;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    private final String SECRET_KEY = "your_secret_key"; // Замените на более безопасный ключ
    private final long EXPIRATION_TIME = 86400000; // 1 день в миллисекундах

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getNumberPhone())
                .claim("userId", user.getId())
                .claim("order", user.getOrder())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

}
