package ttps.spring.persistence.dao.interfaces;

import ttps.spring.models.Avistamiento;
import ttps.spring.persistence.dao.interfaces.generic.GenericDAO;

import java.util.List;

public interface AvistamientoDAO extends GenericDAO<Avistamiento> {
    // Buscar avistamientos por mascota
    List<Avistamiento> findByMascotaId(Long mascotaId);

    // Contar avistamientos pendientes/activos para estadísticas
    int contarAvistamientosPendientes();

    // Obtener solo avistamientos activos
    List<Avistamiento> findAvistamientosActivos();
}
