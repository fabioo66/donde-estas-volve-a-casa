package ttps.spring.models.raza.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import ttps.spring.models.raza.Raza;

@Schema(description = "DTO de respuesta para la entidad Raza")
public record RazaResponse(
        @Schema(description = "Identificador único de la raza", example = "1")
        Long id,

        @Schema(description = "Nombre de la raza", example = "Labrador Retriever")
        String nombre,

        @Schema(description = "ID del tipo de mascota al que pertenece la raza", example = "1")
        Long tipoMascotaId
) {
    public static RazaResponse from (Raza raza) {
        return new RazaResponse(
                raza.getId(),
                raza.getNombre(),
                raza.getTipo_mascota() != null ? raza.getTipo_mascota().getId() : null
        );
    }
}
