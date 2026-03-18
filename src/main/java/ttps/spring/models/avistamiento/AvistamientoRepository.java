package ttps.spring.models.avistamiento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvistamientoRepository extends JpaRepository<Avistamiento, Long> {

    Page<Avistamiento> findByActivoTrue(Pageable pageable);

    Page<Avistamiento> findByMascotaIdAndActivoTrue(Long mascotaId, Pageable pageable);

    Optional<Avistamiento> findByIdAndActivoTrue(Long id);

    // Método derivado estándar
    long countByActivoTrue();

    default Long contarAvistamientosPendientes() {
        return countByActivoTrue();
    }
}
