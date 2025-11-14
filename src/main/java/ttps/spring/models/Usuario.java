package ttps.spring.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import ttps.utils.PasswordUtils;

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

    @Column(length = 60)
    private String email;

    @Column(length = 60)
    private String contrasenia;
    private String telefono;
    private String barrio;
    private String ciudad;

    private boolean activo = true;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    @JsonManagedReference("usuario-mascotas")
    private List<Mascota> mascotas;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    @JsonManagedReference("usuario-avistamientos")
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
        this.contrasenia = PasswordUtils.hashPassword(contrasenia);
        this.telefono = telefono;
        this.barrio = barrio;
        this.ciudad = ciudad;
        this.mascotas = new LinkedList<>();
        this.avistamientos = new LinkedList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    /**
     * Establece la contraseña del usuario, hasheándola automáticamente
     * @param contrasenia la contraseña en texto plano
     */
    public void setContrasenia(String contrasenia) {
        this.contrasenia = PasswordUtils.hashPassword(contrasenia);
    }

    /**
     * Verifica si una contraseña en texto plano coincide con la contraseña hasheada del usuario
     * @param plainPassword la contraseña en texto plano a verificar
     * @return true si la contraseña es correcta, false en caso contrario
     */
    public boolean verificarContrasenia(String plainPassword) {
        return PasswordUtils.verifyPassword(plainPassword, this.contrasenia);
    }

    public void agregarAvistamiento(Avistamiento avistamiento, Mascota mascota) {
        this.avistamientos.add(avistamiento);
        avistamiento.setUsuario(this);
        avistamiento.setMascota(mascota);
        mascota.agregarAvistamiento(avistamiento);
    }

    public void agregarMascota(Mascota mascota) {
        this.mascotas.add(mascota);
        mascota.setUsuario(this);
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public List<Mascota> getMascotas() {
        return mascotas;
    }

    public void setMascotas(List<Mascota> mascotas) {
        this.mascotas = mascotas;
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
}
