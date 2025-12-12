package org.example.colis.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.colis.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private static final String ISSUER = "colis-api";
    private static final String ROLE_CLAIM = "role";
    
    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getLogin())
                .withClaim(ROLE_CLAIM, user.getRole().name())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(algorithm);
    }
    
    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        
        return JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()
                .verify(token);
    }
    
    public String extractUsername(String token) {
        DecodedJWT jwt = validateToken(token);
        return jwt.getSubject();
    }
    
    public String extractRole(String token) {
        DecodedJWT jwt = validateToken(token);
        return jwt.getClaim(ROLE_CLAIM).asString();
    }
}
