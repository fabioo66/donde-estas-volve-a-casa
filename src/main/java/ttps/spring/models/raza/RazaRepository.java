package ttps.spring.models.raza;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface RazaRepository extends JpaRepository<Raza, Long> {

    @Query("SELECT r FROM Raza r WHERE r.nombreNormalizado = :nombreNormalizado AND r.tipo_mascota.id = :tipoId")
    Optional<Raza> findByNombreNormalizadoAndTipoMascotaId(@Param("nombreNormalizado") String nombreNormalizado, @Param("tipoId") Long tipoMascotaId);

    @Query("SELECT r FROM Raza r WHERE r.tipo_mascota.id = :tipoId")
    List<Raza> findAllByTipoMascotaId(@Param("tipoId") Long tipoMascotaId);

}
