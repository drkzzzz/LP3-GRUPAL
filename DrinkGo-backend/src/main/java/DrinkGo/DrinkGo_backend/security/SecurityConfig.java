package DrinkGo.DrinkGo_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad - MODO DESARROLLO
 * 
 * ⚠️ IMPORTANTE: Esta configuración permite acceso SIN AUTENTICACIÓN
 * para facilitar las pruebas del profesor.
 * 
 * Todos los endpoints están públicos:
 * - GET, POST, PUT, DELETE en /restful/planes
 * - GET, POST, PUT, DELETE en /restful/suscripciones  
 * - GET, POST, PUT, DELETE en /restful/configuracion
 * - POST /restful/token y /restful/registros (login y registro)
 * 
 * EN PRODUCCIÓN: Cambiar a .anyRequest().authenticated()
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtFilter jwtFilter
    ) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // MODO DESARROLLO: Todo público para pruebas
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
