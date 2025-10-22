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


    public Avistamiento() {}

    public Avistamiento(int id, Usuario usuario, Mascota mascota, byte[] fotos, String coordenada, LocalDate fecha) {
        this.id = id;
        this.usuario = usuario;
        this.mascota = mascota;
        this.fotos = fotos;
        this.coordenada = coordenada;
        this.fecha = fecha;
    }
}
