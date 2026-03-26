package ttps.spring.models.tipo_mascota.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record Tipo_mascotaRequest(
        @NotNull
        @Schema(description = "ID del tipo de mascota", example = "1")
        Long id,

        @NotNull
        @Schema(description = "Nombre del tipo de mascota", example = "Perro", required = true)
        String nombre
) {
}
