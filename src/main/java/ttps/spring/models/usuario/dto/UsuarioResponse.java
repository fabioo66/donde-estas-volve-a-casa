package ttps.spring.models.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioResponse (
    @Schema(description = "Nombre del usuario", example = "Diego", required = true)
    String nombre,
    @Schema(description = "Apellido del usuario", example = "Gomez", required = true)
    String apellido,
    @Schema(description = "Email del usuario", example = "diego410@gmail.com", required = true)
    String email,
    @Schema(description = "Contrasenia", example = "password213", required = true)
    String contrasenia
) {}
