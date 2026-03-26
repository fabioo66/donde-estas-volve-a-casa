package ttps.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.models.tipo_mascota.DTO.Tipo_mascotaResponse;
import ttps.spring.models.tipo_mascota.Tipo_mascota;
import ttps.spring.models.tipo_mascota.Tipo_mascotaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TipoMascotaService {

    private final Tipo_mascotaRepository tipoRepo;

    @Autowired
    public TipoMascotaService(Tipo_mascotaRepository tipoRepo) {
        this.tipoRepo = tipoRepo;
    }



    public Tipo_mascotaResponse createTipoMascota(String nombre) {
        Tipo_mascota tipo_mascota = new Tipo_mascota(nombre);
        return Tipo_mascotaResponse.from(tipoRepo.save(tipo_mascota));
    }


    public List<Tipo_mascotaResponse> findAll() {
        return tipoRepo.findAll()
                .stream()
                .map(Tipo_mascotaResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        tipoRepo.deleteById(id);
    }

    public Tipo_mascotaResponse updateNombreById(Long id, String nuevoNombre) {
        Tipo_mascota tipo = tipoRepo.getOne(id);
        tipo.setNombre(nuevoNombre);
        return Tipo_mascotaResponse.from(tipoRepo.save(tipo));
    }

    public Optional<Tipo_mascota> findById(Long id) {
        return tipoRepo.findById(id);
    }
}

