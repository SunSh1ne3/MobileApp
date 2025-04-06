package org.example.Service;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.Model.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(EXPIRATION_TIME)))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Claims extractAllData(String token){
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractNumberPhone(String token){
        return (String) extractAllData(token).get("userId");
    }

    public boolean validateToken(String token){
        return (extractAllData(token) != null) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        Claims claims = extractAllData(token);
        return claims.getExpiration().before(new Date());
    }
}
