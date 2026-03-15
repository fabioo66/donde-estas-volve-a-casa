package ttps.spring.models.avistamiento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Datos necesarios para crear un avistamiento")
public record AvistamientoRequest(

        @NotNull
        @Schema(description = "ID de la mascota avistada", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long mascotaId,

        @NotNull
        @Schema(description = "ID del usuario que reporta el avistamiento", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long usuarioId,

        @NotBlank
        @Schema(description = "Descripción detallada del avistamiento", example = "Vi a la mascota cerca del parque")
        String descripcion,

        @NotBlank
        @Schema(description = "Ubicación o coordenadas del avistamiento", example = "-31.4201,-64.1888")
        String ubicacion,

        @Schema(description = "Lista de fotos del avistamiento en formato base64")
        List<String> fotosBase64
) {}

