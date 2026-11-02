package cl.petsalud.petsalud_backend.controller;

import cl.petsalud.petsalud_backend.model.Usuario;
import cl.petsalud.petsalud_backend.repository.UsuarioRepository;
import cl.petsalud.petsalud_backend.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UsuarioRepository usuarioRepository, 
                          PasswordEncoder passwordEncoder, 
                          JwtService jwtService, 
                          AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // --- ENDPOINT 1: REGISTRO ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Verificamos si el usuario ya existe
        if (usuarioRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El usuario ya existe"));
        }

        // Creamos el nuevo usuario con el rol "CLIENTE" por defecto
        Usuario nuevoUsuario = new Usuario(
                request.username(),
                passwordEncoder.encode(request.password()), // Contraseña encriptada con BCrypt
                List.of("CLIENTE")
        );

        usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario registrado exitosamente"));
    }

    // --- ENDPOINT 2: LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Autenticamos al usuario con Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            // Si la autenticación es exitosa, buscamos al usuario y generamos el token
            UserDetails userDetails = usuarioRepository.findByUsername(request.username()).orElseThrow();
            String token = jwtService.generateToken(userDetails);

            // Devolvemos el token en un JSON
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", userDetails.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
    }

    // --- Clases auxiliares (Records) para recibir los datos JSON ---
    public record RegisterRequest(String username, String password) {}
    public record LoginRequest(String username, String password) {}
}