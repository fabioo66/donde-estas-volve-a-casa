package ttps.spring.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.models.avistamiento.dto.AvistamientoRequest;
import ttps.spring.models.avistamiento.dto.AvistamientoResponse;
import ttps.spring.infra.ArchivoException;
import ttps.spring.infra.RecursoNoEncontradoException;
import ttps.spring.models.avistamiento.Avistamiento;
import ttps.spring.models.avistamiento.AvistamientoRepository;
import ttps.spring.models.avistamiento.dto.AvistamientoUpdateRequest;
import ttps.spring.models.mascota.Mascota;
import ttps.spring.models.mascota.MascotaRepository;
import ttps.spring.models.usuario.Usuario;
import ttps.spring.models.usuario.UsuarioRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Service
public class AvistamientoService {

    private final AvistamientoRepository avistamientoRepository;
    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    @Transactional
    public AvistamientoResponse crearAvistamiento(AvistamientoRequest request) {

        Usuario usuario = buscarUsuarioActivoPorId(request.usuarioId());
        Mascota mascota = buscarMascotaActivaPorId(request.mascotaId());

        Avistamiento avistamiento = new Avistamiento(usuario, mascota, request);

        if (request.fotosBase64() != null && !request.fotosBase64().isEmpty()) {
            try {
                List<String> fotosUrls = fileStorageService.saveImagesFromBase64(
                        request.fotosBase64(),
                        "avistamiento_" + System.currentTimeMillis()
                );
                avistamiento.setFotos(objectMapper.writeValueAsString(fotosUrls));
            } catch (Exception e) {
                throw new ArchivoException("Error al guardar las fotos del avistamiento", e);
            }
        }

        return AvistamientoResponse.from(avistamientoRepository.save(avistamiento));
    }

    public AvistamientoResponse obtenerAvistamiento(Long id) {
        return AvistamientoResponse.from(buscarAvistamientoActivoPorId(id));
    }

    public Page<AvistamientoResponse> obtenerTodosLosAvistamientos(Pageable pageable) {
        return avistamientoRepository.findByActivoTrue(pageable)
                .map(AvistamientoResponse::from);
    }

    @Transactional
    public AvistamientoResponse actualizarAvistamiento(Long id, AvistamientoUpdateRequest request) {
        Avistamiento avistamiento = buscarAvistamientoActivoPorId(id);

        avistamiento.actualizar(request);

        if (request.fotosBase64() != null && !request.fotosBase64().isEmpty()) {
            try {
                if (avistamiento.getFotos() != null && !avistamiento.getFotos().isEmpty()) {
                    List<String> oldUrls = objectMapper.readValue(avistamiento.getFotos(), new TypeReference<>() {});
                    fileStorageService.deleteFiles(oldUrls);
                }
                List<String> fotosUrls = fileStorageService.saveImagesFromBase64(
                        request.fotosBase64(),
                        "avistamiento_" + id + "_" + System.currentTimeMillis()
                );
                avistamiento.setFotos(objectMapper.writeValueAsString(fotosUrls));
            } catch (Exception e) {
                throw new ArchivoException("Error al actualizar las fotos del avistamiento", e);
            }
        }

        return AvistamientoResponse.from(avistamientoRepository.save(avistamiento));
    }

    @Transactional
    public void eliminarAvistamiento(Long id) {
        Avistamiento avistamiento = buscarAvistamientoActivoPorId(id);

        if (avistamiento.getFotos() != null && !avistamiento.getFotos().isEmpty()) {
            try {
                List<String> fotosUrls = objectMapper.readValue(avistamiento.getFotos(), new TypeReference<>() {});
                fileStorageService.deleteFiles(fotosUrls);
            } catch (Exception e) {
                throw new ArchivoException("Error al eliminar las fotos del avistamiento", e);
            }
        }

        avistamiento.desactivar();
    }

    @Transactional(readOnly = true)
    public Page<AvistamientoResponse> obtenerAvistamientosPorMascota(Long mascotaId, Pageable pageable) {
        buscarMascotaActivaPorId(mascotaId);
        return avistamientoRepository.findByMascotaIdAndActivoTrue(mascotaId, pageable)
                .map(AvistamientoResponse::from);
    }


    private Avistamiento buscarAvistamientoActivoPorId(Long id) {
        return avistamientoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Avistamiento no encontrado con id: " + id));
    }

    private Mascota buscarMascotaActivaPorId(Long id) {
        return mascotaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada con id: " + id));
    }

    private Usuario buscarUsuarioActivoPorId(Long id) {
        return usuarioRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + id));
    }
}
