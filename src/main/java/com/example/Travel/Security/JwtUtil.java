package com.example.Travel.Security;

import com.example.Travel.entity.Role;
import com.example.Travel.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret:}")
    private String SECRET_KEY;

    @Value("${app.jwt.expiration:86400000}")
    private long EXPIRATION_TIME;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        // Always generate a new secure key
        signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String encodedKey = Base64.getEncoder().encodeToString(signingKey.getEncoded());

        System.out.println("🔑 Generated JWT Secret (add to application.properties):");
        System.out.println("app.jwt.secret=" + encodedKey);
        System.out.println("✅ Using auto-generated 512-bit key");
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // FIX THIS METHOD - Add roles properly
    public String generateTokenFromUser(User user) {
        System.out.println("🎭 Generating token for user: " + user.getUsername());
        System.out.println("🎭 User roles: " + user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));

        Claims claims = Jwts.claims().setSubject(user.getUsername());

        // CRITICAL FIX: Add roles to token
        claims.put("roles", user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));

        claims.put("email", user.getEmail());
        claims.put("userId", user.getId());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        System.out.println("🔍 Validating token: " + (token != null ? token.substring(0, Math.min(20, token.length())) : "null") + "...");

        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            System.out.println("✅ Token valid");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("❌ Token invalid: " + e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}

//package com.example.Travel.Security;
//
//import com.example.Travel.entity.Role;
//import com.example.Travel.entity.User;
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//import java.util.stream.Collectors;
//
//@Component
//public class JwtUtil {
//
//    @Value("${app.jwt.secret:}")
//    private String SECRET_KEY;
//
//    @Value("${app.jwt.expiration:86400000}")
//    private long EXPIRATION_TIME;
//
//    private SecretKey signingKey;
//
//    @PostConstruct
//    public void init() {
//        if (SECRET_KEY == null || SECRET_KEY.trim().isEmpty()) {
//            // Generate secure key automatically
//            signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
//            System.out.println("✅ Generated secure 512-bit JWT key automatically");
//        } else {
//            try {
//                byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
//                int keySize = keyBytes.length * 8;
//
//                if (keySize < 512) {
//                    System.out.println("⚠️  Key too small (" + keySize + " bits). Generating secure 512-bit key.");
//                    signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
//                } else {
//                    signingKey = Keys.hmacShaKeyFor(keyBytes);
//                    System.out.println("✅ Using configured key: " + keySize + " bits");
//                }
//            } catch (Exception e) {
//                System.out.println("⚠️  Invalid JWT secret. Generating secure key.");
//                signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
//            }
//        }
//        System.out.println("🔑 Actual signing key (first 20 chars): " +
//                new String(signingKey.getEncoded()).substring(0, 20) + "...");
//    }
//
//    public String generateToken(String username) {
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(signingKey, SignatureAlgorithm.HS512)
//                .compact();
//    }
//
//    public String generateTokenFromUser(User user) {
//        Claims claims = Jwts.claims().setSubject(user.getUsername());
//        claims.put("roles", user.getRoles().stream()
//                .map(Role::getName)
//                .collect(Collectors.toList()));
//        claims.put("email", user.getEmail());
//        claims.put("userId", user.getId());
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(signingKey, SignatureAlgorithm.HS512)
//                .compact();
//    }
//
//    public boolean validateToken(String token) {
//        System.out.println("🔍 Validating token: " + token.substring(0, 20) + "...");
//
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(signingKey)
//                    .build()
//                    .parseClaimsJws(token);
//            System.out.println("✅ Token valid");
//            return true;
//        } catch (JwtException | IllegalArgumentException e) {
//            System.out.println("❌ Token invalid: " + e.getMessage());
//            return false;
//        }
//    }
//
//    public String extractUsername(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(signingKey)
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//}
