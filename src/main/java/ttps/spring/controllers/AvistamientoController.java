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
import ttps.spring.dto.AvistamientoRequest;
import ttps.spring.dto.AvistamientoResponse;
import ttps.spring.models.Avistamiento;
import ttps.spring.models.Mascota;
import ttps.spring.models.Usuario;
import ttps.spring.services.AvistamientoService;
import ttps.spring.services.FileStorageService;
import ttps.spring.services.MascotaService;
import ttps.spring.services.UsuarioService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/avistamientos")
@Tag(name = "Avistamientos", description = "API para la gestión de avistamientos de mascotas")
public class AvistamientoController {

    private final AvistamientoService avistamientoService;
    private final MascotaService mascotaService;
    private final UsuarioService usuarioService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AvistamientoController(AvistamientoService avistamientoService,
                                  MascotaService mascotaService,
                                  UsuarioService usuarioService,
                                  FileStorageService fileStorageService,
                                  ObjectMapper objectMapper) {
        this.avistamientoService = avistamientoService;
        this.mascotaService = mascotaService;
        this.usuarioService = usuarioService;
        this.fileStorageService = fileStorageService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @Operation(summary = "Crear nuevo avistamiento",
            description = "Registra un nuevo avistamiento de una mascota perdida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Avistamiento creado exitosamente",
                    content = @Content(schema = @Schema(implementation = Avistamiento.class))),
            @ApiResponse(responseCode = "404", description = "Mascota o usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> crearAvistamiento(
            @Parameter(description = "Datos del avistamiento") @RequestBody AvistamientoRequest request) {
        try {
            // Validar que venga el usuario
            if (request.getUsuarioId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El ID del usuario es requerido");
            }

            // Validar que venga la mascota
            if (request.getMascotaId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El ID de la mascota es requerido");
            }

            // Buscar el usuario
            Usuario usuario = usuarioService.obtenerUsuario(request.getUsuarioId());
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }

            // Buscar la mascota
            Mascota mascota = mascotaService.obtenerMascota(request.getMascotaId());
            if (mascota == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Mascota no encontrada");
            }

            // Crear el avistamiento
            Avistamiento avistamiento = new Avistamiento();

            // Asignar coordenada (ubicacion del request)
            if (request.getUbicacion() != null) {
                avistamiento.setCoordenada(request.getUbicacion());
            }

            // Asignar descripción
            if (request.getDescripcion() != null) {
                avistamiento.setDescripcion(request.getDescripcion());
            }

            // Guardar fotos como archivos y almacenar las URLs
            if (request.getFotosBase64() != null && !request.getFotosBase64().isEmpty()) {
                List<String> fotosUrls = fileStorageService.saveImagesFromBase64(
                    request.getFotosBase64(),
                    "avistamiento_" + System.currentTimeMillis()
                );
                String fotosJson = objectMapper.writeValueAsString(fotosUrls);
                avistamiento.setFotos(fotosJson);
            }

            avistamiento.setFecha(LocalDate.now());
            avistamiento.setMascota(mascota);
            avistamiento.setUsuario(usuario);

            Avistamiento creado = avistamientoService.crearAvistamiento(avistamiento);
            return ResponseEntity.status(HttpStatus.CREATED).body(new AvistamientoResponse(creado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear avistamiento: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todos los avistamientos",
            description = "Retorna la lista completa de avistamientos registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avistamientos obtenida exitosamente")
    })
    public ResponseEntity<List<AvistamientoResponse>> obtenerTodosLosAvistamientos() {
        List<Avistamiento> avistamientos = avistamientoService.obtenerTodosLosAvistamientos();
        List<AvistamientoResponse> response = avistamientos.stream()
                .map(AvistamientoResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener avistamiento por ID",
            description = "Retorna los detalles de un avistamiento específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avistamiento encontrado",
                    content = @Content(schema = @Schema(implementation = AvistamientoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Avistamiento no encontrado")
    })
    public ResponseEntity<?> obtenerAvistamiento(
            @Parameter(description = "ID del avistamiento") @PathVariable Long id) {
        Avistamiento avistamiento = avistamientoService.obtenerAvistamiento(id);
        if (avistamiento == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Avistamiento no encontrado");
        }
        return ResponseEntity.ok(new AvistamientoResponse(avistamiento));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar avistamiento",
            description = "Actualiza la información de un avistamiento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avistamiento actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = Avistamiento.class))),
            @ApiResponse(responseCode = "404", description = "Avistamiento, mascota o usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> actualizarAvistamiento(
            @Parameter(description = "ID del avistamiento") @PathVariable Long id,
            @Parameter(description = "Datos actualizados del avistamiento") @RequestBody AvistamientoRequest request) {
        try {
            Avistamiento avistamiento = avistamientoService.obtenerAvistamiento(id);
            if (avistamiento == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Avistamiento no encontrado");
            }

            // Actualizar usuario si viene en el request
            if (request.getUsuarioId() != null) {
                Usuario usuario = usuarioService.obtenerUsuario(request.getUsuarioId());
                if (usuario == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Usuario no encontrado");
                }
                avistamiento.setUsuario(usuario);
            }

            // Actualizar mascota si viene en el request
            if (request.getMascotaId() != null) {
                Mascota mascota = mascotaService.obtenerMascota(request.getMascotaId());
                if (mascota == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Mascota no encontrada");
                }
                avistamiento.setMascota(mascota);
            }

            // Actualizar ubicación/coordenada
            if (request.getUbicacion() != null) {
                avistamiento.setCoordenada(request.getUbicacion());
            }

            // Actualizar descripción
            if (request.getDescripcion() != null) {
                avistamiento.setDescripcion(request.getDescripcion());
            }

            // Actualizar fotos si vienen
            if (request.getFotosBase64() != null && !request.getFotosBase64().isEmpty()) {
                // Eliminar fotos antiguas
                if (avistamiento.getFotos() != null && !avistamiento.getFotos().isEmpty()) {
                    try {
                        List<String> oldUrls = objectMapper.readValue(avistamiento.getFotos(), new TypeReference<List<String>>() {});
                        fileStorageService.deleteFiles(oldUrls);
                    } catch (Exception e) {
                        // Log error pero continuar
                        System.err.println("Error eliminando fotos antiguas: " + e.getMessage());
                    }
                }

                // Guardar nuevas fotos
                List<String> fotosUrls = fileStorageService.saveImagesFromBase64(
                    request.getFotosBase64(),
                    "avistamiento_" + id + "_" + System.currentTimeMillis()
                );
                String fotosJson = objectMapper.writeValueAsString(fotosUrls);
                avistamiento.setFotos(fotosJson);
            }

            Avistamiento actualizado = avistamientoService.actualizarAvistamiento(avistamiento);
            return ResponseEntity.ok(new AvistamientoResponse(actualizado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar avistamiento: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar avistamiento",
            description = "Realiza un borrado lógico del avistamiento (marca como inactivo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avistamiento eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Avistamiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> eliminarAvistamiento(
            @Parameter(description = "ID del avistamiento") @PathVariable Long id) {
        try {
            Avistamiento avistamiento = avistamientoService.obtenerAvistamiento(id);
            if (avistamiento == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Avistamiento no encontrado");
            }

            // Eliminar archivos de fotos
            if (avistamiento.getFotos() != null && !avistamiento.getFotos().isEmpty()) {
                try {
                    List<String> fotosUrls = objectMapper.readValue(avistamiento.getFotos(), new TypeReference<List<String>>() {});
                    fileStorageService.deleteFiles(fotosUrls);
                } catch (Exception e) {
                    System.err.println("Error eliminando fotos: " + e.getMessage());
                }
            }

            avistamientoService.eliminarAvistamiento(id);
            return ResponseEntity.ok("Avistamiento eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar avistamiento: " + e.getMessage());
        }
    }

    @GetMapping("/mascota/{mascotaId}")
    @Operation(summary = "Obtener avistamientos por mascota",
            description = "Retorna todos los avistamientos reportados para una mascota específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avistamientos obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> obtenerAvistamientosPorMascota(
            @Parameter(description = "ID de la mascota") @PathVariable Long mascotaId) {
        try {
            Mascota mascota = mascotaService.obtenerMascota(mascotaId);
            if (mascota == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Mascota no encontrada");
            }

            List<Avistamiento> avistamientos = avistamientoService.obtenerTodosLosAvistamientos()
                    .stream()
                    .filter(a -> a.getMascota() != null && a.getMascota().getId() == mascotaId.intValue())
                    .toList();

            List<AvistamientoResponse> response = avistamientos.stream()
                    .map(AvistamientoResponse::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener avistamientos: " + e.getMessage());
        }
    }
}
