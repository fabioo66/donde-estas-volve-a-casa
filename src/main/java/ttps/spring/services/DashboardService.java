package ttps.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.persistence.dao.interfaces.MascotaDAO;
import ttps.spring.persistence.dao.interfaces.AvistamientoDAO;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class DashboardService {

    private final MascotaDAO mascotaDAO;
    private final AvistamientoDAO avistamientoDAO;

    @Autowired
    public DashboardService(MascotaDAO mascotaDAO, AvistamientoDAO avistamientoDAO) {
        this.mascotaDAO = mascotaDAO;
        this.avistamientoDAO = avistamientoDAO;
    }

    public Map<String, Integer> obtenerEstadisticas() {
        Map<String, Integer> estadisticas = new HashMap<>();
        estadisticas.put("mascotasPerdidas", contarMascotasPerdidas());
        estadisticas.put("recuperadas", contarMascotasRecuperadas());
        estadisticas.put("adoptadas", contarMascotasAdoptadas());
        estadisticas.put("seguimientosPendientes", contarSeguimientosPendientes());
        return estadisticas;
    }

    public int contarMascotasPerdidas() {
        return mascotaDAO.contarMascotasPerdidas();
    }

    public int contarMascotasRecuperadas() {
        return mascotaDAO.contarMascotasRecuperadas();
    }

    public int contarMascotasAdoptadas() {
        return mascotaDAO.contarMascotasAdoptadas();
    }

    public int contarSeguimientosPendientes() {
        // Contamos los avistamientos activos/pendientes de resolución
        return avistamientoDAO.contarAvistamientosPendientes();
    }
}
