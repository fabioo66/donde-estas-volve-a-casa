package ttps.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.models.Avistamiento;
import ttps.spring.persistence.dao.interfaces.AvistamientoDAO;

import java.util.List;

@Service
@Transactional
public class AvistamientoService {

    private final AvistamientoDAO avistamientoDAO;

    @Autowired
    public AvistamientoService(AvistamientoDAO avistamientoDAO) {
        this.avistamientoDAO = avistamientoDAO;
    }

    public Avistamiento crearAvistamiento(Avistamiento avistamiento) {
        return avistamientoDAO.persist(avistamiento);
    }

    public Avistamiento obtenerAvistamiento(Long id) {
        return avistamientoDAO.get(id);
    }

    public List<Avistamiento> obtenerTodosLosAvistamientos() {
        return avistamientoDAO.getAll("fecha");
    }

    public Avistamiento actualizarAvistamiento(Avistamiento avistamiento) {
        return avistamientoDAO.update(avistamiento);
    }

    public void eliminarAvistamiento(Long id) {
        avistamientoDAO.delete(id);
    }

    public void eliminarAvistamiento(Avistamiento avistamiento) {
        avistamientoDAO.delete(avistamiento);
    }
}

