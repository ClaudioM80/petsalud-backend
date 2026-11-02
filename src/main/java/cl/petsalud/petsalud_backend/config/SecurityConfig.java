package cl.petsalud.petsalud_backend.config;

import cl.petsalud.petsalud_backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // <-- Importante
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // <-- ¡ESTA ES LA LÍNEA QUE FALTABA! Habilita los @PreAuthorize
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // Inyectamos nuestro filtro JWT
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitar CSRF (común en APIs REST stateless)
            .csrf(csrf -> csrf.disable())
            
            // 2. Definir las reglas de autorización de rutas
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas: Login y Registro
                .requestMatchers("/api/auth/**").permitAll() 
                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated() 
            )
            
            // 3. Configurar la aplicación como STATELESS (sin estado, sin sesiones)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 4. Añadir nuestro filtro JWT antes del filtro estándar
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Usamos BCrypt para encriptar las contraseñas en la base de datos
        return new BCryptPasswordEncoder();
    }

    // Bean para validar usuario y contraseña durante el Login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}