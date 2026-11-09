package cl.petsalud.petsalud_backend.controller;

import cl.petsalud.petsalud_backend.dto.LoginRequest;
import cl.petsalud.petsalud_backend.dto.RegisterRequest;
import cl.petsalud.petsalud_backend.model.Usuario;
import cl.petsalud.petsalud_backend.repository.UsuarioRepository;
import cl.petsalud.petsalud_backend.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // Verificamos si el usuario ya existe
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El usuario ya existe"));
        }

        // Determinamos el rol (si no viene en el JSON, por defecto es CLIENTE)
        String rolAsignado = (request.getRole() != null) ? request.getRole().name() : "CLIENTE";

        // Creamos el nuevo usuario
        // NOTA: Asumo que tu constructor de Usuario recibe (String, String, List<String>)
        Usuario nuevoUsuario = new Usuario(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()), // Contraseña encriptada con BCrypt
                List.of(rolAsignado)
        );

        usuarioRepository.save(nuevoUsuario);
        
        // MEJORA: Devolvemos un 201 (Created) en lugar de 200
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Usuario registrado exitosamente"));
    }

    // --- ENDPOINT 2: LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Autenticamos al usuario con Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Si la autenticación es exitosa, buscamos al usuario y generamos el token
            UserDetails userDetails = usuarioRepository.findByUsername(request.getUsername()).orElseThrow();
            String token = jwtService.generateToken(userDetails);

            // Devolvemos el token en un JSON
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", userDetails.getUsername());
            response.put("mensaje", "Login exitoso");
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Si falla la autenticación, devolvemos 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
        }
    }
}