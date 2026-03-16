package ttps.spring.models.usuario.dto;

public record LoginResponse(
        Long id,
        String email,
        String rol,
        String token,
        String nombre,
        String apellido
) {}