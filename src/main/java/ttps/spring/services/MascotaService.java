package ttps.spring.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.infra.ArchivoException;
import ttps.spring.infra.RecursoNoEncontradoException;
import ttps.spring.infra.ValidacionException;
import org.springframework.dao.DataIntegrityViolationException;
import ttps.spring.models.mascota.MascotaRepository;
import ttps.spring.models.mascota.Estado;
import ttps.spring.models.mascota.Mascota;
import ttps.spring.models.mascota.dto.MascotaInfo;
import ttps.spring.models.mascota.dto.MascotaRequest;
import ttps.spring.models.mascota.dto.MascotaResponse;
import ttps.spring.models.raza.RazaRepository;
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
    private final ttps.spring.models.raza.RazaRepository razaRepository;
    private final RazaService razaService;

    @Autowired
    public MascotaService(MascotaRepository mascotaRepository, AvistamientoService avistamientoService, UsuarioService usuarioService, ObjectMapper objectMapper, FileStorageService fileStorageService, RazaRepository razaRepository, RazaService razaService) {
        this.mascotaRepository = mascotaRepository;
        this.avistamientoService = avistamientoService;
        this.usuarioService = usuarioService;
        this.objectMapper = objectMapper;
        this.fileStorageService = fileStorageService;
        this.razaRepository = razaRepository;
        this.razaService = razaService;
    }

    @Transactional
    public MascotaResponse crearMascota(MascotaRequest request, Long usuarioId) {

        Usuario usuario = usuarioService.obtenerUsuario(usuarioId);
        if (usuario == null) {
            throw new RecursoNoEncontradoException("Usuario no encontrado: " + usuarioId);
        }

        // Validar que se haya indicado el tipo de mascota (necesario para asociar/crear razas)
        if (request.tipo_mascota() == null || request.tipo_mascota().getId() == null) {
            throw new ValidacionException("Tipo de mascota es requerido");
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
        ttps.spring.models.raza.Raza resolvedRaza;
        if (request.raza() == null) {
            throw new ValidacionException("Raza es requerida");
        }

        RazaRef inputRaza = request.raza();
        Long inputRazaId = inputRaza.id();
        String inputRazaNombre = inputRaza.nombre();

        // Restricción: si el cliente envía una raza con id, NO puede enviar nombre manual
        if (inputRazaId != null && inputRazaNombre != null && !inputRazaNombre.trim().isEmpty()) {
            throw new ValidacionException("Si envía razaId no debe enviar nombre de raza manual");
        }

        Long tipoId = request.tipo_mascota().getId();

        if (inputRazaId != null) {
            // validar existencia y pertenencia a tipo
            resolvedRaza = razaRepository.findById(inputRazaId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Raza no encontrada: " + inputRazaId));
            if (resolvedRaza.getTipo_mascota() == null || !resolvedRaza.getTipo_mascota().getId().equals(tipoId)) {
                throw new ValidacionException("La raza seleccionada no pertenece al tipo de mascota indicado");
            }
        } else {
            // nombre manual obligatorio cuando no hay id
            if (inputRazaNombre == null || inputRazaNombre.trim().isEmpty()) {
                throw new ValidacionException("Debe indicar la raza: seleccionar una existente o nombre manual");
            }
            // Delegar a RazaService que normaliza (trim, elimina diacríticos, uppercase), valida tipo y maneja concurrencia
            resolvedRaza = razaService.findOrCreateByNombreAndTipoId(inputRazaNombre, tipoId);
        }

        // Validación adicional: la raza resultante debe tener nombre normalizado
        if (resolvedRaza == null || resolvedRaza.getNombreNormalizado() == null || resolvedRaza.getNombreNormalizado().trim().isEmpty()) {
            throw new ValidacionException("Raza inválida o no normalizada");
        }

        Mascota mascota = new Mascota(request, usuario, fotosJson);
        mascota.setRaza(resolvedRaza);
        try {
            return MascotaResponse.from(mascotaRepository.save(mascota));
        } catch (DataIntegrityViolationException ex) {
            // Normalizar error para el cliente: conflicto en integridad (por ejemplo constraint unique)
            throw new ValidacionException("No se pudo guardar la mascota: datos inconsistentes o duplicados");
        }
    }

    public MascotaInfo obtenerMascotaResponse(Long id) {
        Mascota m = mascotaRepository.getReferenceById(id);
        String municipio = null;
        String provincia = null;
        if (m.getCoordenadas() != null && !m.getCoordenadas().isEmpty()) {
            try {
                Map<String, String> datos = Georef_ar.getDatos(m.getCoordenadas());
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
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada: " + id));
    }

    @Transactional
    public MascotaResponse actualizarMascota(MascotaRequest request, Long id) {
        Mascota mascota = this.obtenerMascota(id);
        if (mascota == null) {
            throw new RecursoNoEncontradoException("Mascota no encontrada: " + id);
        }

        // Obtener el estado anterior antes de actualizar
        Estado estadoAnterior = mascota.getEstado();


        if (request.fotosBase64() != null && !request.fotosBase64().isEmpty()) {
            // Eliminar fotos antiguas
            if (mascota.getFotos() != null && !mascota.getFotos().isEmpty()) {
                try {
                    List<String> oldUrls = objectMapper.readValue(mascota.getFotos(), new com.fasterxml.jackson.core.type.TypeReference<>() {});
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
                    request.fotosBase64() != null ? request.fotosBase64() : java.util.Collections.emptyList(),
                    "mascota_" + System.currentTimeMillis()
            );
            String fotosSerialized = objectMapper.writeValueAsString(fotosUrls);
            if (fotosSerialized != null) {
                mascota.setFotos(fotosSerialized);
            }
        } catch (Exception e){
            throw new ArchivoException("Error al guardar las fotos de la mascota: ", e);
        }

        // Aplicar campos del request sobre la entidad (permitir actualizaciones parciales)
        if (request.nombre() != null) mascota.setNombre(request.nombre());
        if (request.tamanio() != null) mascota.setTamanio(request.tamanio());
        if (request.color() != null) mascota.setColor(request.color());
        if (request.fecha() != null) mascota.setFecha(request.fecha());
        if (request.descripcion() != null) mascota.setDescripcion(request.descripcion());
        if (request.estado() != null) mascota.setEstado(request.estado());
        if (request.coordenadas() != null) mascota.setCoordenadas(request.coordenadas());
        if (request.tipo_mascota() != null) mascota.setTipo_mascota(request.tipo_mascota());

        // Resolver/validar raza similar a crearMascota
        if (request.raza() == null) {
            throw new ValidacionException("Raza es requerida");
        }
        RazaRef inputRaza = request.raza();
        Long inputRazaId = inputRaza.id();
        String inputRazaNombre = inputRaza.nombre();

        // Restricción: si el cliente envía una raza con id, NO puede enviar nombre manual
        if (inputRazaId != null && inputRazaNombre != null && !inputRazaNombre.trim().isEmpty()) {
            throw new ValidacionException("Si envía razaId no debe enviar nombre de raza manual");
        }

        Long tipoId = (request.tipo_mascota() != null && request.tipo_mascota().getId() != null)
                ? request.tipo_mascota().getId() : mascota.getTipo_mascota().getId();

        ttps.spring.models.raza.Raza resolvedRaza;
        if (inputRazaId != null) {
            // validar existencia y pertenencia a tipo
            resolvedRaza = razaRepository.findById(inputRazaId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Raza no encontrada: " + inputRazaId));
            if (resolvedRaza.getTipo_mascota() == null || !resolvedRaza.getTipo_mascota().getId().equals(tipoId)) {
                throw new ValidacionException("La raza seleccionada no pertenece al tipo de mascota indicado");
            }
        } else {
            // nombre manual obligatorio cuando no hay id
            if (inputRazaNombre == null || inputRazaNombre.trim().isEmpty()) {
                throw new ValidacionException("Debe indicar la raza: seleccionar una existente o nombre manual");
            }
            // Delegar a RazaService que normaliza y crea si hace falta
            resolvedRaza = razaService.findOrCreateByNombreAndTipoId(inputRazaNombre, tipoId);
        }

        mascota.setRaza(resolvedRaza);

        // Debug: mostrar valores antes de persistir
        System.out.println("[DEBUG] actualizarMascota - request.descripcion='" + request.descripcion() + "'");
        System.out.println("[DEBUG] actualizarMascota - antes save: id=" + mascota.getId() + ", descripcion='" + mascota.getDescripcion() + "', estado='" + mascota.getEstado() + "'");

        // Guardar cambios
        Mascota saved = mascotaRepository.save(mascota);
        MascotaResponse mascotaActualizada = MascotaResponse.from(saved);

        // Debug: mostrar valores después de persistir
        System.out.println("[DEBUG] actualizarMascota - despues save: id=" + saved.getId() + ", descripcion='" + saved.getDescripcion() + "', estado='" + saved.getEstado() + "'");

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
        if (mascota == null) {
            throw new RecursoNoEncontradoException("Mascota no encontrada: " + id);
        }

        // Eliminar archivos de fotos
        if (mascota.getFotos() != null && !mascota.getFotos().isEmpty()) {
            try {
                List<String> fotosUrls = objectMapper.readValue(mascota.getFotos(), new com.fasterxml.jackson.core.type.TypeReference<>() {});
                fileStorageService.deleteFiles(fotosUrls);
            } catch (Exception e) {
                System.err.println("Error eliminando fotos: " + e.getMessage());
            }
        }

        // Borrado logico
        System.out.println("[DEBUG] eliminarMascota(Long) - marcando inactivo id=" + id);
        mascota.setActivo(false);
        // Persistir el cambio de estado (borrado lógico)
        mascotaRepository.save(mascota);
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
