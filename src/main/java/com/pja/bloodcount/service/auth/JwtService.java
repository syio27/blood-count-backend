package com.pja.bloodcount.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

//    private final LocalDateTime now = LocalDateTime.now();
//    private final LocalDateTime expiration = now.plusMinutes(24);
//    private final LocalDateTime issuedAt = LocalDateTime.from(now);
//    private final LocalDateTime expiresAt = LocalDateTime.from(expiration);
    private Date issuedAt;
    private Date expiresAt;

    @Value("${app.secretKey}")
    private String SECRET_KEY;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails, Integer timezoneOffset) {
        return generateToken(new HashMap<>(), userDetails, timezoneOffset);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            Integer timezoneOffset
    ) {
        adjustExpirationForTimezone(timezoneOffset);
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        log.info("USERNAME: {}", username);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private void adjustExpirationForTimezone(Integer timezoneOffset){
        Instant now = Instant.now();
        ZonedDateTime zonedNow = ZonedDateTime.ofInstant(now, ZoneOffset.UTC)
                .plusMinutes(timezoneOffset);
        ZonedDateTime zonedExpiration = zonedNow.plusMinutes(360);

        this.issuedAt = Date.from(zonedNow.toInstant());
        this.expiresAt = Date.from(zonedExpiration.toInstant());
    }
}