package ttps.spring.models.tipo_mascota.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import ttps.spring.models.tipo_mascota.Tipo_mascota;

@Schema(description = "DTO de respuesta para la entidad TipoMascota")
public record Tipo_mascotaResponse(
        @Schema(description = "ID del tipo de mascota", example = "1")
        Long id,

        @Schema(description = "Nombre del tipo de mascota", example = "Perro")
        String nombre
) {
    public static Tipo_mascotaResponse from (Tipo_mascota tipoMascota) {
        return new Tipo_mascotaResponse(
                tipoMascota.getId(),
                tipoMascota.getNombre()
        );
    }
}
