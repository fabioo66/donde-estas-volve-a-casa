package ttps.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.models.avistamiento.AvistamientoRepository;
import ttps.spring.models.mascota.MascotaRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class HomeService {

    private final MascotaRepository mascotaRepository;
    private final AvistamientoRepository avistamientoRepository;

    @Autowired
    public HomeService(MascotaRepository mascotaRepository, AvistamientoRepository avistamientoRepository) {
        this.mascotaRepository = mascotaRepository;
        this.avistamientoRepository = avistamientoRepository;
    }

    public Map<String, Integer> obtenerEstadisticas() {
        Map<String, Integer> estadisticas = new HashMap<>();
        estadisticas.put("mascotasPerdidas", Math.toIntExact(contarMascotasPerdidas()));
        estadisticas.put("recuperadas", Math.toIntExact(contarMascotasRecuperadas()));
        estadisticas.put("adoptadas", Math.toIntExact(contarMascotasAdoptadas()));
        estadisticas.put("seguimientosPendientes", Math.toIntExact(contarSeguimientosPendientes()));
        return estadisticas;
    }

    public int contarMascotasPerdidas() {
        return Math.toIntExact(mascotaRepository.contarMascotasPerdidas());
    }

    public int contarMascotasRecuperadas() {
        return Math.toIntExact(mascotaRepository.contarMascotasRecuperadas());
    }

    public int contarMascotasAdoptadas() {
        return Math.toIntExact(mascotaRepository.contarMascotasAdoptadas());
    }

    public int contarSeguimientosPendientes() {
        // Contamos los avistamientos activos/pendientes de resolución
        return Math.toIntExact(avistamientoRepository.contarAvistamientosPendientes());
    }
}
