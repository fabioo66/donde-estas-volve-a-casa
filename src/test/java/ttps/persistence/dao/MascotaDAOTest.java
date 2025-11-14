package ttps.persistence.dao;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ttps.spring.Application;
import ttps.spring.models.*;
import ttps.spring.services.MascotaService;
import ttps.spring.services.UsuarioService;

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

    private Mascota mascotaTest;
    private Usuario usuarioDuenio;

    @BeforeAll
    public void setUp() {
        // Crear un usuario para asociar las mascotas
        usuarioDuenio = new Usuario(
                "María",
                "González",
                "maria.gonzalez@example.com",
                "password456",
                "3517777777",
                "Alberdi",
                "Córdoba"
        );
        usuarioDuenio = usuarioService.crearUsuario(usuarioDuenio);
    }

    @Test
    @Order(1)
    @DisplayName("Test CREATE - Crear una nueva mascota")
    public void testCreateMascota() {
        // Arrange
        mascotaTest = new Mascota();
        mascotaTest.setNombre("Bobby");
        mascotaTest.setTipo("Perro");
        mascotaTest.setRaza("Golden Retriever");
        mascotaTest.setTamanio(Tamanio.GRANDE);
        mascotaTest.setColor("Dorado");
        mascotaTest.setFecha(LocalDate.now());
        mascotaTest.setEstado(Estado.PERDIDO_PROPIO);
        mascotaTest.setCoordenadas("-31.4201,-64.1888");
        mascotaTest.setDescripcion("Perro grande, muy amigable, color dorado");
        mascotaTest.setFotos(new ArrayList<>());

        usuarioDuenio.agregarMascota(mascotaTest);

        // Act
        Mascota mascotaCreada = mascotaService.crearMascota(mascotaTest);

        // Assert
        assertNotNull(mascotaCreada, "La mascota creada no debe ser null");
        assertTrue(mascotaCreada.getId() > 0, "El ID debe ser mayor a 0");
        assertEquals("Bobby", mascotaCreada.getNombre());
        assertEquals("Perro", mascotaCreada.getTipo());
        assertEquals(Tamanio.GRANDE, mascotaCreada.getTamaño());
        assertEquals(Estado.PERDIDO_PROPIO, mascotaCreada.getEstado());
        assertNotNull(mascotaCreada.getUsuario());
        assertEquals(usuarioDuenio.getId(), mascotaCreada.getUsuario().getId());

        // Verificar bidireccionalidad
        assertTrue(usuarioDuenio.getMascotas().contains(mascotaCreada),
                "El usuario debe tener la mascota en su lista");

        System.out.println("✓ Mascota creada con ID: " + mascotaCreada.getId());
        System.out.println("✓ Bidireccionalidad Usuario-Mascota verificada");
    }

    @Test
    @Order(2)
    @DisplayName("Test READ - Obtener mascota por ID")
    public void testGetMascota() {
        // Arrange
        int mascotaId = mascotaTest.getId();

        // Act
        Mascota mascotaObtenida = mascotaService.obtenerMascota((long) mascotaId);

        // Assert
        assertNotNull(mascotaObtenida, "La mascota obtenida no debe ser null");
        assertEquals(mascotaId, mascotaObtenida.getId());
        assertEquals("Bobby", mascotaObtenida.getNombre());
        assertEquals("Golden Retriever", mascotaObtenida.getRaza());
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
        assertTrue(mascotas.stream().anyMatch(m -> m.getId() == mascotaTest.getId()),
                "La lista debe contener la mascota de prueba");

        System.out.println("✓ Total de mascotas en la base de datos: " + mascotas.size());
    }

    @Test
    @Order(4)
    @DisplayName("Test UPDATE - Actualizar una mascota")
    public void testUpdateMascota() {
        // Arrange
        Mascota mascotaParaActualizar = mascotaService.obtenerMascota((long) mascotaTest.getId());
        String nuevaDescripcion = "Perro grande, muy amigable, fue encontrado!";
        Estado nuevoEstado = Estado.RECUPERADO;

        // Act
        mascotaParaActualizar.setDescripcion(nuevaDescripcion);
        mascotaParaActualizar.setEstado(nuevoEstado);
        Mascota mascotaActualizada = mascotaService.actualizarMascota(mascotaParaActualizar);

        // Assert
        assertNotNull(mascotaActualizada, "La mascota actualizada no debe ser null");
        assertEquals(nuevaDescripcion, mascotaActualizada.getDescripcion());
        assertEquals(nuevoEstado, mascotaActualizada.getEstado());

        // Verificar que los cambios persisten en la base de datos
        Mascota mascotaVerificada = mascotaService.obtenerMascota((long) mascotaTest.getId());
        assertEquals(nuevaDescripcion, mascotaVerificada.getDescripcion());
        assertEquals(Estado.RECUPERADO, mascotaVerificada.getEstado());

        System.out.println("✓ Mascota actualizada - Nuevo estado: " + mascotaActualizada.getEstado());
    }

    @Test
    @Order(5)
    @DisplayName("Test DELETE - Borrado lógico de mascota")
    public void testDeleteMascota() {
        // Arrange
        Mascota mascotaAEliminar = mascotaService.obtenerMascota((long) mascotaTest.getId());

        // Act
        mascotaService.eliminarMascota(mascotaAEliminar);

        // Assert - El registro sigue existiendo pero está marcado como inactivo
        Mascota mascotaBorrada = mascotaService.obtenerMascota((long) mascotaTest.getId());
        assertNotNull(mascotaBorrada, "La mascota con borrado lógico no debe ser null");
        assertFalse(mascotaBorrada.isActivo(), "La mascota debe estar marcada como inactiva");

        System.out.println("✓ Mascota marcada como inactiva (borrado lógico) correctamente");
    }

    @Test
    @Order(6)
    @DisplayName("Test DELETE por ID - Borrado lógico por identificador")
    public void testDeletePorId() {
        // Arrange
        Mascota mascotaParaBorradoLogico = new Mascota();
        mascotaParaBorradoLogico.setNombre("Max");
        mascotaParaBorradoLogico.setTipo("Perro");
        mascotaParaBorradoLogico.setTamanio(Tamanio.GRANDE);

        usuarioDuenio.agregarMascota(mascotaParaBorradoLogico);

        mascotaParaBorradoLogico.setFecha(LocalDate.now());
        mascotaParaBorradoLogico.setEstado(Estado.ADOPTADO);
        mascotaParaBorradoLogico.setCoordenadas("-31.4201,-64.1888");
        mascotaParaBorradoLogico.setDescripcion("Perro adoptado");
        mascotaParaBorradoLogico.setUsuario(usuarioDuenio);
        mascotaParaBorradoLogico.setFotos(new ArrayList<>());
        mascotaParaBorradoLogico.setAvistamientos(new ArrayList<>());
        mascotaParaBorradoLogico = mascotaService.crearMascota(mascotaParaBorradoLogico);
        long idMascota = mascotaParaBorradoLogico.getId();

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
            usuarioService.eliminarUsuario(usuarioDuenio);
            System.out.println("✓ Usuario de prueba marcado como inactivo");
        }
    }
}
