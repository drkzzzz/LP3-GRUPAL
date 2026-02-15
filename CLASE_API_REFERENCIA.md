# 📌 Referencia Técnica -- Implementación de APIs con JWT (Trabajo en Clase)

Este documento contiene la estructura base utilizada en los ejercicios
prácticos de clase para la implementación de APIs REST con autenticación
JWT en Spring Boot.

El objetivo es reutilizar esta arquitectura en el proyecto DrinkGo,
específicamente en el Bloque 5 (Inventario, Lotes y Movimientos).

------------------------------------------------------------------------

# 🏗 Arquitectura Usada en Clase

Estructura por capas obligatoria:

controller/ entity/ repository/ security/ service/

No se usó acceso directo a repositorios desde los controladores.

------------------------------------------------------------------------

# 🔐 Seguridad Implementada

Flujo trabajado en clase:

1.  Registro de usuario
2.  Generación de cliente_id usando SHA-256
3.  Encriptación de llave_secreta con BCrypt
4.  Generación de token JWT
5.  Validación de token mediante filtro

------------------------------------------------------------------------

# 🧠 Generación de cliente_id (SHA-256)

``` java
public void setCliente_id(String cliente_id) {
    String datos = nombres + apellidos + email;
    MessageDigest md = null;
    try {
        md = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    }
    md.update(datos.getBytes());
    byte[] digest = md.digest();
    String result = new BigInteger(1, digest).toString(16).toLowerCase();
    cliente_id = result;
    this.cliente_id = cliente_id;
}
```

------------------------------------------------------------------------

# 🔒 Encriptación de llave_secreta (BCrypt)

``` java
public void setLlave_secreta(String llave_secreta) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    this.llave_secreta = encoder.encode(llave_secreta);
}
```

------------------------------------------------------------------------

# 🎟 Generación de Token (JwtUtil)

``` java
@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_TIME = 100L * 365 * 24 * 60 * 60 * 1000;

    public String generarToken(String clienteId) {
        return Jwts.builder()
                .setSubject(clienteId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extraerClienteId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
```

------------------------------------------------------------------------

# 🛡 JwtFilter

``` java
@Component
public class JwtFilter extends GenericFilter {

    @Autowired
    private RegistrosRepository registrosRepository;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            Optional<Registros> match = registrosRepository.findAll()
                    .stream()
                    .filter(r -> token.equals(r.getAccess_token()))
                    .findFirst();

            if (match.isPresent()) {
                String clienteId = match.get().getCliente_id();

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                clienteId,
                                null,
                                Collections.emptyList()
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(req, res);
    }
}
```

------------------------------------------------------------------------

# ⚙ SecurityConfig

``` java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtFilter jwtFilter
    ) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/restful/token",
                                         "/restful/registros")
                        .permitAll()
                        .anyRequest().authenticated()
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
```

------------------------------------------------------------------------

# 📌 Consideraciones Importantes

-   Se utilizó borrado lógico con:
    -   @SQLDelete
    -   @SQLRestriction
-   No se eliminan registros físicamente.
-   El token se guarda en base de datos.
-   El cliente_id se usa como identificador del tenant.
-   El profesor valida funcionamiento mediante Postman.

------------------------------------------------------------------------

# 🎯 Uso en DrinkGo

En el proyecto DrinkGo:

-   Reutilizar esta arquitectura.
-   Adaptar la seguridad al Bloque 5.
-   Extraer cliente_id desde SecurityContextHolder.
-   Aplicar lógica multi-tenant en Inventario, Lotes y Movimientos.
