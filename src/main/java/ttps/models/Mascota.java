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

    public void setTamaño(Tamanio tamaño) {
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

    public List<byte[]> getFotos() {
        return fotos;
    }

    public void setFotos(List<byte[]> fotos) {
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

    public void setAvistamientos(List<Avistamiento> avistamientos) {
        this.avistamientos = avistamientos;
    }
}
