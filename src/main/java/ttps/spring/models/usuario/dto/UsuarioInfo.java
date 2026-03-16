package ttps.spring.models.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información básica del usuario")
public record UsuarioInfo(
        @Schema(description = "ID del usuario") Long id,
        @Schema(description = "Nombre del usuario") String nombre,
        @Schema(description = "Email del usuario") String email
) {}

