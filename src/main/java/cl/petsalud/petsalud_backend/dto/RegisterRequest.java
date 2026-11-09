package cl.petsalud.petsalud_backend.dto;

import cl.petsalud.petsalud_backend.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 20, message = "El usuario debe tener entre 4 y 20 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    // El rol que se asignará (CLIENTE, EMPLEADO, GERENTE)
    private Role role;

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}