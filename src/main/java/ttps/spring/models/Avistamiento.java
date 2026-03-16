package ttps.spring.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "avistamiento")
@Schema(description = "Representa un avistamiento de una mascota perdida")
public class Avistamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="avistamiento_id")
    @Schema(description = "ID único del avistamiento", example = "1")
    private int id;

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


    public Avistamiento() {}

    public Avistamiento(int id, Usuario usuario, Mascota mascota, String fotos, String coordenada, LocalDate fecha, String descripcion) {
        this.id = id;
        this.usuario = usuario;
        this.mascota = mascota;
        this.fotos = fotos;
        this.coordenada = coordenada;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

    public String getFotos() {
        return fotos;
    }

    public void setFotos(String fotos) {
        this.fotos = fotos;
    }

    public String getCoordenada() {
        return coordenada;
    }

    public void setCoordenada(String coordenada) {
        this.coordenada = coordenada;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
