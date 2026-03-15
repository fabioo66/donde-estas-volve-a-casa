package ttps.spring.models.mascota.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ttps.spring.models.mascota.Estado;
import ttps.spring.models.mascota.Mascota;
import ttps.spring.models.mascota.Tamanio;

@Schema(description = "Respuesta que contiene los datos de una mascota")
public record MascotaResponse(

        @Schema(description = "ID de la mascota", example = "1")
        Long id,

        @Schema(description = "Nombre de la mascota", example = "Firulais")
        String nombre,

        @Schema(description = "Tamaño de la mascota", example = "MEDIANO")
        Tamanio tamanio,

        @Schema(description = "Color de la mascota", example = "Marrón")
        String color,

        @Schema(description = "Fecha de pérdida o adopción", example = "2024-06-01")
        String fecha,

        @Schema(description = "Descripción adicional de la mascota", example = "Es un perro muy amigable")
        String descripcion,

        @Schema(description = "Estado actual de la mascota", example = "PERDIDA")
        Estado estado,

        @Schema(description = "URLs de las fotos en formato JSON")
        String fotos,

        @Schema(description = "Coordenadas del lugar donde se perdió o se encuentra la mascota", example = "-34.6037,-58.3816")
        String coordenadas,

        @Schema(description = "Tipo de mascota", example = "PERRO")
        String tipo,

        @Schema(description = "Raza de la mascota", example = "Labrador")
        String raza,

        @Schema(description = "Mascota activa o inactiva")
        Boolean activa,

        @Schema(description = "Identificador del usuario propietario de la mascota", example = "1")
        Long usuarioId
) {

    public static MascotaResponse from(Mascota mascota) {
        return new MascotaResponse(
                mascota.getId(),
                mascota.getNombre(),
                mascota.getTamanio(),
                mascota.getColor(),
                mascota.getFecha().toString(),
                mascota.getDescripcion(),
                mascota.getEstado(),
                mascota.getFotos(), // Convertir la lista de fotos a JSON
                mascota.getCoordenadas(),
                mascota.getTipo(),
                mascota.getRaza(),
                mascota.isActivo(),
                mascota.getUsuario().getId()
        );
    }
}
