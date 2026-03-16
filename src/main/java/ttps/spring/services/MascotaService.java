package ttps.spring.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.infra.ArchivoException;
import ttps.spring.models.mascota.MascotaRepository;
import ttps.spring.models.mascota.Estado;
import ttps.spring.models.mascota.Mascota;
import ttps.spring.models.mascota.dto.MascotaRequest;
import ttps.spring.models.mascota.dto.MascotaResponse;
import ttps.spring.models.usuario.Usuario;
import ttps.spring.services.UsuarioService;

import java.awt.print.Pageable;
import java.util.List;

@Service
@Transactional
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final AvistamientoService avistamientoService;
    private final UsuarioService usuarioService;
    private final ObjectMapper objectMapper;
    private final FileStorageService fileStorageService;

    @Autowired
    public MascotaService(MascotaRepository mascotaRepository, AvistamientoService avistamientoService, UsuarioService usuarioService, ObjectMapper objectMapper, FileStorageService fileStorageService) {
        this.mascotaRepository = mascotaRepository;
        this.avistamientoService = avistamientoService;
        this.usuarioService = usuarioService;
        this.objectMapper = objectMapper;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public MascotaResponse crearMascota(MascotaRequest request, Long usuarioId) {

        Usuario usuario = usuarioService.obtenerUsuario((long) usuarioId);

        String fotosJson = "";
        if (request.fotosBase64() != null && !request.fotosBase64().isEmpty()) {
            try {
                List<String> fotosUrls = fileStorageService.saveImagesFromBase64(
                        request.fotosBase64(),
                        "mascota_" + System.currentTimeMillis()
                );
                fotosJson = objectMapper.writeValueAsString(fotosUrls);
            } catch (Exception e){
                throw new ArchivoException("Error al guardar las fotos de la mascota: ", e);
            }

        }

        Mascota mascota = new Mascota(request, usuario, fotosJson);
        return MascotaResponse.from(mascotaRepository.save(mascota));
    }

    public MascotaResponse obtenerMascotaResponse(Long id) {
       return MascotaResponse.from(mascotaRepository.getReferenceById(id));
    }

    public Mascota obtenerMascota(Long id) {
        return mascotaRepository.getReferenceById();
    }

    @Transactional
    public MascotaResponse actualizarMascota(MascotaRequest request, Long id) {
        Mascota mascota = this.obtenerMascota(id);

        // Obtener el estado anterior antes de actualizar
        Estado estadoAnterior = mascota != null ? mascota.getEstado() : null;


        if (request.fotosBase64() != null && !request.fotosBase64().isEmpty()) {
            // Eliminar fotos antiguas
            if (mascota.getFotos() != null && !mascota.getFotos().isEmpty()) {
                try {
                    List<String> oldUrls = objectMapper.readValue(mascota.getFotos(), new TypeReference<List<String>>() {
                    });
                    fileStorageService.deleteFiles(oldUrls);
                } catch (Exception e) {
                    // Log error pero continuar
                    System.err.println("Error eliminando fotos antiguas: " + e.getMessage());
                }
            }
        }

        //Guardar nuevas fotos
        try {
            List<String> fotosUrls = fileStorageService.saveImagesFromBase64(
                    request.fotosBase64(),
                    "mascota_" + System.currentTimeMillis()
            );
            mascota.setFotos(objectMapper.writeValueAsString(fotosUrls));
        } catch (Exception e){
            throw new ArchivoException("Error al guardar las fotos de la mascota: ", e);
        }

        // Actualizar la mascota
        MascotaResponse mascotaActualizada = MascotaResponse.from(mascotaRepository.save(mascota));

        // Si cambió el estado a RECUPERADO, eliminar todos los avistamientos activos
        if (mascotaActualizada.estado() == Estado.RECUPERADO &&
                estadoAnterior != Estado.RECUPERADO) {
            avistamientoService.eliminarTodosLosAvistamientosDeMascota(mascotaActualizada.id());
        }
        return mascotaActualizada;
    }

    @Transactional
    public void eliminarMascota(Long id) {
        Mascota mascota = this.obtenerMascota((long) id);

        // Eliminar archivos de fotos
        if (mascota.getFotos() != null && !mascota.getFotos().isEmpty()) {
            try {
                List<String> fotosUrls = objectMapper.readValue(mascota.getFotos(), new TypeReference<List<String>>() {});
                fileStorageService.deleteFiles(fotosUrls);
            } catch (Exception e) {
                System.err.println("Error eliminando fotos: " + e.getMessage());
            }
        }

        // Borrado logico
        mascota.setActivo(false);
        mascotaRepository.deleteById(id);
    }

    public void eliminarMascota(Mascota mascota) {
        mascotaRepository.delete(mascota);
    }

    public List<MascotaResponse> obtenerMascotasPorUsuario(Long usuarioId) {
        return mascotaRepository.findByUsuarioId(usuarioId);
    }

    public List<MascotaResponse> obtenerMascotasPerdidas() {
        // Retorna mascotas con estado PERDIDO_PROPIO o PERDIDO_AJENO
        return mascotaRepository.findMascotasPerdidas();
    }

    public List<Mascota> obtenerTodasLasMascotas() {
        return mascotaRepository.findAll();
    }
}
