package ttps.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.models.tipo_mascota.Tipo_mascota;
import ttps.spring.models.tipo_mascota.Tipo_mascotaRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TipoMascotaService {

    private final Tipo_mascotaRepository tipoRepo;

    @Autowired
    public TipoMascotaService(Tipo_mascotaRepository tipoRepo) {
        this.tipoRepo = tipoRepo;
    }

    public Optional<Tipo_mascota> findById(Long id) {
        return tipoRepo.findById(id);
    }

    public List<Tipo_mascota> findAll() {
        return tipoRepo.findAll();
    }
}

