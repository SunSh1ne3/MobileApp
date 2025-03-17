package org.example.Service;

import io.jsonwebtoken.SignatureAlgorithm;
import org.example.Model.User;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    private final String SECRET_KEY = System.getenv("JWT_SECRET");
    private final long EXPIRATION_TIME = 86400000;

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getNumberPhone())
                .claim("userId", user.getId())
                //.claim("order", user.getTypeOrder())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

//    public String generateToken(String NumberPhone, Long ID) {
//        return Jwts.builder()
//                .setSubject(NumberPhone)
//                .claim("userId", ID)
//                //.claim("order", user.getTypeOrder())
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//                .compact();
//    }

}
