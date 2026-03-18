package ttps.spring.models.avistamiento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ttps.spring.models.avistamiento.Avistamiento;
import ttps.spring.models.mascota.dto.MascotaInfo;
import ttps.spring.models.usuario.dto.UsuarioInfo;
import ttps.utils.Georef_ar;

import java.time.LocalDate;
import java.util.Map;

@Schema(description = "Respuesta con datos completos de un avistamiento")
public record AvistamientoResponse(

        @Schema(description = "ID del avistamiento", example = "1")
        Long id,

        @Schema(description = "Coordenadas del avistamiento", example = "-31.4201,-64.1888")
        String coordenada,

        @Schema(description = "Descripción del avistamiento")
        String descripcion,

        @Schema(description = "Fecha del avistamiento")
        LocalDate fecha,

        @Schema(description = "URLs de las fotos en formato JSON")
        String fotos,

        @Schema(description = "Estado activo del avistamiento")
        boolean activo,

        @Schema(description = "Información de la mascota avistada")
        MascotaInfo mascota,

        @Schema(description = "Información del usuario que reportó")
        UsuarioInfo usuario
) {

    public static AvistamientoResponse from(Avistamiento avistamiento) {
        MascotaInfo mascotaInfo = null;
        String municipio = null;
        String provincia = null;
        if (avistamiento.getCoordenada() != null && !avistamiento.getCoordenada().isEmpty()) {
            try {
                Map<String, String> datos = ttps.utils.Georef_ar.getDatos(avistamiento.getCoordenada());
                municipio = datos.get("municipio");
                provincia = datos.get("provincia");
            } catch (Exception e) {
                municipio = "Desconocido";
                provincia = "Desconocido";
            }
        }

        if (avistamiento.getMascota() != null) {
            var m = avistamiento.getMascota();
            mascotaInfo = new MascotaInfo(
                    m.getId(),
                    m.getNombre(),
                    m.getTipo(),
                    m.getRaza(),
                    m.getColor(),
                    m.getTamanio() != null ? m.getTamanio().name() : null,
                    m.getFotos(),
                    municipio,
                    provincia
            );
        }

        UsuarioInfo usuarioInfo = null;
        if (avistamiento.getUsuario() != null) {
            var u = avistamiento.getUsuario();
            usuarioInfo = new UsuarioInfo(u.getId(), u.getNombre(), u.getEmail());
        }

        return new AvistamientoResponse(
                avistamiento.getId(),
                avistamiento.getCoordenada(),
                avistamiento.getDescripcion(),
                avistamiento.getFecha(),
                avistamiento.getFotos(),
                avistamiento.isActivo(),
                mascotaInfo,
                usuarioInfo
        );
    }
}

