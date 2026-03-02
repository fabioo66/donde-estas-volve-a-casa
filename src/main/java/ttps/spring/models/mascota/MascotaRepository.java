package ttps.spring.models.mascota;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    //crear metodo para obtener las mascotas perdidas del usuario findMascotasPerdidas
}
