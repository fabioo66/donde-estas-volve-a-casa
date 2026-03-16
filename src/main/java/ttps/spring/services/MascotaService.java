package ttps.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.persistence.dao.interfaces.MascotaDAO;
import ttps.spring.models.Estado;
import ttps.spring.models.Mascota;

import java.util.List;

@Service
@Transactional
public class MascotaService {

    private final MascotaDAO mascotaDAO;
    private final AvistamientoService avistamientoService;

    @Autowired
    public MascotaService(MascotaDAO mascotaDAO, AvistamientoService avistamientoService) {
        this.mascotaDAO = mascotaDAO;
        this.avistamientoService = avistamientoService;
    }

    public Mascota crearMascota(Mascota mascota) {
        return mascotaDAO.persist(mascota);
    }

    public Mascota obtenerMascota(Long id) {
        return mascotaDAO.get(id);
    }

    public Mascota actualizarMascota(Mascota mascota) {
        // Obtener el estado anterior antes de actualizar
        Mascota mascotaAnterior = mascotaDAO.get(Long.valueOf(mascota.getId()));
        Estado estadoAnterior = mascotaAnterior != null ? mascotaAnterior.getEstado() : null;

        // Actualizar la mascota
        Mascota mascotaActualizada = mascotaDAO.update(mascota);

        // Si cambió el estado a RECUPERADO, eliminar todos los avistamientos activos
        if (mascota.getEstado() == Estado.RECUPERADO &&
            estadoAnterior != Estado.RECUPERADO) {
            avistamientoService.eliminarTodosLosAvistamientosDeMascota(Long.valueOf(mascota.getId()));
        }

        return mascotaActualizada;
    }

    public void eliminarMascota(Long id) {
        mascotaDAO.delete(id);
    }

    public void eliminarMascota(Mascota mascota) {
        mascotaDAO.delete(mascota);
    }

    public List<Mascota> obtenerMascotasPorUsuario(Long usuarioId) {
        return mascotaDAO.findByUsuario(usuarioId);
    }

    public List<Mascota> obtenerMascotasPerdidas() {
        // Retorna mascotas con estado PERDIDO_PROPIO o PERDIDO_AJENO
        return mascotaDAO.findMascotasPerdidas();
    }

    public List<Mascota> obtenerTodasLasMascotas() {
        return mascotaDAO.getAll("nombre");
    }
}
