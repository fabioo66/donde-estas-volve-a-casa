package ttps.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttps.spring.models.raza.DTO.RazaResponse;
import ttps.spring.models.raza.DTO.RazaRequest;
import ttps.spring.services.RazaService;

import java.util.List;

@RestController
@RequestMapping("/razas")
@RequiredArgsConstructor
@Tag(name = "Raza", description = "API para la gestión de razas de mascotas")
public class RazaController {

    private final RazaService razaService;


    @PostMapping("raza/{nombre}/tipo/{tipoId}")
    @Operation(summary = "Crear o encontrar una raza por nombre y tipo de mascota",
               description = "Busca una raza existente por nombre (normalizado) y tipo de mascota. Si no existe, la crea. Este endpoint es principalmente para uso interno por MascotaService.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Raza encontrada o creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Tipo de mascota no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<RazaResponse> findOrCreateRaza(
            @Valid
            @Parameter(description = "Nombre de la raza") @PathVariable String nombre,
            @Parameter(description = "ID del tipo de mascota") @RequestBody Long tipoMascotaId) {
        RazaResponse response = RazaResponse.from(razaService.findOrCreateByNombreAndTipoId(nombre, tipoMascotaId));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{razaId}")
    @Operation(summary = "Retorna la raza asociada a un ID de raza",
               description = "Busca la raza asociada a un ID de raza y la retorna")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Raza encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Raza no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<RazaResponse> getRazas (
            @Parameter(description = "ID de una raza") @PathVariable("razaId") Long razaId){
        return ResponseEntity.ok(razaService.getById(razaId));
    }

    @GetMapping("/tipo/{tipoMascotaId}")
    @Operation(summary = "Retorna la lista de razas asociadas a un tipo de mascota",
            description = "Busca las razas asociadas a un ID de tipo de mascota")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de razas devuelta exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<RazaResponse>> getRazasByTipoMascotaId (
            @Parameter(description = "ID del tipo de mascota") @PathVariable("tipoMascotaId") Long tipoMascotaId){
        return ResponseEntity.ok(razaService.listByTipoId(tipoMascotaId));
    }

    @PutMapping("/raza/{id}")
    @Operation(summary = "Actualizar una raza",
            description = "Actualiza los datos de una raza existente. Endpoint pensado para administración de la API.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Raza actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Raza o tipo de mascota no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<RazaResponse> updateRaza(
            @Parameter(description = "ID de la raza a actualizar") @PathVariable Long id,
            @Valid @RequestBody RazaRequest request) {
        // Construir un DTO intermedio para pasar al servicio (el servicio actualmente espera RazaResponse)
        RazaResponse updated = razaService.updateRaza(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/raza/{id}")
    @Operation(summary = "Eliminar una raza",
            description = "Elimina una raza por su ID. Usar con precaución.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Raza eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Raza no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> deleteRaza(
            @Parameter(description = "ID de la raza a eliminar") @PathVariable Long id) {
        razaService.deleteRaza(id);
        return ResponseEntity.noContent().build();
    }
}
