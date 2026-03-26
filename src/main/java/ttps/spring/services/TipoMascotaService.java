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
import java.text.Normalizer;
import java.util.Locale;

@Service
@Transactional
public class TipoMascotaService {

    private final Tipo_mascotaRepository tipoRepo;

    @Autowired
    public TipoMascotaService(Tipo_mascotaRepository tipoRepo) {
        this.tipoRepo = tipoRepo;
    }



    public Tipo_mascotaResponse createTipoMascota(String nombre) {
        String nombreNorm = normalize(nombre);
        Tipo_mascota tipo_mascota = new Tipo_mascota(nombreNorm);
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
        Tipo_mascota tipo = tipoRepo.findById(id).orElseThrow(() -> new ttps.spring.infra.RecursoNoEncontradoException("TipoMascota no encontrado: " + id));
        tipo.setNombre(normalize(nuevoNombre));
        return Tipo_mascotaResponse.from(tipoRepo.save(tipo));
    }

    // Normaliza nombre: trim, eliminar diacríticos y uppercase
    private String normalize(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        String normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFKD);
        String withoutDiacritics = normalized.replaceAll("\\p{M}", "");
        return withoutDiacritics.toUpperCase(Locale.ROOT);
    }

    public Optional<Tipo_mascota> findById(Long id) {
        return tipoRepo.findById(id);
    }
}

