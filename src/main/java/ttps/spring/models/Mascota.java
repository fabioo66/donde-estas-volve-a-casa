package ttps.spring.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mascota")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="mascota_id")
    private int id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private Tamanio tamaño;

    private String color;
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Column(columnDefinition = "TEXT")
    private String fotos; // JSON array de URLs
    private String coordenadas;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name="usuario_id")
    @JsonBackReference("usuario-mascotas")
    private Usuario usuario;

    private String tipo;
    private String raza;

    private boolean activo = true;

    @OneToMany(mappedBy = "mascota", fetch = FetchType.EAGER)
    @JsonManagedReference("mascota-avistamientos")
    private List<Avistamiento>  avistamientos;

    // Constructor vacío
    public Mascota() {
        this.avistamientos = new ArrayList<>();
    }

    public Mascota(int id, String nombre, Tamanio tamaño, String color, LocalDate fecha, Estado estado, String fotos, String coordenadas, String descripcion, Usuario usuario, String tipo, String raza) {
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
        this.avistamientos = new ArrayList<>();
        this.tipo = tipo;
        this.raza = raza;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Tamanio getTamaño() {
        return tamaño;
    }

    public void setTamanio(Tamanio tamaño) {
        this.tamaño = tamaño;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getFotos() {
        return fotos;
    }

    public void setFotos(String fotos) {
        this.fotos = fotos;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public List<Avistamiento> getAvistamientos() {
        return avistamientos;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setAvistamientos(List<Avistamiento> avistamientos) {
        this.avistamientos = avistamientos;
    }

    public void agregarAvistamiento(Avistamiento avistamiento) {
        this.avistamientos.add(avistamiento);
    }
}
