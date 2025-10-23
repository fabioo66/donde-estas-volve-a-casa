package ttps.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "avistamiento")
public class Avistamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="avistamiento_id")
    private int id;

    @ManyToOne
    @JoinColumn(name="usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name="mascota_id")
    private Mascota mascota;
    private byte[] fotos;
    private String coordenada;
    private LocalDate fecha;

    private boolean activo = true;


    public Avistamiento() {}

    public Avistamiento(int id, Usuario usuario, Mascota mascota, byte[] fotos, String coordenada, LocalDate fecha) {
        this.id = id;
        this.usuario = usuario;
        this.mascota = mascota;
        this.fotos = fotos;
        this.coordenada = coordenada;
        this.fecha = fecha;
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

    public byte[] getFotos() {
        return fotos;
    }

    public void setFotos(byte[] fotos) {
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
}
