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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ttps.spring.models.mascota.dto.MascotaInfo;
import ttps.spring.models.mascota.dto.MascotaRequest;
import ttps.spring.models.mascota.Estado;
import ttps.spring.models.mascota.Mascota;
import ttps.spring.models.mascota.Tamanio;
import ttps.spring.models.mascota.dto.MascotaResponse;
import ttps.spring.models.usuario.Usuario;
import ttps.spring.services.AvistamientoService;
import ttps.spring.services.FileStorageService;
import ttps.spring.services.MascotaService;
import ttps.spring.services.UsuarioService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/mascotas")
@RequiredArgsConstructor
@Tag(name = "Mascotas", description = "API para la gestión de mascotas perdidas y encontradas")
public class MascotaController {

    private final MascotaService mascotaService;
    private final UsuarioService usuarioService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

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
    public ResponseEntity<MascotaResponse> crearMascota(
            @Valid
            @Parameter(description = "ID del usuario propietario") @PathVariable Long usuarioId,
            @Parameter(description = "Datos de la mascota a crear") @RequestBody MascotaRequest request) {
            MascotaResponse response = mascotaService.crearMascota(request, usuarioId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener mascota por ID",
               description = "Retorna los detalles de una mascota específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mascota encontrada",
                     content = @Content(schema = @Schema(implementation = Mascota.class))),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")

    })
    public ResponseEntity<MascotaInfo> obtenerMascota(
            @Parameter(description = "ID de la mascota") @PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.obtenerMascotaResponse(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener mascotas de un usuario",
               description = "Retorna todas las mascotas registradas por un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de mascotas obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<MascotaResponse>> obtenerMascotasUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(mascotaService.obtenerMascotasPorUsuario(usuarioId));
    }

    @GetMapping("/perdidas")
    @Operation(summary = "Obtener mascotas perdidas",
               description = "Retorna todas las mascotas con estado PERDIDO")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de mascotas perdidas obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<MascotaResponse>> obtenerMascotasPerdidas() {
        return ResponseEntity.ok(mascotaService.obtenerMascotasPerdidas());
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
    public ResponseEntity<MascotaResponse> editarMascota(
            @Parameter(description = "ID de la mascota") @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la mascota") @RequestBody MascotaRequest request) {
        return ResponseEntity.ok(mascotaService.actualizarMascota(request, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mascota",
               description = "Realiza un borrado lógico de la mascota (marca como inactiva)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mascota eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public void eliminarMascota(
            @Parameter(description = "ID de la mascota") @PathVariable Long id) {
        mascotaService.eliminarMascota(id);
    }
}
