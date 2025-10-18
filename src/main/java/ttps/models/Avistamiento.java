package ttps.models;

import java.time.LocalDate;

public class Avistamiento {
    private int id;
    private Usuario usuario;
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
