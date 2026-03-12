package ttps.spring.models.avistamiento.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Datos para actualizar un avistamiento")
public record AvistamientoUpdateRequest(

        @Schema(description = "Descripción detallada del avistamiento", example = "Vi a la mascota cerca del parque")
        String descripcion,

        @Schema(description = "Ubicación o coordenadas del avistamiento", example = "-31.4201,-64.1888")
        String ubicacion,

        @Schema(description = "Lista de fotos del avistamiento en formato base64")
        List<String> fotosBase64
) {}

