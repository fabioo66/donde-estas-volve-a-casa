package ttps.persistence.dao;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ttps.spring.Application;
import ttps.spring.models.mascota.Estado;
import ttps.spring.models.mascota.Mascota;
import ttps.spring.models.mascota.Tamanio;
import ttps.spring.models.mascota.dto.MascotaRequest;
import ttps.spring.models.mascota.dto.MascotaResponse;
import ttps.spring.models.usuario.Usuario;
import ttps.spring.models.usuario.dto.RegistroUsuarioRequest;
import ttps.spring.services.MascotaService;
import ttps.spring.services.UsuarioService;
import ttps.spring.models.tipo_mascota.Tipo_mascotaRepository;
import ttps.spring.models.tipo_mascota.Tipo_mascota;
import ttps.spring.models.raza.DTO.RazaRef;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MascotaDAOTest {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private UsuarioService usuarioService;


    @Autowired
    private Tipo_mascotaRepository tipoRepo;

    private MascotaResponse mascotaTest;
    private Usuario usuarioDuenio;
    private Tipo_mascota tipoPerro;

    @BeforeAll
    public void setUp() {
        RegistroUsuarioRequest req = new RegistroUsuarioRequest(
                "maria.gonzalez",
                "María",
                "González",
                "maria.gonzalez@example.com",
                "password456",
                "3517777777",
                "Femenino",
                21,
                "Córdoba",
                "Alberdi",
                "Capital"
        );
        try {
            usuarioDuenio = usuarioService.crearUsuario(req);
        } catch (Exception e) {
            // si ya existía el email u otra condicion, intentar recuperar por email para hacer los tests idempotentes
            try {
                usuarioDuenio = usuarioService.obtenerUsuarioPorEmail(req.email());
            } catch (Exception ex) {
                // si no podemos recuperar, rethrow original para que el error quede claro
                throw e;
            }
        }

        // Crear el tipo de mascota que usaremos en los tests
        tipoPerro = tipoRepo.findByNombreIgnoreCase("Perro")
                .orElseGet(() -> tipoRepo.save(new Tipo_mascota("Perro")));
    }

    @Test
    @Order(1)
    @DisplayName("Test CREATE - Crear una nueva mascota")
    public void testCreateMascota() {
        // Arrange
        MascotaRequest mascotaReq = new MascotaRequest(
                null,
                "Bobby",
                Tamanio.GRANDE,
                "Dorado",
                LocalDate.now(),
                "Perro grande, muy amigable, color dorado",
                Estado.PERDIDO_PROPIO,
                new ArrayList<>(),
                "-31.4201,-64.1888",
                tipoPerro,
                new RazaRef(null, "Golden Retriever"),
                true,
                usuarioDuenio.getId()
        );
        // Act
        MascotaResponse mascotaCreada = mascotaService.crearMascota(mascotaReq, usuarioDuenio.getId());
        mascotaTest = mascotaCreada;

        // Assert
        assertNotNull(mascotaCreada, "La mascota creada no debe ser null");
        assertNotNull(mascotaCreada.id(), "El ID debe existir");
        assertEquals("Bobby", mascotaCreada.nombre());
        assertEquals(tipoPerro.getId(), mascotaCreada.tipo_mascota().getId());
        assertEquals(Tamanio.GRANDE, mascotaCreada.tamanio());
        assertEquals(Estado.PERDIDO_PROPIO, mascotaCreada.estado());
        assertNotNull(mascotaCreada.usuarioId());
        assertEquals(usuarioDuenio.getId(), mascotaCreada.usuarioId());
        // Verificar bidireccionalidad
        assertTrue(usuarioDuenio.getMascotas().stream().anyMatch(m -> m.getId().equals(mascotaCreada.id())),
                "El usuario debe tener la mascota en su lista");

        System.out.println("✓ Mascota creada con ID: " + mascotaCreada.id());
        System.out.println("✓ Bidireccionalidad Usuario-Mascota verificada");
    }

    @Test
    @Order(2)
    @DisplayName("Test READ - Obtener mascota por ID")
    public void testGetMascota() {
        // Arrange
        Long mascotaId = mascotaTest.id();

        // Act
        Mascota mascotaObtenida = mascotaService.obtenerMascota(mascotaId);

        // Assert
        assertNotNull(mascotaObtenida, "La mascota obtenida no debe ser null");
        assertEquals(mascotaId, mascotaObtenida.getId());
        assertEquals("Bobby", mascotaObtenida.getNombre());
        assertEquals("Golden Retriever", mascotaObtenida.getRaza().getNombre());
        assertEquals(Estado.PERDIDO_PROPIO, mascotaObtenida.getEstado());

        System.out.println("✓ Mascota obtenida: " + mascotaObtenida.getNombre() + " - " + mascotaObtenida.getRaza());
    }

    @Test
    @Order(3)
    @DisplayName("Test READ ALL - Obtener todas las mascotas")
    public void testGetAllMascotas() {
        // Act
        List<Mascota> mascotas = mascotaService.obtenerTodasLasMascotas();

        // Assert
        assertNotNull(mascotas, "La lista de mascotas no debe ser null");
        assertFalse(mascotas.isEmpty(), "La lista debe contener al menos una mascota");
        assertTrue(mascotas.stream().anyMatch(m -> m.getId().equals(mascotaTest.id())),
                "La lista debe contener la mascota de prueba");

        System.out.println("✓ Total de mascotas en la base de datos: " + mascotas.size());
    }

    @Test
    @Order(4)
    @DisplayName("Test UPDATE - Actualizar una mascota")
    public void testUpdateMascota() {
        // Arrange
        Mascota mascotaParaActualizar = mascotaService.obtenerMascota(mascotaTest.id());
        String nuevaDescripcion = "Perro grande, muy amigable, fue encontrado!";
        Estado nuevoEstado = Estado.RECUPERADO;

        // Act
        // Para actualizar, construimos un MascotaRequest con los nuevos datos
        MascotaRequest updateReq = new MascotaRequest(
                mascotaParaActualizar.getId(),
                mascotaParaActualizar.getNombre(),
                mascotaParaActualizar.getTamanio(),
                mascotaParaActualizar.getColor(),
                mascotaParaActualizar.getFecha(),
                nuevaDescripcion,
                nuevoEstado,
                new ArrayList<>(),
                mascotaParaActualizar.getCoordenadas(),
                mascotaParaActualizar.getTipo(),
                new RazaRef(mascotaParaActualizar.getRaza().getId(), null),
                mascotaParaActualizar.isActivo(),
                usuarioDuenio.getId()
        );

        MascotaResponse mascotaActualizada = mascotaService.actualizarMascota(updateReq, mascotaParaActualizar.getId());

        // Assert
        assertNotNull(mascotaActualizada, "La mascota actualizada no debe ser null");
        assertEquals(nuevaDescripcion, mascotaActualizada.descripcion());
        assertEquals(nuevoEstado, mascotaActualizada.estado());

        // Verificar que los cambios persisten en la base de datos
        Mascota mascotaVerificada = mascotaService.obtenerMascota(mascotaTest.id());
        assertEquals(nuevaDescripcion, mascotaVerificada.getDescripcion());
        assertEquals(Estado.RECUPERADO, mascotaVerificada.getEstado());

        System.out.println("✓ Mascota actualizada - Nuevo estado: " + mascotaActualizada.estado());
    }

    @Test
    @Order(5)
    @DisplayName("Test DELETE - Borrado lógico de mascota")
    public void testDeleteMascota() {
        // Arrange
        Mascota mascotaAEliminar = mascotaService.obtenerMascota(mascotaTest.id());

        // Act
        mascotaService.eliminarMascota(mascotaAEliminar.getId());

        // Assert - El registro sigue existiendo pero está marcado como inactivo
        Mascota mascotaBorrada = mascotaService.obtenerMascota(mascotaTest.id());
        assertNotNull(mascotaBorrada, "La mascota con borrado lógico no debe ser null");
        assertFalse(mascotaBorrada.isActivo(), "La mascota debe estar marcada como inactiva");

        System.out.println("✓ Mascota marcada como inactiva (borrado lógico) correctamente");
    }

    @Test
    @Order(6)
    @DisplayName("Test DELETE por ID - Borrado lógico por identificador")
    public void testDeletePorId() {
        // Arrange
        MascotaRequest mascotaReq = new MascotaRequest(
                null,
                "Max",
                Tamanio.GRANDE,
                "",
                LocalDate.now(),
                "Perro adoptado",
                Estado.ADOPTADO,
                new ArrayList<>(),
                "-31.4201,-64.1888",
                tipoPerro,
                new RazaRef(null, "Mestizo"),
                true,
                usuarioDuenio.getId()
        );
        MascotaResponse mascotaParaBorradoLogico = mascotaService.crearMascota(mascotaReq, usuarioDuenio.getId());
        long idMascota = mascotaParaBorradoLogico.id();

        // Act - Borrado lógico por ID
        mascotaService.eliminarMascota(idMascota);

        // Assert
        Mascota mascotaBorradaLogico = mascotaService.obtenerMascota(idMascota);
        assertNotNull(mascotaBorradaLogico, "La mascota con borrado lógico no debe ser null");
        assertFalse(mascotaBorradaLogico.isActivo(), "La mascota debe estar marcada como inactiva");

        System.out.println("✓ Mascota marcada como inactiva mediante delete(id) correctamente");
    }

    @AfterAll
    public void tearDown() {
        // Limpiar el usuario creado para las pruebas (borrado lógico)
        if (usuarioDuenio != null) {
            usuarioService.eliminarUsuario(usuarioDuenio.getId());
            System.out.println("✓ Usuario de prueba marcado como inactivo");
        }
    }
}
