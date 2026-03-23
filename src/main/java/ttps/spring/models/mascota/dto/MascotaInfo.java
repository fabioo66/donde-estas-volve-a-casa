package ttps.spring.models.mascota.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ttps.spring.models.raza.Raza;
import ttps.spring.models.tipo_mascota.Tipo_mascota;

@Schema(description = "Información básica de la mascota")
public record MascotaInfo(
        @Schema(description = "ID de la mascota") Long id,
        @Schema(description = "Nombre de la mascota") String nombre,
        @Schema(description = "Tipo de mascota") Tipo_mascota tipo_mascota,
        @Schema(description = "Raza de la mascota") Raza raza,
        @Schema(description = "Color de la mascota") String color,
        @Schema(description = "Tamaño de la mascota") String tamanio,
        @Schema(description = "Fotos de la mascota en formato JSON") String fotos,
        @Schema(description = "Ciudad correspondiente a la coordenada") String ciudad,
        @Schema(description = "Provincia correspondiente a la coordenada") String provincia
) {}

