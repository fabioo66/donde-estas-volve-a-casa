package ttps.spring.models.tipo_mascota;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface Tipo_mascotaRepository extends JpaRepository<Tipo_mascota, Long> {
    Optional<Tipo_mascota> findByNombreIgnoreCase(String nombre);
}

