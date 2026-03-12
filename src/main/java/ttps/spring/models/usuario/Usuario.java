package ttps.spring.models.usuario;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import ttps.spring.models.avistamiento.Avistamiento;
import ttps.spring.models.mascota.Mascota;
import ttps.utils.PasswordUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="usuario_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Setter
    private String nombre;

    @Setter
    private String apellido;

    @Setter @Column(unique = true, length = 60)
    private String email;

    @Setter @Column(unique = true, length = 50)
    private String nombreUsuario;

    @Column(length = 60)
    private String contrasenia;

    @Setter
    private String telefono;

    @Setter
    private String genero;

    @Setter
    private Integer edad;

    @Setter
    private String provincia;

    @Setter
    private String municipio;

    @Setter
    private String departamento;

    @Setter
    private boolean activo = true;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-mascotas")
    private List<Mascota> mascotas = new LinkedList<>();

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-avistamientos")
    private List<Avistamiento> avistamientos = new LinkedList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.rol.name()));
    }

    @Override
    public String getPassword() {
        return this.contrasenia;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return this.activo;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }

    public void cambiarContrasenia(String contraseniaHasheada) {
        this.contrasenia = contraseniaHasheada;
    }

    public void agregarAvistamiento(Avistamiento avistamiento, Mascota mascota) {
        this.avistamientos.add(avistamiento);
        mascota.agregarAvistamiento(avistamiento);
    }

    public void agregarMascota(Mascota mascota) {
        mascota.setUsuario(this);
        this.mascotas.add(mascota);
    }

    public Usuario(String nombre, String apellido, String email, String nombreUsuario, String contraseniaHasheada,
                   String telefono, String genero, Integer edad, String provincia, String municipio, String departamento) {
        this.rol = Rol.USUARIO;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.nombreUsuario = nombreUsuario;
        this.contrasenia = contraseniaHasheada;
        this.telefono = telefono;
        this.genero = genero;
        this.edad = edad;
        this.provincia = provincia;
        this.municipio = municipio;
        this.departamento = departamento;
    }
}
