package ttps.models;

import jakarta.persistence.*;

import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "mascota")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="mascota_id")
    private int id;

    private String nombre;
    private Tamanio tamaño;
    private String color;
    private LocalDate fecha;
    private Estado estado;
    private List<byte[]> fotos;
    private String coordenadas;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name="usuario_id")
    private Usuario usuario;

    private String tipo;
    private String raza;

    @OneToMany(mappedBy = "mascota")
    private List<Avistamiento>  avistamientos;

    // Constructor vacío
    public Mascota() {}

    public Mascota(int id, String nombre, Tamanio tamaño, String color, LocalDate fecha, Estado estado, List<byte[]> fotos, String coordenadas, String descripcion, Usuario usuario, String tipo, String raza) {
        this.id = id;
        this.nombre = nombre;
        this.tamaño = tamaño;
        this.color = color;
        this.fecha = fecha;
        this.estado = estado;
        this.fotos = fotos;
        this.coordenadas = coordenadas;
        this.descripcion = descripcion;
        this.usuario = usuario;
        this.tipo = tipo;
        this.raza = raza;
    }
}
