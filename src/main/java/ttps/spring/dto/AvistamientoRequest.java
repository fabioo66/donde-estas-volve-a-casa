package ttps.spring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Datos necesarios para crear o actualizar un avistamiento")
public class AvistamientoRequest {

    @Schema(description = "ID de la mascota avistada", example = "1", required = true)
    private Long mascotaId;

    @Schema(description = "ID del usuario que reporta el avistamiento", example = "1", required = true)
    private Long usuarioId;

    @Schema(description = "Descripción detallada del avistamiento", example = "Vi a la mascota cerca del parque")
    private String descripcion;

    @Schema(description = "Ubicación o coordenadas del avistamiento", example = "-31.4201,-64.1888")
    private String ubicacion;

    @Schema(description = "Lista de fotos del avistamiento en formato base64")
    private List<String> fotosBase64;

    // Getters y Setters
    public Long getMascotaId() { return mascotaId; }
    public void setMascotaId(Long mascotaId) { this.mascotaId = mascotaId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public List<String> getFotosBase64() { return fotosBase64; }
    public void setFotosBase64(List<String> fotosBase64) { this.fotosBase64 = fotosBase64; }
}
