package ttps.spring.models.mascota;

import org.springframework.data.jpa.repository.JpaRepository;
import ttps.spring.models.mascota.dto.MascotaResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    // Devuelve las mascotas perdidas activas
    List<Mascota> findByEstadoInAndActivoTrue(List<Estado> estados);

    // Obtener mascotas activas por usuario
    List<Mascota> findByUsuarioIdAndActivoTrue(Long usuarioId);

    Optional<Mascota> findByIdAndActivoTrue(Long id);



    // Métodos derivados para conteos por estado (Spring Data genera la implementación automáticamente)
    long countByEstadoAndActivoTrue(Estado estado);
    long countByEstadoInAndActivoTrue(List<Estado> estados);


    default Long contarMascotasRecuperadas() {
        return countByEstadoAndActivoTrue(Estado.RECUPERADO);
    }

    default Long contarMascotasPerdidas() {
        return countByEstadoInAndActivoTrue(Arrays.asList(Estado.PERDIDO_PROPIO, Estado.PERDIDO_AJENO));
    }

    default Long contarMascotasAdoptadas() {
        return countByEstadoAndActivoTrue(Estado.ADOPTADO);
    }

}
