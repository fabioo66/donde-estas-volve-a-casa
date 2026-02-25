package ttps.spring.models.usuario;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ttps.spring.models.usuario.dto.RegistroUsuarioRequest;
import ttps.spring.models.avistamiento.Avistamiento;
import ttps.spring.models.mascota.Mascota;
import ttps.utils.PasswordUtils;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@Entity
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

    @Column(unique = true, length = 50)
    private String nombreUsuario;

    @Column(length = 60)
    private String contrasenia;
    private String telefono;

    private String genero;
    private Integer edad;

    private String provincia;
    private String municipio;
    private String departamento;

    @Setter
    private boolean activo = true;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    @JsonManagedReference("usuario-mascotas")
    private List<Mascota> mascotas;

    @Setter
    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    @JsonManagedReference("usuario-avistamientos")
    private List<Avistamiento> avistamientos;

    public Usuario(String nombreUsuario, String nombre, String apellido, String email, String contrasenia,
                   String telefono, String genero, Integer edad, String provincia, String municipio, String departamento) {
        this.nombreUsuario = nombreUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasenia = PasswordUtils.hashPassword(contrasenia);
        this.telefono = telefono;
        this.genero = genero;
        this.edad = edad;
        this.provincia = provincia;
        this.municipio = municipio;
        this.departamento = departamento;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    public void setAvistamientos(List<Avistamiento> avistamientos) {
        this.avistamientos = avistamientos;
    }

    public Usuario(RegistroUsuarioRequest request) {
        this.nombreUsuario = request.nombreUsuario();
        this.nombre = request.nombre();
        this.apellido = request.apellido();
        this.email = request.email();
        this.contrasenia = PasswordUtils.hashPassword(request.password());
        this.telefono = request.telefono();
        this.genero = request.genero();
        this.edad = request.edad();
        this.provincia = request.provincia();
        this.municipio = request.municipio();
        this.departamento = request.departamento();
    }
}
