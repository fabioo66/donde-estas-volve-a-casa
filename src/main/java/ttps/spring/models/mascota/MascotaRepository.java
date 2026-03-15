package ttps.spring.models.mascota;

import org.springframework.data.jpa.repository.JpaRepository;
import ttps.spring.models.mascota.dto.MascotaResponse;

import java.util.List;

import java.util.Optional;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    List<MascotaResponse> findMascotasPerdidas();

    List<MascotaResponse> findByUsuarioId(Long usuarioId);


    Optional<Mascota> findByIdAndActivoTrue(Long id);

    MascotaResponse findByMascotaId(Long id);

    Mascota getReferenceById();
}
