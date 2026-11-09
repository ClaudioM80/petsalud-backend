package cl.petsalud.petsalud_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Clave fija suficientemente larga (mínimo 32 bytes para HS256)
    private static final String SECRET_KEY = "MiClaveSuperSecretaParaPetsalud2024SuperSeguraConSuficientesCaracteres";
    
    // Tiempo de expiración: 10 horas (en milisegundos)
    private static final long JWT_EXPIRATION = 1000 * 60 * 60 * 10;

    // 1. Generar un token para un usuario
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Validar si el token es correcto y pertenece al usuario (CON DEBUG)
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        
        // --- TRAMPA DE DEBUG ---
        System.out.println("=== DEBUG JWT ===");
        System.out.println("Usuario en el Token: [" + username + "]");
        System.out.println("Usuario en la BD: [" + userDetails.getUsername() + "]");
        System.out.println("Token expirado?: " + isTokenExpired(token));
        System.out.println("=================");
        // -----------------------

        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // 3. Extraer el nombre de usuario del token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
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
        // Forzamos UTF-8 para evitar problemas de codificación de bytes
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}