package ttps.spring.dto;

import java.time.LocalDateTime;

public class AvistamientoRequest {
    private Long mascotaId;
    private String descripcion;
    private String ubicacion;
    private String foto;

    // Getters y Setters
    public Long getMascotaId() { return mascotaId; }
    public void setMascotaId(Long mascotaId) { this.mascotaId = mascotaId; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}
