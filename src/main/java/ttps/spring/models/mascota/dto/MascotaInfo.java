package ttps.spring.models.mascota.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información básica de la mascota")
public record MascotaInfo(
        @Schema(description = "ID de la mascota") Long id,
        @Schema(description = "Nombre de la mascota") String nombre,
        @Schema(description = "Tipo de mascota") String tipo,
        @Schema(description = "Raza de la mascota") String raza,
        @Schema(description = "Color de la mascota") String color,
        @Schema(description = "Tamaño de la mascota") String tamanio,
        @Schema(description = "Fotos de la mascota en formato JSON") String fotos,
        @Schema(description = "Ciudad correspondiente a la coordenada") String ciudad,
        @Schema(description = "Provincia correspondiente a la coordenada") String provincia
) {}

