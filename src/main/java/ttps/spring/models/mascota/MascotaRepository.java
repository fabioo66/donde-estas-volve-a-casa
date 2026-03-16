package ttps.spring.models.mascota;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    Optional<Mascota> findByIdAndActivoTrue(Long id);
}
