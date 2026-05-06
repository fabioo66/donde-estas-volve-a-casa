package ttps.spring.models.raza.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para la creacion o edicion de una Raza")
public record RazaRequest(
        @NotNull
        @Schema(description = "ID de la raza, requerido para edicion, omitido para creacion")
        Long id,

        @NotNull
        @Schema(description = "Nombre de la raza, requerido para creacion y edicion")
        String nombre,

        @Schema(description = "ID del tipo de mascota al que pertenece la raza, requerido para creacion y edicion")
        Long tipoMascotaId
) {
}
