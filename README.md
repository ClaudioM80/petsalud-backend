# Clínica Petsalud - Backend Seguro

Proyecto Spring Boot con Spring Security y JWT.

## Ejecución
`mvn spring-boot:run`

## Endpoints
- `/api/auth/login` (Público)
- `/api/auth/register` (Público)
- `/api/test/cliente` (Requiere rol CLIENTE)
- `/api/test/empleado` (Requiere rol EMPLEADO)
- `/api/test/gerente` (Requiere rol GERENTE)
