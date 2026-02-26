package ttps.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ttps.spring.models.avistamiento.dto.AvistamientoRequest;
import ttps.spring.models.avistamiento.dto.AvistamientoResponse;
import ttps.spring.models.avistamiento.dto.AvistamientoUpdateRequest;
import ttps.spring.services.AvistamientoService;

@RestController
@RequestMapping("/avistamientos")
@RequiredArgsConstructor
@Tag(name = "Avistamientos", description = "API para la gestión de avistamientos de mascotas")
public class AvistamientoController {

    private final AvistamientoService avistamientoService;

    @PostMapping
    @Operation(summary = "Crear nuevo avistamiento")
    @ApiResponse(responseCode = "201", description = "Avistamiento creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de la solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Mascota o usuario no encontrado o inactivo")
    public ResponseEntity<AvistamientoResponse> crearAvistamiento(
            @Valid @RequestBody AvistamientoRequest request,
            UriComponentsBuilder uriBuilder) {
        AvistamientoResponse response = avistamientoService.crearAvistamiento(request);
        var uri = uriBuilder.path("/avistamientos/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los avistamientos", description = "Paginado, ordenado por fecha descendente por defecto")
    public ResponseEntity<Page<AvistamientoResponse>> obtenerTodosLosAvistamientos(
            @PageableDefault(size = 10, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(avistamientoService.obtenerTodosLosAvistamientos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener avistamiento por ID")
    @ApiResponse(responseCode = "404", description = "Avistamiento no encontrado")
    public ResponseEntity<AvistamientoResponse> obtenerAvistamiento(@PathVariable Long id) {
        return ResponseEntity.ok(avistamientoService.obtenerAvistamiento(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar avistamiento", description = "Solo permite modificar descripción, ubicación y fotos")
    @ApiResponse(responseCode = "404", description = "Avistamiento no encontrado")
    public ResponseEntity<AvistamientoResponse> actualizarAvistamiento(
            @PathVariable Long id,
            @RequestBody AvistamientoUpdateRequest request) {
        return ResponseEntity.ok(avistamientoService.actualizarAvistamiento(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar avistamiento", description = "Borrado lógico: marca el avistamiento como inactivo, no lo elimina de la base de datos")
    @ApiResponse(responseCode = "204", description = "Avistamiento eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "Avistamiento no encontrado")
    public ResponseEntity<Void> eliminarAvistamiento(@PathVariable Long id) {
        avistamientoService.eliminarAvistamiento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mascota/{mascotaId}")
    @Operation(summary = "Obtener avistamientos por mascota", description = "Paginado, ordenado por fecha descendente por defecto")
    @ApiResponse(responseCode = "404", description = "Mascota no encontrada o inactiva")
    public ResponseEntity<Page<AvistamientoResponse>> obtenerAvistamientosPorMascota(
            @PathVariable Long mascotaId,
            @PageableDefault(size = 10, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(avistamientoService.obtenerAvistamientosPorMascota(mascotaId, pageable));
    }
}
