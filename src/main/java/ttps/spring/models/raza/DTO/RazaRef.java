package ttps.spring.models.raza.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Referencia ligera para Raza usada en requests: puede contener id (seleccionada) o nombre (manual)")
public record RazaRef(
        @Schema(description = "ID de la raza (si se seleccionó una existente)")
        Long id,

        @Schema(description = "Nombre de la raza (si se ingresa manualmente)")
        String nombre
) {
}

