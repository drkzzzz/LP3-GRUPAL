package DrinkGo.DrinkGo_backend.security;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utilidad JWT basada en CLASE_API_REFERENCIA.md
 * Genera, valida y extrae información de tokens JWT.
 */
@Component
public class JwtUtil {

    private final SecretKey key = Jwts.SIG.HS256.key().build();
    private final long EXPIRATION_TIME = 100L * 365 * 24 * 60 * 60 * 1000; // ~100 años

    public String generarToken(String clienteId) {
        return Jwts.builder()
                .subject(clienteId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extraerClienteId(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
