package ttps.spring.models.mascota;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.util.Optional;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    List<Mascota> findMascotasPerdidas();

    List<Mascota> findByUsuarioId(Long usuarioId);


    Optional<Mascota> findByIdAndActivoTrue(Long id);
}
