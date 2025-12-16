package ttps.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttps.spring.services.DashboardService;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "API para estadísticas del dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas del dashboard",
               description = "Retorna las estadísticas principales: mascotas perdidas, recuperadas, adoptadas y seguimientos pendientes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    })
    public ResponseEntity<Map<String, Integer>> obtenerEstadisticas() {
        Map<String, Integer> estadisticas = dashboardService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/estadisticas/mascotas-perdidas")
    @Operation(summary = "Obtener cantidad de mascotas perdidas",
               description = "Retorna el número total de mascotas con estado PERDIDO_PROPIO o PERDIDO_AJENO")
    public ResponseEntity<Integer> obtenerMascotasPerdidas() {
        int count = dashboardService.contarMascotasPerdidas();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/estadisticas/recuperadas")
    @Operation(summary = "Obtener cantidad de mascotas recuperadas",
               description = "Retorna el número total de mascotas con estado RECUPERADO")
    public ResponseEntity<Integer> obtenerMascotasRecuperadas() {
        int count = dashboardService.contarMascotasRecuperadas();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/estadisticas/adoptadas")
    @Operation(summary = "Obtener cantidad de mascotas adoptadas",
               description = "Retorna el número total de mascotas con estado ADOPTADO")
    public ResponseEntity<Integer> obtenerMascotasAdoptadas() {
        int count = dashboardService.contarMascotasAdoptadas();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/estadisticas/seguimientos-pendientes")
    @Operation(summary = "Obtener cantidad de seguimientos pendientes",
               description = "Retorna el número total de avistamientos activos sin resolver")
    public ResponseEntity<Integer> obtenerSeguimientosPendientes() {
        int count = dashboardService.contarSeguimientosPendientes();
        return ResponseEntity.ok(count);
    }
}
