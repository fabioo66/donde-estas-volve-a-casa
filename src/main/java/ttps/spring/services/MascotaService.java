package ttps.spring.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.infra.ArchivoException;
import ttps.spring.models.mascota.MascotaRepository;
import ttps.spring.models.mascota.Estado;
import ttps.spring.models.mascota.Mascota;
import ttps.spring.models.mascota.dto.MascotaInfo;
import ttps.spring.models.mascota.dto.MascotaRequest;
import ttps.spring.models.mascota.dto.MascotaResponse;
import ttps.spring.models.usuario.Usuario;
import ttps.spring.models.raza.DTO.RazaRef;
import ttps.utils.Georef_ar;


import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final AvistamientoService avistamientoService;
    private final UsuarioService usuarioService;
    private final ObjectMapper objectMapper;
    private final FileStorageService fileStorageService;
    private final Georef_ar georef_ar;
    private final ttps.spring.models.raza.RazaRepository razaRepository;
    private final RazaService razaService;

    @Autowired
    public MascotaService(MascotaRepository mascotaRepository, AvistamientoService avistamientoService, UsuarioService usuarioService, ObjectMapper objectMapper, FileStorageService fileStorageService, Georef_ar georef_ar, ttps.spring.models.raza.RazaRepository razaRepository, RazaService razaService) {
        this.mascotaRepository = mascotaRepository;
        this.avistamientoService = avistamientoService;
        this.usuarioService = usuarioService;
        this.objectMapper = objectMapper;
        this.fileStorageService = fileStorageService;
        this.georef_ar = georef_ar;
        this.razaRepository = razaRepository;
        this.razaService = razaService;
    }

    @Transactional
    public MascotaResponse crearMascota(MascotaRequest request, Long usuarioId) {

        Usuario usuario = usuarioService.obtenerUsuario(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado: " + usuarioId);
        }

        // Validar que se haya indicado el tipo de mascota (necesario para asociar/crear razas)
        if (request.tipo_mascota() == null || request.tipo_mascota().getId() == null) {
            throw new IllegalArgumentException("Tipo de mascota es requerido");
        }

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

        // Resolver la raza según reglas:
        // - si llega id -> usarla (validar que existe y pertenezca al tipo indicado)
        // - si no llega id -> debe venir nombre manual y se usará findOrCreate (RazaService se encarga de normalizar y eliminar diacríticos)
        ttps.spring.models.raza.Raza resolvedRaza = null;
        if (request.raza() == null) {
            throw new IllegalArgumentException("Raza es requerida");
        }

        RazaRef inputRaza = request.raza();
        Long inputRazaId = inputRaza == null ? null : inputRaza.id();
        String inputRazaNombre = inputRaza == null ? null : inputRaza.nombre();

        // Restricción: si el cliente envía una raza con id, NO puede enviar nombre manual
        if (inputRazaId != null && inputRazaNombre != null && !inputRazaNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Si envía razaId no debe enviar nombre de raza manual");
        }

        Long tipoId = request.tipo_mascota().getId();

        if (inputRazaId != null) {
            // validar existencia y pertenencia a tipo
            resolvedRaza = razaRepository.findById(inputRazaId)
                    .orElseThrow(() -> new IllegalArgumentException("Raza no encontrada: " + inputRazaId));
            if (resolvedRaza.getTipo_mascota() == null || !resolvedRaza.getTipo_mascota().getId().equals(tipoId)) {
                throw new IllegalArgumentException("La raza seleccionada no pertenece al tipo de mascota indicado");
            }
        } else {
            // nombre manual obligatorio cuando no hay id
            if (inputRazaNombre == null || inputRazaNombre.trim().isEmpty()) {
                throw new IllegalArgumentException("Debe indicar la raza: seleccionar una existente o nombre manual");
            }
            // Delegar a RazaService que normaliza (trim, elimina diacríticos, uppercase), valida tipo y maneja concurrencia
            resolvedRaza = razaService.findOrCreateByNombreAndTipoId(inputRazaNombre, tipoId);
        }

        Mascota mascota = new Mascota(request, usuario, fotosJson);
        mascota.setRaza(resolvedRaza);
        return MascotaResponse.from(mascotaRepository.save(mascota));
    }

    public MascotaInfo obtenerMascotaResponse(Long id) {
        Mascota m = mascotaRepository.getReferenceById(id);
        String municipio = null;
        String provincia = null;
        if (m.getCoordenadas() != null && !m.getCoordenadas().isEmpty()) {
            try {
                Map<String, String> datos = this.georef_ar.getDatos(m.getCoordenadas());
                municipio = datos.get("municipio");
                provincia = datos.get("provincia");
            } catch (Exception e) {
                municipio = "Desconocido";
                provincia = "Desconocido";
            }
        }
        return new MascotaInfo(
                m.getId(),
                m.getNombre(),
                m.getTipo_mascota(),
                m.getRaza(),
                m.getColor(),
                m.getTamanio() != null ? m.getTamanio().name() : null,
                m.getFotos(),
                municipio,
                provincia
        );
    }

    public Mascota obtenerMascota(Long id) {
        return mascotaRepository.getReferenceById(id);
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
        Mascota mascota = this.obtenerMascota(id);

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
        List<Mascota> mascotas = mascotaRepository.findByUsuarioIdAndActivoTrue(usuarioId);
        return mascotas.stream().map(MascotaResponse::from).toList();
    }

    public List<MascotaResponse> obtenerMascotasPerdidas() {
        // Retorna mascotas con estado PERDIDO_PROPIO o PERDIDO_AJENO
        List<Mascota> mascotas = mascotaRepository.findByEstadoInAndActivoTrue(
                java.util.Arrays.asList(Estado.PERDIDO_PROPIO, Estado.PERDIDO_AJENO)
        );
        return mascotas.stream().map(MascotaResponse::from).toList();
    }

    public List<Mascota> obtenerTodasLasMascotas() {
        return mascotaRepository.findAll();
    }
}
