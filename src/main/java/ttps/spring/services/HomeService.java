package ttps.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.models.avistamiento.AvistamientoRepository;
import ttps.spring.models.mascota.Estado;
import ttps.spring.models.mascota.MascotaRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class HomeService {

    private final MascotaRepository mascotaRepository;
    private final AvistamientoRepository avistamientoRepository;

    @Autowired
    public HomeService(MascotaRepository mascotaDAO, AvistamientoRepository avistamientoDAO) {
        this.mascotaRepository = mascotaDAO;
        this.avistamientoRepository = avistamientoDAO;
    }

    public Map<String, Integer> obtenerEstadisticas() {
        Map<String, Integer> estadisticas = new HashMap<>();
        estadisticas.put("mascotasPerdidas", contarMascotasPerdidas());
        estadisticas.put("recuperadas", contarMascotasRecuperadas());
        estadisticas.put("adoptadas", contarMascotasAdoptadas());
        estadisticas.put("avistamientosPendientes", contarAvistamientosPendientes());
        return estadisticas;
    }

    public int contarMascotasPerdidas() {
        return mascotaRepository.countByEstado(Estado.PERDIDO_PROPIO)
            + mascotaRepository.countByEstado(Estado.PERDIDO_AJENO);
    }

    public int contarMascotasRecuperadas() {
        return mascotaRepository.countByEstado(Estado.RECUPERADO);
    }

    public int contarMascotasAdoptadas() {
        return mascotaRepository.countByEstado(Estado.ADOPTADO);
    }

    public int contarAvistamientosPendientes() {
        return avistamientoRepository.countByActivoTrue();
    }
}
