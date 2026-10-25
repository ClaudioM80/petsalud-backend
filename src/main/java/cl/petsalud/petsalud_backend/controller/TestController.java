package cl.petsalud.petsalud_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/public/saludo")
    public String saludoPublico() {
        return "Este es un endpoint público de PETSALUD. No necesitas login.";
    }

    @GetMapping("/privado/citas")
    public String citasPrivadas() {
        return "Este es el listado de citas médicas. Necesitas estar autenticado.";
    }
}