package ttps.spring.models.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest (
    @Schema(description = "Email del usuario", example = "diego410@gmail.com", required = true)
    String email,
    @Schema(description = "Contraseña del usuario", example = "password123", required = true)
    String contrasenia
) {}
