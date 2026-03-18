package ttps.persistence.dao;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ttps.spring.Application;
import ttps.spring.models.avistamiento.dto.AvistamientoRequest;
import ttps.spring.models.avistamiento.dto.AvistamientoResponse;
import ttps.spring.models.mascota.Estado;
import ttps.spring.models.mascota.Tamanio;
import ttps.spring.models.mascota.dto.MascotaRequest;
import ttps.spring.models.mascota.dto.MascotaResponse;
import ttps.spring.models.usuario.dto.RegistroUsuarioRequest;
import ttps.spring.models.usuario.Usuario;
import ttps.spring.services.AvistamientoService;
import ttps.spring.services.MascotaService;
import ttps.spring.services.UsuarioService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AvistamientoDAOTest {

    @Autowired
    private AvistamientoService avistamientoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MascotaService mascotaService;

    private AvistamientoResponse avistamientoTest;
    private Usuario usuarioReportador;
    private MascotaResponse mascotaAvistada;

    @BeforeAll
    public void setUp() {
        // Crear un usuario reportador
        RegistroUsuarioRequest usuarioReq = new RegistroUsuarioRequest(
                "pedro_rep",
                "Pedro",
                "Martínez",
                "pedro.martinez@example.com",
                "password789",
                "3518888888",
                "Masculino",
                28,
                "Córdoba",
                "Nueva Córdoba",
                "Capital"
        );
        usuarioReportador = usuarioService.crearUsuario(usuarioReq);

        // Crear una mascota para avistar
        MascotaRequest mascotaReq = new MascotaRequest(
                null,
                "Luna",
                Tamanio.PEQUENIO,
                "Blanco y marrón",
                LocalDate.now().minusDays(5),
                "Gata siamesa perdida",
                Estado.PERDIDO_AJENO,
                List.of(),
                "-31.4167,-64.1833",
                "Gato",
                "Siamés",
                true,
                usuarioReportador.getId()
        );
        mascotaAvistada = mascotaService.crearMascota(mascotaReq, usuarioReportador.getId());
    }

    @Test
    @Order(1)
    @DisplayName("Test CREATE - Crear un nuevo avistamiento")
    public void testCreateAvistamiento() {
        // Arrange
        AvistamientoRequest request = new AvistamientoRequest(
                mascotaAvistada.id(),
                usuarioReportador.getId(),
                "Vi a la mascota cerca del parque",
                "-31.4200,-64.1885",
                List.of("/uploads/avistamiento_1.jpg")
        );

        // Act
        AvistamientoResponse avistamientoCreado = avistamientoService.crearAvistamiento(request);
        this.avistamientoTest = avistamientoCreado;

        // Assert
        assertNotNull(avistamientoCreado, "El avistamiento creado no debe ser null");
        assertNotNull(avistamientoCreado.id(), "El ID debe existir");
        assertEquals("-31.4200,-64.1885", avistamientoCreado.coordenada());

        System.out.println("✓ Avistamiento creado con ID: " + avistamientoCreado.id());
    }

    @Test
    @Order(2)
    @DisplayName("Test READ - Obtener avistamiento por ID")
    public void testGetAvistamiento() {
        // Arrange
        Long avistamientoId = avistamientoTest.id();

        // Act
        AvistamientoResponse avistamientoObtenido = avistamientoService.obtenerAvistamiento(avistamientoId);

        // Assert
        assertNotNull(avistamientoObtenido, "El avistamiento obtenido no debe ser null");
        assertEquals(avistamientoId, avistamientoObtenido.id());
        assertEquals("-31.4200,-64.1885", avistamientoObtenido.coordenada());

        System.out.println("✓ Avistamiento obtenido - Coordenada: " + avistamientoObtenido.coordenada());
    }

    @Test
    @Order(3)
    @DisplayName("Test READ ALL - Obtener todos los avistamientos activos")
    public void testGetAllAvistamientos() {
        // Act
        var avistamientos = avistamientoService.obtenerTodosLosAvistamientos(Pageable.unpaged());
        assertNotNull(avistamientos, "La lista de avistamientos no debe ser null");

        System.out.println("✓ Test listado avistamientos ejecutado");
    }

    @Test
    @Order(4)
    @DisplayName("Test UPDATE - Actualizar un avistamiento")
    public void testUpdateAvistamiento() {
        // Arrange
        Long id = avistamientoTest.id();
        ttps.spring.models.avistamiento.dto.AvistamientoUpdateRequest updateReq = new ttps.spring.models.avistamiento.dto.AvistamientoUpdateRequest(
                "Actualizada descripción",
                "-31.4250,-64.1900",
                List.of()
        );

        // Act
        var avistamientoActualizado = avistamientoService.actualizarAvistamiento(id, updateReq);

        // Assert
        assertNotNull(avistamientoActualizado, "El avistamiento actualizado no debe ser null");
        assertEquals("-31.4250,-64.1900", avistamientoActualizado.coordenada());

        System.out.println("✓ Avistamiento actualizado - Nueva coordenada: " + avistamientoActualizado.coordenada());
    }

    @Test
    @Order(5)
    @DisplayName("Test DELETE - Borrado lógico de avistamiento")
    public void testDeleteAvistamiento() {
        // Act
        avistamientoService.eliminarAvistamiento(avistamientoTest.id());

        // Assert - El registro sigue existiendo pero está marcado como inactivo
        var avistamientoBorrado = avistamientoService.obtenerAvistamiento(avistamientoTest.id());
        assertNotNull(avistamientoBorrado, "El avistamiento con borrado lógico no debe ser null");
        assertFalse(avistamientoBorrado.activo(), "El avistamiento debe estar marcado como inactivo");

        System.out.println("✓ Avistamiento marcado como inactivo (borrado lógico) correctamente");
    }

    @Test
    @Order(6)
    @DisplayName("Test DELETE por ID - Borrado lógico por identificador")
    public void testDeletePorId() {
        // Arrange
        AvistamientoRequest request = new AvistamientoRequest(
                mascotaAvistada.id(),
                usuarioReportador.getId(),
                "Vi a la mascota cerca del parque",
                "-31.4300,-64.1900",
                List.of("/uploads/avistamiento_2.jpg")
        );
        var creado = avistamientoService.crearAvistamiento(request);
        Long idAvistamiento = creado.id();

        // Act - Borrado lógico por ID
        avistamientoService.eliminarAvistamiento(idAvistamiento);

        // Assert
        var avistamientoBorradoLogico = avistamientoService.obtenerAvistamiento(idAvistamiento);
        assertNotNull(avistamientoBorradoLogico, "El avistamiento con borrado lógico no debe ser null");
        assertFalse(avistamientoBorradoLogico.activo(), "El avistamiento debe estar marcado como inactivo");

        System.out.println("✓ Avistamiento marcado como inactivo mediante delete(id) correctamente");
    }

    @AfterAll
    public void tearDown() {
        // Limpiar datos de prueba (borrado lógico)
        if (mascotaAvistada != null) {
            mascotaService.eliminarMascota(mascotaService.obtenerMascota(mascotaAvistada.id()).getId());
            System.out.println("✓ Mascota de prueba marcada como inactiva");
        }
        if (usuarioReportador != null) {
            usuarioService.eliminarUsuario(usuarioReportador.getId());
            System.out.println("✓ Usuario de prueba marcado como inactivo");
        }
    }
}