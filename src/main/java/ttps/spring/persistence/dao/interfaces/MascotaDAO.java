package ttps.spring.persistence.dao.interfaces;

import ttps.spring.models.Mascota;
import ttps.spring.persistence.dao.interfaces.generic.GenericDAO;

import java.util.List;

public interface MascotaDAO extends GenericDAO<Mascota> {
    // Buscar mascotas activas por id de usuario
    List<Mascota> findByUsuarioActivas(Long usuarioId);

    // Buscar TODAS las mascotas por id de usuario (activas e inactivas)
    List<Mascota> findByUsuario(Long usuarioId);

    // Buscar mascotas perdidas (según enum Estado: PERDIDO_PROPIO o PERDIDO_AJENO)
    List<Mascota> findMascotasPerdidas();
}
