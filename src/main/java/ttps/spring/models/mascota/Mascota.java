package ttps.spring.models.mascota;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ttps.spring.models.avistamiento.Avistamiento;
import ttps.spring.models.mascota.dto.MascotaRequest;
import ttps.spring.models.raza.Raza;
import ttps.spring.models.tipo_mascota.Tipo_mascota;
import ttps.spring.models.usuario.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mascota")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="mascota_id")
    private Long id;

    @Setter
    private String nombre;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "tamaño")
    private Tamanio tamanio;

    @Setter
    private String color;

    @Setter
    private LocalDate fecha;

    @Setter
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String fotos; // JSON array de URLs

    @Setter
    private String coordenadas;

    @Setter
    private String descripcion;

    @Setter
    @ManyToOne
    @JoinColumn(name="usuario_id")
    @JsonBackReference("usuario-mascotas")
    private Usuario usuario;

    @Setter
    @ManyToOne
    private Tipo_mascota tipo_mascota;

    @Setter
    @ManyToOne
    private Raza raza;

    @Setter
    private boolean activo = true;

    @OneToMany(mappedBy = "mascota", fetch = FetchType.EAGER)
    @JsonManagedReference("mascota-avistamientos")
    private List<Avistamiento>  avistamientos;

    public Mascota(MascotaRequest request, Usuario usuario, String fotosJson) {
        this.id = request.id();
        this.nombre = request.nombre();
        this.tamanio = request.tamanio();
        this.color = request.color();
        this.fecha = request.fecha();
        this.estado = request.estado();
        this.fotos = fotosJson;
        this.coordenadas = request.coordenadas();
        this.descripcion = request.descripcion();
        this.usuario = usuario;
        this.avistamientos = new ArrayList<>();
        this.tipo_mascota = request.tipo_mascota();
        // La raza se asigna en el service (se resuelve a entidad Raza). No copiar directamente desde el request DTO.
        this.usuario = usuario;
    }

    public void agregarAvistamiento(Avistamiento avistamiento) {
        this.avistamientos.add(avistamiento);
    }

    // Compatibilidad: helper para acceder al tipo con el nombre corto getTipo()
    public Tipo_mascota getTipo() {
        return this.tipo_mascota;
    }
}
