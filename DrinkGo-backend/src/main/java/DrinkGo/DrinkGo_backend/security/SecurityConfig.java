package DrinkGo.DrinkGo_backend.security;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Evita que Spring Boot registre el JwtFilter automáticamente como servlet filter.
     * Solo debe ejecutarse dentro de la cadena de Spring Security.
     */
    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter filter) {
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    /**
     * Configuración CORS para permitir requests desde el frontend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS habilitado para el frontend
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // CSRF deshabilitado (API REST stateless con JWT)
            .csrf(csrf -> csrf.disable())
            // Sin sesiones - cada request se autentica con su token
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // ============================================================
            // REGLAS DE AUTORIZACIÓN
            // ============================================================
            .authorizeHttpRequests(auth -> auth
                // --- Endpoints PÚBLICOS (no requieren token) ---
                .requestMatchers(HttpMethod.POST, "/restful/registros").permitAll()
                .requestMatchers(HttpMethod.POST, "/restful/token").permitAll()

                // --- Si necesitas más endpoints públicos, agrégalos aquí ---
                // .requestMatchers("/api/tienda-online/**").permitAll()

                // --- Todo lo demás requiere autenticación ---
                .anyRequest().authenticated()
            )
            // Registrar el JwtFilter ANTES del filtro de autenticación de Spring
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            // ============================================================
            // MANEJO DE ERRORES DE AUTENTICACIÓN → 401
            // ============================================================
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");

                    String jsonResponse = "{\"status\": 401, \"error\": \"No autorizado\", \"mensaje\": \"Se requiere un token Bearer válido en el header Authorization\"}";

                    PrintWriter writer = response.getWriter();
                    writer.write(jsonResponse);
                    writer.flush();
                })
            );

        return http.build();
    }
}
