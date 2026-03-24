package ttps.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.models.raza.DTO.RazaRequest;
import ttps.spring.models.raza.DTO.RazaResponse;
import ttps.spring.models.raza.Raza;
import ttps.spring.models.raza.RazaRepository;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RazaService {
    private final RazaRepository razaRepository;
    private final ttps.spring.models.tipo_mascota.Tipo_mascotaRepository tipoRepo;

    @Autowired
    public RazaService(RazaRepository razaRepository, ttps.spring.models.tipo_mascota.Tipo_mascotaRepository tipoRepo) {
        this.razaRepository = razaRepository;
        this.tipoRepo = tipoRepo;
    }

    // Normaliza: trim, eliminar diacríticos y uppercase
    private String normalize(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        String normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFKD);
        // eliminar marcas diacríticas
        String withoutDiacritics = normalized.replaceAll("\\p{M}", "");
        return withoutDiacritics.toUpperCase(Locale.ROOT);
    }

    @Transactional
    public RazaResponse crearRaza(RazaRequest request, Long tipoMascotaId) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(request.nombre());
        Objects.requireNonNull(tipoMascotaId);

        String nombre = request.nombre().trim();
        String nombreNormalizado = normalize(nombre);

        // validar existencia del tipo
        tipoRepo.findById(tipoMascotaId).orElseThrow(() -> new IllegalArgumentException("TipoMascota no existe: " + tipoMascotaId));

        // intentar reusar si existe
        Optional<Raza> existente = razaRepository.findByNombreNormalizadoAndTipoMascotaId(nombreNormalizado, tipoMascotaId);
        if (existente.isPresent()) {
            return RazaResponse.from(existente.get());
        }

        Raza raza = new Raza();
        raza.setNombre(nombre);
        raza.setNombreNormalizado(nombreNormalizado);
        raza.setTipoMascotaId(tipoMascotaId);

        try {
            Raza saved = razaRepository.save(raza);
            return RazaResponse.from(saved);
        } catch (DataIntegrityViolationException ex) {
            // probable condición de carrera: otro hilo creó la raza; re-consultar
            Optional<Raza> race = razaRepository.findByNombreNormalizadoAndTipoMascotaId(nombreNormalizado, tipoMascotaId);
            if (race.isPresent()) return RazaResponse.from(race.get());
            // si no está presente, rethrow
            throw ex;
        }
    }

    // Busca una raza por nombre (raw) y tipo. Devuelve Optional<Raza>
    public Optional<Raza> findByNombreAndTipoIdOptional(String nombreRaw, Long tipoMascotaId) {
        if (nombreRaw == null || tipoMascotaId == null) return Optional.empty();
        String nombreNormalizado = normalize(nombreRaw);
        return razaRepository.findByNombreNormalizadoAndTipoMascotaId(nombreNormalizado, tipoMascotaId);
    }

    // Método find-or-create seguro para uso por MascotaService
    @Transactional
    public Raza findOrCreateByNombreAndTipoId(String nombreRaw, Long tipoMascotaId) {
        if (nombreRaw == null) throw new IllegalArgumentException("Nombre de raza es requerido");
        Objects.requireNonNull(tipoMascotaId);

        String nombre = nombreRaw.trim();
        String nombreNormalizado = normalize(nombre);

        // validar tipo
        tipoRepo.findById(tipoMascotaId).orElseThrow(() -> new IllegalArgumentException("TipoMascota no existe: " + tipoMascotaId));

        Optional<Raza> existing = razaRepository.findByNombreNormalizadoAndTipoMascotaId(nombreNormalizado, tipoMascotaId);
        if (existing.isPresent()) return existing.get();

        Raza raza = new Raza();
        raza.setNombre(nombre);
        raza.setNombreNormalizado(nombreNormalizado);
        raza.setTipoMascotaId(tipoMascotaId);

        try {
            return razaRepository.save(raza);
        } catch (DataIntegrityViolationException ex) {
            // concurrency: otro proceso creó la misma raza
            Optional<Raza> race = razaRepository.findByNombreNormalizadoAndTipoMascotaId(nombreNormalizado, tipoMascotaId);
            if (race.isPresent()) return race.get();
            throw ex;
        }
    }

    // Respuestas DTO: listar por tipo
    @Transactional(readOnly = true)
    public List<RazaResponse> listByTipoId(Long tipoMascotaId) {
        return razaRepository.findAllByTipoMascotaId(tipoMascotaId)
                .stream()
                .map(RazaResponse::from)
                .collect(Collectors.toList());
    }

    // Buscar por id
    @Transactional(readOnly = true)
    public Optional<RazaResponse> getById(Long id) {
        return razaRepository.findById(id).map(RazaResponse::from);
    }

}
