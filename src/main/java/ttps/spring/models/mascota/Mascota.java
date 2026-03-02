package ttps.spring.models.mascota;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ttps.spring.models.avistamiento.Avistamiento;
import ttps.spring.models.usuario.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
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
    private String tipo;

    @Setter
    private String raza;

    @Setter
    private boolean activo = true;

    @OneToMany(mappedBy = "mascota", fetch = FetchType.EAGER)
    @JsonManagedReference("mascota-avistamientos")
    private List<Avistamiento>  avistamientos;

    public Mascota(Long id, String nombre, Tamanio tamanio, String color, LocalDate fecha, Estado estado, String fotos, String coordenadas, String descripcion, Usuario usuario, String tipo, String raza) {
        this.id = id;
        this.nombre = nombre;
        this.tamanio = tamanio;
        this.color = color;
        this.fecha = fecha;
        this.estado = estado;
        this.fotos = fotos;
        this.coordenadas = coordenadas;
        this.descripcion = descripcion;
        this.usuario = usuario;
        this.avistamientos = new ArrayList<>();
        this.tipo = tipo;
        this.raza = raza;
    }

    public void agregarAvistamiento(Avistamiento avistamiento) {
        this.avistamientos.add(avistamiento);
    }
}
