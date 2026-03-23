package ttps.spring.models.mascota.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ttps.spring.models.mascota.Estado;
import ttps.spring.models.mascota.Tamanio;
import ttps.spring.models.raza.Raza;
import ttps.spring.models.tipo_mascota.Tipo_mascota;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "DTO para la creación o actualización de una mascota")
public record MascotaRequest(
        @NotNull
        @Schema(description = "Identificador único de la mascota")
        Long id,

        @NotNull
        @Schema(description = "Nombre de la mascota", example = "Firulais")
        String nombre,

        @NotNull
        @Schema(description = "Tamaño de la mascota", example = "MEDIANO")
        Tamanio tamanio,

        @NotNull
        @Schema(description = "Color de la mascota", example = "Marrón")
        String color,

        @NotNull
        @Schema(description = "Fecha de pérdida o adopción", example = "2024-06-01")
        LocalDate fecha,

        @NotNull
        @Schema(description = "Descripción de la mascota", example = "Perro mestizo de tamaño mediano, color marrón con manchas blancas.")
        String descripcion,

        @NotNull
        @Schema(description = "Estado de la mascota", example = "PERDIDO_PROPIO")
        Estado estado,

        @Schema(description = "Lista de fotos en formato Base64")
        List<String> fotosBase64,

        @Schema(description = "Coordenadas del lugar donde se perdió o se encuentra la mascota", example = "-34.6037,-58.3816")
        String coordenadas,

        @NotNull
        @Schema(description = "Tipo de mascota", example = "PERRO")
        Tipo_mascota tipo_mascota,

        @Schema(description = "Raza de la mascota", example = "Mestizo")
        Raza raza,

        @Schema(description = "Mascota activa o inactiva")
        Boolean activa,

        @NotNull
        @Schema(description = "Identificador del usuario propietario de la mascota", example = "1")
        Long usuarioId
) {
}
