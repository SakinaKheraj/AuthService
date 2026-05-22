    package org.example.service;

    import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

    import io.jsonwebtoken.Claims;
    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.io.Decoders;
    import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureAlgorithm;

    @Service
    public class JwtService {

        public static final String SECRET = "7gY0q3uM4mM8j8Fv9hV2kN5uL1sXcR6bT0pQaWzYxNk=";

        public String extractUsername(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
            final Claims claims = extractAllClaims(token);
            return claimResolver.apply(claims);
        }

        public Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }

        private Boolean isTokenExpired(String token) {
            return extractExpiration(token).before(new Date());
        }

        public Boolean validateToken(String token, UserDetails userDetails) {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        }

        private String createToken(Map<String, Object> claims, String username) {
            return Jwts.builder()
                        .claims(claims)
                        .subject(username)
                        .issuedAt(new Date(System.currentTimeMillis()))
                        .expiration(new Date(System.currentTimeMillis()+1000*60*1))
                        .signWith(getSignKey())
                        .compact();
        }
        private Claims extractAllClaims(String token) {
            return Jwts
                    .parser() // for chaining like builder
                    .verifyWith((SecretKey) getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        }

        private Key getSignKey() {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET);
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }