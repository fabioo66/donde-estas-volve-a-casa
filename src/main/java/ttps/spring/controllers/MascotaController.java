package ttps.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttps.spring.dto.MascotaRequest;
import ttps.spring.models.Estado;
import ttps.spring.models.Mascota;
import ttps.spring.models.Tamanio;
import ttps.spring.models.Usuario;
import ttps.spring.services.MascotaService;
import ttps.spring.services.UsuarioService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;
    private final UsuarioService usuarioService;

    @Autowired
    public MascotaController(MascotaService mascotaService, UsuarioService usuarioService) {
        this.mascotaService = mascotaService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> crearMascota(@PathVariable int usuarioId,
                                          @RequestBody MascotaRequest request) {
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

            // Convertir fotos de base64 a byte[]
            if (request.getFotosBase64() != null && !request.getFotosBase64().isEmpty()) {
                List<byte[]> fotos = new ArrayList<>();
                for (String fotoBase64 : request.getFotosBase64()) {
                    fotos.add(Base64.getDecoder().decode(fotoBase64));
                }
                mascota.setFotos(fotos);
            }

            Mascota creada = mascotaService.crearMascota(mascota);
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
    public ResponseEntity<?> obtenerMascota(@PathVariable int id) {
        Mascota mascota = mascotaService.obtenerMascota((long) id);
        if (mascota == null || !mascota.isActivo()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Mascota no encontrada");
        }
        return ResponseEntity.ok(mascota);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Mascota>> obtenerMascotasUsuario(@PathVariable int usuarioId) {
        List<Mascota> mascotas = mascotaService.obtenerMascotasPorUsuario((long) usuarioId);
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/perdidas")
    public ResponseEntity<List<Mascota>> obtenerMascotasPerdidas() {
        List<Mascota> perdidas = mascotaService.obtenerMascotasPerdidas();
        return ResponseEntity.ok(perdidas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarMascota(@PathVariable int id,
                                           @RequestBody MascotaRequest request) {
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
                List<byte[]> fotos = new ArrayList<>();
                for (String fotoBase64 : request.getFotosBase64()) {
                    fotos.add(Base64.getDecoder().decode(fotoBase64));
                }
                mascota.setFotos(fotos);
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
    public ResponseEntity<?> eliminarMascota(@PathVariable int id) {
        try {
            Mascota mascota = mascotaService.obtenerMascota((long) id);
            if (mascota == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Mascota no encontrada");
            }
            // Borrado logico
            mascota.setActivo(false);
            mascotaService.actualizarMascota(mascota);
            return ResponseEntity.ok("Mascota eliminada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar mascota: " + e.getMessage());
        }
    }
}
