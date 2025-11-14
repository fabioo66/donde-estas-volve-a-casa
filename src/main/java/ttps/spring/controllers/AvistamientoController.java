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
import ttps.spring.services.AvistamientoService;
import ttps.spring.services.MascotaService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/avistamientos")
@Tag(name = "Avistamientos", description = "API para la gestión de avistamientos de mascotas")
public class AvistamientoController {

    private final AvistamientoService avistamientoService;
    private final MascotaService mascotaService;

    @Autowired
    public AvistamientoController(AvistamientoService avistamientoService,
                                  MascotaService mascotaService) {
        this.avistamientoService = avistamientoService;
        this.mascotaService = mascotaService;
    }

    @PostMapping
    @Operation(summary = "Crear nuevo avistamiento",
            description = "Registra un nuevo avistamiento de una mascota perdida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Avistamiento creado exitosamente",
                    content = @Content(schema = @Schema(implementation = Avistamiento.class))),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> crearAvistamiento(
            @Parameter(description = "Datos del avistamiento") @RequestBody AvistamientoRequest request) {
        try {
            Mascota mascota = mascotaService.obtenerMascota(request.getMascotaId());
            if (mascota == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Mascota no encontrada");
            }

            Avistamiento avistamiento = new Avistamiento();
            // El modelo usa 'coordenada', mapeamos desde 'ubicacion' del request
            if (request.getUbicacion() != null) {
                avistamiento.setCoordenada(request.getUbicacion());
            } else if (request.getDescripcion() != null) {
                avistamiento.setCoordenada(request.getDescripcion());
            }

            // El modelo usa byte[] fotos, pero el request tiene String
            // Deberías convertir la foto de String (base64) a byte[]
            if (request.getFoto() != null && !request.getFoto().isEmpty()) {
                avistamiento.setFotos(request.getFoto().getBytes());
            }

            avistamiento.setFecha(LocalDate.now());
            avistamiento.setMascota(mascota);

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
            @ApiResponse(responseCode = "404", description = "Avistamiento o mascota no encontrada"),
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

            if (request.getMascotaId() != null) {
                Mascota mascota = mascotaService.obtenerMascota(request.getMascotaId());
                if (mascota == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Mascota no encontrada");
                }
                avistamiento.setMascota(mascota);
            }

            if (request.getUbicacion() != null) {
                avistamiento.setCoordenada(request.getUbicacion());
            } else if (request.getDescripcion() != null) {
                avistamiento.setCoordenada(request.getDescripcion());
            }

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
