package ttps.spring.controllers;

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
import ttps.spring.models.Avistamiento;
import ttps.spring.models.Mascota;
import ttps.spring.models.Usuario;
import ttps.spring.services.AvistamientoService;
import ttps.spring.services.MascotaService;
import ttps.spring.services.UsuarioService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/avistamientos")
@Tag(name = "Avistamientos", description = "API para la gestión de avistamientos de mascotas")
public class AvistamientoController {

    private final AvistamientoService avistamientoService;
    private final MascotaService mascotaService;
    private final UsuarioService usuarioService;

    @Autowired
    public AvistamientoController(AvistamientoService avistamientoService,
                                  MascotaService mascotaService,
                                  UsuarioService usuarioService) {
        this.avistamientoService = avistamientoService;
        this.mascotaService = mascotaService;
        this.usuarioService = usuarioService;
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

            // Asignar foto si viene
            if (request.getFoto() != null && !request.getFoto().isEmpty()) {
                avistamiento.setFotos(request.getFoto().getBytes());
            }

            avistamiento.setFecha(LocalDate.now());
            avistamiento.setMascota(mascota);
            avistamiento.setUsuario(usuario);

            Avistamiento creado = avistamientoService.crearAvistamiento(avistamiento);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
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
    public ResponseEntity<List<Avistamiento>> obtenerTodosLosAvistamientos() {
        List<Avistamiento> avistamientos = avistamientoService.obtenerTodosLosAvistamientos();
        return ResponseEntity.ok(avistamientos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener avistamiento por ID",
            description = "Retorna los detalles de un avistamiento específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avistamiento encontrado",
                    content = @Content(schema = @Schema(implementation = Avistamiento.class))),
            @ApiResponse(responseCode = "404", description = "Avistamiento no encontrado")
    })
    public ResponseEntity<?> obtenerAvistamiento(
            @Parameter(description = "ID del avistamiento") @PathVariable Long id) {
        Avistamiento avistamiento = avistamientoService.obtenerAvistamiento(id);
        if (avistamiento == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Avistamiento no encontrado");
        }
        return ResponseEntity.ok(avistamiento);
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

            // Actualizar foto
            if (request.getFoto() != null && !request.getFoto().isEmpty()) {
                avistamiento.setFotos(request.getFoto().getBytes());
            }

            Avistamiento actualizado = avistamientoService.actualizarAvistamiento(avistamiento);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar avistamiento: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar avistamiento",
            description = "Elimina permanentemente un avistamiento del sistema")
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

            return ResponseEntity.ok(avistamientos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener avistamientos: " + e.getMessage());
        }
    }
}
