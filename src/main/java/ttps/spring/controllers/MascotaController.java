package ttps.spring.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttps.spring.dto.MascotaRequest;
import ttps.spring.models.Avistamiento;
import ttps.spring.models.Estado;
import ttps.spring.models.Mascota;
import ttps.spring.models.Tamanio;
import ttps.spring.models.Usuario;
import ttps.spring.services.AvistamientoService;
import ttps.spring.services.FileStorageService;
import ttps.spring.services.MascotaService;
import ttps.spring.services.UsuarioService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/mascotas")
@Tag(name = "Mascotas", description = "API para la gestión de mascotas perdidas y encontradas")
public class MascotaController {

    private final MascotaService mascotaService;
    private final UsuarioService usuarioService;
    private final AvistamientoService avistamientoService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    @Autowired
    public MascotaController(MascotaService mascotaService, UsuarioService usuarioService,
                            AvistamientoService avistamientoService, FileStorageService fileStorageService,
                            ObjectMapper objectMapper) {
        this.mascotaService = mascotaService;
        this.usuarioService = usuarioService;
        this.avistamientoService = avistamientoService;
        this.fileStorageService = fileStorageService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/usuario/{usuarioId}")
    @Operation(summary = "Crear una nueva mascota",
               description = "Registra una nueva mascota asociada a un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Mascota creada exitosamente",
                     content = @Content(schema = @Schema(implementation = Mascota.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> crearMascota(
            @Parameter(description = "ID del usuario propietario") @PathVariable int usuarioId,
            @Parameter(description = "Datos de la mascota a crear") @RequestBody MascotaRequest request) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario((long) usuarioId);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }

            Mascota mascota = new Mascota();
            mascota.setNombre(request.getNombre());
            mascota.setTamanio(Tamanio.valueOf(request.getTamanio().toUpperCase()));
            mascota.setColor(request.getColor());
            mascota.setFecha(request.getFecha() != null ? request.getFecha() : LocalDate.now());
            mascota.setDescripcion(request.getDescripcion());
            mascota.setEstado(request.getEstado() != null
                    ? Estado.valueOf(request.getEstado().toUpperCase())
                    : Estado.RECUPERADO);
            mascota.setCoordenadas(request.getCoordenadas());
            mascota.setTipo(request.getTipo());
            mascota.setRaza(request.getRaza());
            mascota.setUsuario(usuario);
            mascota.setActivo(true);

            // Guardar fotos como archivos y almacenar las URLs
            if (request.getFotosBase64() != null && !request.getFotosBase64().isEmpty()) {
                List<String> fotosUrls = fileStorageService.saveImagesFromBase64(
                    request.getFotosBase64(),
                    "mascota_" + System.currentTimeMillis()
                );
                String fotosJson = objectMapper.writeValueAsString(fotosUrls);
                mascota.setFotos(fotosJson);
            }

            Mascota creada = mascotaService.crearMascota(mascota);

            // Crear avistamiento automáticamente SOLO si el estado es PERDIDO_AJENO
            if (creada.getEstado() == Estado.PERDIDO_AJENO) {
                try {
                    Avistamiento avistamiento = new Avistamiento();
                    avistamiento.setFecha(LocalDate.now());
                    avistamiento.setMascota(creada);
                    avistamiento.setUsuario(usuario);

                    // Usar las mismas coordenadas de la mascota para el avistamiento
                    if (request.getCoordenadas() != null) {
                        avistamiento.setCoordenada(request.getCoordenadas());
                    }

                    // Crear descripción automática para el avistamiento de mascota perdida ajena
                    String descripcionAvistamiento = "Avistamiento inicial - Mascota encontrada sin dueño conocido";

                    if (request.getDescripcion() != null && !request.getDescripcion().trim().isEmpty()) {
                        descripcionAvistamiento += ". " + request.getDescripcion();
                    }
                    avistamiento.setDescripcion(descripcionAvistamiento);

                    // Usar las mismas fotos de la mascota para el avistamiento inicial
                    if (creada.getFotos() != null && !creada.getFotos().isEmpty()) {
                        avistamiento.setFotos(creada.getFotos());
                    }

                    // Crear el avistamiento
                    avistamientoService.crearAvistamiento(avistamiento);

                } catch (Exception e) {
                    // Log del error pero no fallar la creación de la mascota
                    System.err.println("Error al crear avistamiento automático para mascota " +
                        creada.getId() + ": " + e.getMessage());
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Valor invalido: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear mascota: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener mascota por ID",
               description = "Retorna los detalles de una mascota específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mascota encontrada",
                     content = @Content(schema = @Schema(implementation = Mascota.class))),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    public ResponseEntity<?> obtenerMascota(
            @Parameter(description = "ID de la mascota") @PathVariable int id) {
        Mascota mascota = mascotaService.obtenerMascota((long) id);
        if (mascota == null || !mascota.isActivo()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Mascota no encontrada");
        }
        return ResponseEntity.ok(mascota);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener mascotas de un usuario",
               description = "Retorna todas las mascotas registradas por un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de mascotas obtenida exitosamente")
    })
    public ResponseEntity<List<Mascota>> obtenerMascotasUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int usuarioId) {
        List<Mascota> mascotas = mascotaService.obtenerMascotasPorUsuario((long) usuarioId);
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/perdidas")
    @Operation(summary = "Obtener mascotas perdidas",
               description = "Retorna todas las mascotas con estado PERDIDO")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de mascotas perdidas obtenida exitosamente")
    })
    public ResponseEntity<List<Mascota>> obtenerMascotasPerdidas() {
        List<Mascota> perdidas = mascotaService.obtenerMascotasPerdidas();
        return ResponseEntity.ok(perdidas);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar mascota",
               description = "Actualiza la información de una mascota existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mascota actualizada exitosamente",
                     content = @Content(schema = @Schema(implementation = Mascota.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> editarMascota(
            @Parameter(description = "ID de la mascota") @PathVariable int id,
            @Parameter(description = "Datos actualizados de la mascota") @RequestBody MascotaRequest request) {
        try {
            Mascota mascota = mascotaService.obtenerMascota((long) id);
            if (mascota == null || !mascota.isActivo()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Mascota no encontrada");
            }

            if (request.getNombre() != null) mascota.setNombre(request.getNombre());
            if (request.getTamanio() != null) {
                mascota.setTamanio(Tamanio.valueOf(request.getTamanio().toUpperCase()));
            }
            if (request.getColor() != null) mascota.setColor(request.getColor());
            if (request.getFecha() != null) mascota.setFecha(request.getFecha());
            if (request.getDescripcion() != null) mascota.setDescripcion(request.getDescripcion());
            if (request.getEstado() != null) {
                mascota.setEstado(Estado.valueOf(request.getEstado().toUpperCase()));
            }
            if (request.getCoordenadas() != null) mascota.setCoordenadas(request.getCoordenadas());
            if (request.getTipo() != null) mascota.setTipo(request.getTipo());
            if (request.getRaza() != null) mascota.setRaza(request.getRaza());

            // Actualizar fotos si vienen
            if (request.getFotosBase64() != null && !request.getFotosBase64().isEmpty()) {
                // Eliminar fotos antiguas
                if (mascota.getFotos() != null && !mascota.getFotos().isEmpty()) {
                    try {
                        List<String> oldUrls = objectMapper.readValue(mascota.getFotos(), new TypeReference<List<String>>() {});
                        fileStorageService.deleteFiles(oldUrls);
                    } catch (Exception e) {
                        // Log error pero continuar
                        System.err.println("Error eliminando fotos antiguas: " + e.getMessage());
                    }
                }

                // Guardar nuevas fotos
                List<String> fotosUrls = fileStorageService.saveImagesFromBase64(
                    request.getFotosBase64(),
                    "mascota_" + mascota.getId() + "_" + System.currentTimeMillis()
                );
                String fotosJson = objectMapper.writeValueAsString(fotosUrls);
                mascota.setFotos(fotosJson);
            }

            Mascota actualizada = mascotaService.actualizarMascota(mascota);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Valor invalido: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar mascota: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mascota",
               description = "Realiza un borrado lógico de la mascota (marca como inactiva)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mascota eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> eliminarMascota(
            @Parameter(description = "ID de la mascota") @PathVariable int id) {
        try {
            Mascota mascota = mascotaService.obtenerMascota((long) id);
            if (mascota == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Mascota no encontrada");
            }

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
            Mascota mascotaEliminada = mascotaService.actualizarMascota(mascota);
            return ResponseEntity.ok(mascotaEliminada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar mascota: " + e.getMessage());
        }
    }
}
