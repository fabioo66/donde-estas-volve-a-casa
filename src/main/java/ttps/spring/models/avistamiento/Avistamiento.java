package ttps.spring.models.avistamiento;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ttps.spring.models.avistamiento.dto.AvistamientoRequest;
import ttps.spring.models.avistamiento.dto.AvistamientoUpdateRequest;
import ttps.spring.models.mascota.Mascota;
import ttps.spring.models.usuario.Usuario;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "avistamiento")
@Schema(description = "Representa un avistamiento de una mascota perdida")
public class Avistamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="avistamiento_id")
    @Schema(description = "ID único del avistamiento", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name="usuario_id")
    @JsonBackReference("usuario-avistamientos")
    @Schema(description = "Usuario que reportó el avistamiento")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name="mascota_id")
    @JsonBackReference("mascota-avistamientos")
    @Schema(description = "Mascota que fue avistada")
    private Mascota mascota;

    @Setter
    @Schema(description = "Fotos del avistamiento en formato JSON con URLs", example = "[\"/uploads/avistamiento_1.jpg\"]")
    @Column(columnDefinition = "TEXT")
    private String fotos; // JSON array de URLs

    @Schema(description = "Coordenadas geográficas del avistamiento", example = "-31.4201,-64.1888")
    private String coordenada;

    @Schema(description = "Fecha en que ocurrió el avistamiento", example = "2025-11-14")
    private LocalDate fecha;

    @Schema(description = "Descripción detallada del avistamiento", example = "Vi a la mascota cerca del parque central")
    private String descripcion;

    @Schema(description = "Indica si el avistamiento está activo", example = "true")
    private boolean activo = true;

    public Avistamiento(Usuario usuario, Mascota mascota, AvistamientoRequest request) {
        this.usuario = usuario;
        this.mascota = mascota;
        this.coordenada = request.ubicacion();
        this.descripcion = request.descripcion();
        this.fecha = LocalDate.now();
    }

    public void actualizar(AvistamientoUpdateRequest request) {
        if (request.ubicacion() != null) this.coordenada = request.ubicacion();
        if (request.descripcion() != null) this.descripcion = request.descripcion();
    }

    public void desactivar() {
        this.activo = false;
    }
}
