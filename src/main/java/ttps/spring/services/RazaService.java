package ttps.spring.services;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.models.raza.DTO.RazaRequest;
import ttps.spring.models.raza.DTO.RazaResponse;
import ttps.spring.models.raza.Raza;
import ttps.spring.models.raza.RazaRepository;
import ttps.spring.models.tipo_mascota.Tipo_mascota;
import ttps.spring.models.tipo_mascota.Tipo_mascotaRepository;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import ttps.spring.infra.ValidacionException;
import ttps.spring.infra.RecursoNoEncontradoException;

@Service
@Transactional
public class RazaService {
    private final RazaRepository razaRepository;
    private final Tipo_mascotaRepository tipoRepo;

    @Autowired
    public RazaService(RazaRepository razaRepository, Tipo_mascotaRepository tipoRepo) {
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

    // Método find-or-create seguro para uso por MascotaService
    @Transactional
    public Raza findOrCreateByNombreAndTipoId(String nombreRaw, Long tipoMascotaId) {
        if (nombreRaw == null) throw new ValidacionException("Nombre de raza es requerido");
        if (tipoMascotaId == null) throw new ValidacionException("Tipo de mascota es requerido");

        String nombre = nombreRaw.trim();
        String nombreNormalizado = normalize(nombre);

        if (nombreNormalizado == null || nombreNormalizado.isEmpty()) {
            throw new ValidacionException("Nombre de raza inválido después de normalizar");
        }

        // validar tipo
        Tipo_mascota tipo_mascota = tipoRepo.findById(tipoMascotaId).orElseThrow(() -> new RecursoNoEncontradoException("TipoMascota no encontrado: " + tipoMascotaId));

        Optional<Raza> existing = razaRepository.findByNombreNormalizadoAndTipoMascotaId(nombreNormalizado, tipoMascotaId);
        if (existing.isPresent()) return existing.get();

        Raza raza = new Raza(nombre, nombreNormalizado, tipo_mascota);


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
    public RazaResponse getById(Long id) {
        return RazaResponse.from(razaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Raza no encontrada: " + id)));
    }

    public RazaResponse updateRaza(@Valid Long razaId, RazaRequest razaRequest) {
        // obtener la entidad existente
        Raza raza = razaRepository.findById(razaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Raza no encontrada: " + razaId));

        // si se solicita cambio de tipo, validar existencia del tipo
        if (razaRequest.tipoMascotaId() != null) {
            tipoRepo.findById(razaRequest.tipoMascotaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("TipoMascota no encontrado: " + razaRequest.tipoMascotaId()));
            raza.setTipoMascotaId(razaRequest.tipoMascotaId());
        }

        // si se proporciona nuevo nombre, normalizar y comprobar unicidad
        if (razaRequest.nombre() != null && !razaRequest.nombre().trim().isEmpty()) {
            String nombreRaw = razaRequest.nombre().trim();
            String nombreNormalizado = normalize(nombreRaw);

            if (nombreNormalizado == null || nombreNormalizado.isEmpty()) {
                throw new ValidacionException("Nombre de raza inválido después de normalizar");
            }

            Long tipoIdParaCheck = (razaRequest.tipoMascotaId() != null) ? razaRequest.tipoMascotaId() : raza.getTipoMascotaId();

            Optional<Raza> conflicto = razaRepository.findByNombreNormalizadoAndTipoMascotaId(nombreNormalizado, tipoIdParaCheck);
            if (conflicto.isPresent() && !Objects.equals(conflicto.get().getId(), razaId)) {
                throw new ValidacionException("Ya existe una raza con ese nombre para el tipo de mascota seleccionado.");
            }

            raza.setNombre(nombreRaw);
            raza.setNombreNormalizado(nombreNormalizado);
        }

        try {
            Raza saved = razaRepository.save(raza);
            return RazaResponse.from(saved);
        } catch (DataIntegrityViolationException ex) {
            // posible condición de carrera: re-consultar si ya existe la raza conflictiva
            String nombreNorm = (razaRequest.nombre() != null) ? normalize(razaRequest.nombre()) : normalize(raza.getNombre());
            Long tipoId = (razaRequest.tipoMascotaId() != null) ? razaRequest.tipoMascotaId() : raza.getTipoMascotaId();
            Optional<Raza> existing = razaRepository.findByNombreNormalizadoAndTipoMascotaId(nombreNorm, tipoId);
            if (existing.isPresent()) return RazaResponse.from(existing.get());
            throw ex;
        }
    }

    public void deleteRaza(Long id) {
        razaRepository.deleteById(id);
    }
}
