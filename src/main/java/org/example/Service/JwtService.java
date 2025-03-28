package org.example.Service;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.Model.User;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    private final String SECRET_KEY;
    public JwtService(){
        Dotenv dotenv = Dotenv.load();
        SECRET_KEY = dotenv.get("JWT_SECRET");

        if (SECRET_KEY == null) {
            throw new IllegalArgumentException("JWT_SECRET is not set in .env file");
        }
    }

    public String generateToken(User user) {
        long EXPIRATION_TIME = 86400000;
        return Jwts.builder()
                .setSubject(user.getNumberPhone())
                .claim("userId", user.getId())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
