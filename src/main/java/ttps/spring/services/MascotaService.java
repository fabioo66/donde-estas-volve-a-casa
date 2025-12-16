package ttps.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.persistence.dao.interfaces.MascotaDAO;
import ttps.spring.models.Mascota;

import java.util.List;

@Service
@Transactional
public class MascotaService {

    private final MascotaDAO mascotaDAO;

    @Autowired
    public MascotaService(MascotaDAO mascotaDAO) {
        this.mascotaDAO = mascotaDAO;
    }

    public Mascota crearMascota(Mascota mascota) {
        return mascotaDAO.persist(mascota);
    }

    public Mascota obtenerMascota(Long id) {
        return mascotaDAO.get(id);
    }

    public Mascota actualizarMascota(Mascota mascota) {
        return mascotaDAO.update(mascota);
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
