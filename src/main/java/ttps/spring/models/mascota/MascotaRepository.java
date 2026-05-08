package ttps.spring.models.mascota;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ttps.spring.models.mascota.dto.MascotaResponse;

import java.util.List;
import java.util.Optional;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    @Query("SELECT m FROM Mascota m WHERE m.estado = PERDIDO_PROPIO OR m.estado = PERDIDO_AJENO")
    List<MascotaResponse> findMascotasPerdidas();

    @Query("SELECT m FROM Mascota m WHERE m.usuario.id = :usuarioId")
    List<MascotaResponse> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    Optional<Mascota> findByIdAndActivoTrue(Long id);

    int countByEstado(Estado estado);
}
