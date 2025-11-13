package ttps.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.models.Mascota;
import ttps.spring.persistence.dao.interfaces.MascotaDAO;

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

    public List<Mascota> obtenerTodasLasMascotas() {
        return mascotaDAO.getAll("id");
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
}

