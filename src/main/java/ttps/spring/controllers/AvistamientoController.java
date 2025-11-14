package ttps.spring.controllers;

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
    public ResponseEntity<?> crearAvistamiento(@RequestBody AvistamientoRequest request) {
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
    public ResponseEntity<List<Avistamiento>> obtenerTodosLosAvistamientos() {
        List<Avistamiento> avistamientos = avistamientoService.obtenerTodosLosAvistamientos();
        return ResponseEntity.ok(avistamientos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerAvistamiento(@PathVariable Long id) {
        Avistamiento avistamiento = avistamientoService.obtenerAvistamiento(id);
        if (avistamiento == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Avistamiento no encontrado");
        }
        return ResponseEntity.ok(avistamiento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAvistamiento(@PathVariable Long id,
                                                     @RequestBody AvistamientoRequest request) {
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
    public ResponseEntity<?> eliminarAvistamiento(@PathVariable Long id) {
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
    public ResponseEntity<?> obtenerAvistamientosPorMascota(@PathVariable Long mascotaId) {
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
