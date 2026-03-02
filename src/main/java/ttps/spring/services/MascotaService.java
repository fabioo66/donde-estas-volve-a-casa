package ttps.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.models.mascota.MascotaRepository;
import ttps.spring.models.mascota.Estado;
import ttps.spring.models.mascota.Mascota;

import java.util.List;

@Service
@Transactional
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final AvistamientoService avistamientoService;

    @Autowired
    public MascotaService(MascotaRepository mascotaRepository, AvistamientoService avistamientoService) {
        this.mascotaRepository = mascotaRepository;
        this.avistamientoService = avistamientoService;
    }

    public Mascota crearMascota(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    public Mascota obtenerMascota(Long id) {
        return mascotaRepository.getReferenceById(id);
    }

    public Mascota actualizarMascota(Mascota mascota) {
        // Obtener el estado anterior antes de actualizar
        Mascota mascotaAnterior = mascotaRepository.getById(Long.valueOf(mascota.getId()));
        Estado estadoAnterior = mascotaAnterior != null ? mascotaAnterior.getEstado() : null;

        // Actualizar la mascota
        Mascota mascotaActualizada = mascotaRepository.save(mascota);

        // Si cambió el estado a RECUPERADO, eliminar todos los avistamientos activos
        if (mascota.getEstado() == Estado.RECUPERADO &&
            estadoAnterior != Estado.RECUPERADO) {
            avistamientoService.eliminarTodosLosAvistamientosDeMascota(Long.valueOf(mascota.getId()));
        }

        return mascotaActualizada;
    }

    public void eliminarMascota(Long id) {
        mascotaRepository.deleteById(id);
    }

    public void eliminarMascota(Mascota mascota) {
        mascotaRepository.delete(mascota);
    }

    public List<Mascota> obtenerMascotasPorUsuario(Long usuarioId) {
        return mascotaRepository.findByUsuarioId(usuarioId);
    }

    public List<Mascota> obtenerMascotasPerdidas() {
        // Retorna mascotas con estado PERDIDO_PROPIO o PERDIDO_AJENO
        return mascotaRepository.findMascotasPerdidas();
    }

    public List<Mascota> obtenerTodasLasMascotas() {
        return mascotaRepository.findAll();
    }
}
