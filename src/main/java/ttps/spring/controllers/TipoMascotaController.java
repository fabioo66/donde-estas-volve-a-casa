package ttps.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttps.spring.models.tipo_mascota.DTO.Tipo_mascotaResponse;
import ttps.spring.services.TipoMascotaService;

import java.util.List;

@RestController
@RequestMapping("/tipos_mascota")
@RequiredArgsConstructor
@Tag(name = "TipoMascota", description = "API para la gestión de tipos de mascotas")
public class TipoMascotaController {
    private final TipoMascotaService tipoMascotaService;

    @PostMapping("/tipo/{nombre}")
    @Operation(summary = "Crear un nuevo tipo de mascota",
               description = "Crea un nuevo tipo de mascota con el nombre proporcionado. Este endpoint es principalmente para uso interno por MascotaService.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de mascota creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Tipo_mascotaResponse> createTipoMascota(@PathVariable("nombre") String nombre) {
        Tipo_mascotaResponse response = tipoMascotaService.createTipoMascota(nombre);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los tipos de mascotas",
               description = "Retorna una lista de todos los tipos de mascotas disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipos de mascotas obtenidos exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<Tipo_mascotaResponse>> getTipoMascotas() {
        List<Tipo_mascotaResponse> response = tipoMascotaService.findAll();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/tipo/{id}")
    @Operation(summary = "Eliminar un tipo de mascota por ID",
               description = "Elimina un tipo de mascota específico por su ID. Este endpoint es principalmente para uso interno por MascotaService.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de mascota eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tipo de mascota no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> deleteTipoMascota(@PathVariable Long id) {
        tipoMascotaService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/tipo/{id}/nombre/{nuevoNombre}")
    @Operation(summary = "Actualizar el nombre de un tipo de mascota por ID",
               description = "Actualiza el nombre de un tipo de mascota específico por su ID. Este endpoint es principalmente para uso interno por MascotaService.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de mascota actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tipo de mascota no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Tipo_mascotaResponse> updateTipoMascota(@PathVariable Long id, @PathVariable String nuevoNombre) {
        Tipo_mascotaResponse response = tipoMascotaService.updateNombreById(id, nuevoNombre);
        return ResponseEntity.ok(response);
    }
}
