package ttps.models;

import java.util.LinkedList;
import java.util.List;

public class Usuario {
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String contrasenia;
    private String telefono;
    private String barrio;
    private String ciudad;
    private List<Mascota> mascotas;
    private List<Avistamiento> avistamientos;

    // Constructor vacío
    public Usuario() {
        this.mascotas = new LinkedList<>();
        this.avistamientos = new LinkedList<>();
    }

    public Usuario(String nombre, String apellido, String email, String contrasenia, String telefono, String barrio, String ciudad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasenia = contrasenia;
        this.telefono = telefono;
        this.barrio = barrio;
        this.ciudad = ciudad;
        this.mascotas = new LinkedList<>();
        this.avistamientos = new LinkedList<>();
    }
}
