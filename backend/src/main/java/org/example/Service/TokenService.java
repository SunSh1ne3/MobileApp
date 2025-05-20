package org.example.Service;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.Model.User;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class TokenService {
    private final String SECRET_KEY;
    private static final long TOKEN_EXPIRATION  = 86400000; // 24 часа

    public TokenService(){
        Dotenv dotenv = Dotenv.load();
        SECRET_KEY = dotenv.get("JWT_SECRET");

        if (SECRET_KEY == null || SECRET_KEY.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET must be at least 32 characters long");
        }
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getUserRole());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getNumberPhone())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(TOKEN_EXPIRATION)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractNumberPhone(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getTokenValidationError(String token) {
        try {
            if (isTokenExpired(token)) {
                return "Token expired";
            }
            return "Token is valid";
        } catch (ExpiredJwtException e) {
            return "Token expired";
        } catch (UnsupportedJwtException e) {
            return "Unsupported token format";
        } catch (MalformedJwtException e) {
            return "Malformed token";
        } catch (SignatureException e) {
            return "Invalid token signature";
        } catch (IllegalArgumentException e) {
            return "Token is empty or null";
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
