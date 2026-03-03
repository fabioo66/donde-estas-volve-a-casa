package ttps.spring.models.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByIdAndActivoTrue(Long id);

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    void deleteByNombreUsuario(String nombreUsuario);

    boolean existsByTelefono(String telefono);

    List<Usuario> findAllByActivoTrue();

    boolean existsByNombreUsuario(String nombreUsuario);
}
