package cl.petsalud.petsalud_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll() // Endpoint público
                .anyRequest().authenticated() // Todo lo demás requiere login
            )
            .httpBasic(Customizer.withDefaults()); // <-- Línea corregida

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Creamos un usuario de prueba: admin / admin123 con rol ADMIN
        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}admin123") // {noop} significa que no se encripta (solo para pruebas)
                .roles("ADMIN")
                .build();

        // Creamos un usuario de prueba: cliente / cliente123 con rol USER
        UserDetails cliente = User.builder()
                .username("cliente")
                .password("{noop}cliente123")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, cliente);
    }
}