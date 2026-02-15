package DrinkGo.DrinkGo_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

/**
 * Utilidad para generación y validación de tokens JWT.
 * Genera tokens firmados con HMAC-SHA256.
 * Incluye método para generar hash SHA-256 del token (para almacenar en sesiones_usuario).
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration.ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Genera un token JWT con los claims del usuario autenticado.
     * Claims: sub (usuario_id), negocio_id, rol, iat, exp
     */
    public String generarToken(Long usuarioId, Long negocioId, String rol) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(usuarioId))
                .claims(Map.of(
                        "negocio_id", negocioId,
                        "rol", rol
                ))
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(key)
                .compact();
    }

    /**
     * Valida la firma y expiración del token JWT.
     */
    public boolean validarToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extrae el usuario_id (subject) del token.
     */
    public Long extraerUsuarioId(String token) {
        Claims claims = extraerClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Extrae el negocio_id del token.
     */
    public Long extraerNegocioId(String token) {
        Claims claims = extraerClaims(token);
        return claims.get("negocio_id", Long.class);
    }

    /**
     * Extrae el rol del token.
     */
    public String extraerRol(String token) {
        Claims claims = extraerClaims(token);
        return claims.get("rol", String.class);
    }

    /**
     * Extrae la fecha de expiración del token.
     */
    public Date extraerExpiracion(String token) {
        return extraerClaims(token).getExpiration();
    }

    /**
     * Genera hash SHA-256 del token JWT.
     * Este hash se almacena en sesiones_usuario.hash_token.
     * El token plano NUNCA se guarda en base de datos.
     */
    public static String generarHashSHA256(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash SHA-256", e);
        }
    }

    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}
