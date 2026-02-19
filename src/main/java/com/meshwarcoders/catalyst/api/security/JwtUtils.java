package com.meshwarcoders.catalyst.api.security;

import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.model.common.UserType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtUtils {

    private SecureRandom secureRandom = new SecureRandom();
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private static final int MINUTE_IN_MS = 60000;

    private static final long ACCESS_TOKEN_EXPIRATION = 120L * MINUTE_IN_MS;

    private static final long CONFIRMATION_TOKEN_EXPIRATION = 2L * 60 * MINUTE_IN_MS;

    private static final long PASSWORD_RESET_EXPIRATION = 15L * MINUTE_IN_MS;

    private static final long REFRESH_TOKEN_EXPIRATION = 7L * 24 * 60 * MINUTE_IN_MS;

    public String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateAccessToken(String email, UserType userType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION);

        return Jwts.builder()
                .setSubject(email)
                .claim("userType",userType)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims validateAccessToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Token has expired.");
        } catch (MalformedJwtException | UnsupportedJwtException e) {
            throw new UnauthorizedException("Invalid token signature or format.");
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Token claims string is empty.");
        }
    }

    public String generateRefreshToken(String email){
        Date now  = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "refresh_token")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims validateRefreshToken(String token){
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (!"refresh_token".equals(claims.get("type"))){
                throw new UnauthorizedException("Invalid token signature or format.");
            }
            return claims;
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Token has expired.");
        } catch (MalformedJwtException | UnsupportedJwtException e) {
            throw new UnauthorizedException("Invalid token signature or format.");
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Token claims string is empty.");
        }
    }

    public String generateConfirmationToken(){
        return Jwts.builder()
                .claim("type", "confirmation_token")
                .setExpiration(new Date(System.currentTimeMillis() + CONFIRMATION_TOKEN_EXPIRATION))
                .signWith(getSigningKey(),SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims validateConfirmationToken(String token){
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (!"confirmation_token".equals(claims.get("type"))) {
                throw new UnauthorizedException("Invalid token signature or format.");
            }

            return claims;
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Token has expired.");
        } catch (MalformedJwtException | UnsupportedJwtException e) {
            throw new UnauthorizedException("Invalid token signature or format.");
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Token claims string is empty.");
        }
    }

    public String generateResetPasswordToken() {
        return Jwts.builder()
                .claim("type", "password_reset")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + PASSWORD_RESET_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims validateResetPasswordToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (!"password_reset".equals(claims.get("type"))) {
                throw new UnauthorizedException("Invalid token signature or format.");
            }

            return claims;
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Token has expired.");
        } catch (MalformedJwtException | UnsupportedJwtException e) {
            throw new UnauthorizedException("Invalid token signature or format.");
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Token claims string is empty.");
        }
    }
}