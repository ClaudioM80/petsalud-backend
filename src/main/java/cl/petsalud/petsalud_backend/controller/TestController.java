package cl.petsalud.petsalud_backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    // Cualquier usuario autenticado puede entrar aquí
    @GetMapping("/general")
    public String general() {
        return "Hola! Estás autenticado con un token válido.";
    }

    // Solo los CLIENTES pueden entrar aquí
    @GetMapping("/cliente")
    @PreAuthorize("hasRole('CLIENTE')")
    public String cliente() {
        return "Hola CLIENTE, este es tu panel de mascotas.";
    }

    // Solo los EMPLEADOS pueden entrar aquí
    @GetMapping("/empleado")
    @PreAuthorize("hasRole('EMPLEADO')")
    public String empleado() {
        return "Hola EMPLEADO, aquí puedes ver las fichas médicas.";
    }

    // Solo los GERENTES pueden entrar aquí
    @GetMapping("/gerente")
    @PreAuthorize("hasRole('GERENTE')")
    public String gerente() {
        return "Hola GERENTE, aquí están los reportes de ventas de la clínica.";
    }
}