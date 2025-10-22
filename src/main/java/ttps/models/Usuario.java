package ttps.models;

import jakarta.persistence.*;

import java.util.LinkedList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="usuario_id")
    private long id;
    private String nombre;
    private String apellido;
    private String email;
    private String contrasenia;
    private String telefono;
    private String barrio;
    private String ciudad;


    @OneToMany(mappedBy = "usuario")
    private List<Mascota> mascotas;

    @OneToMany(mappedBy = "usuario")
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
