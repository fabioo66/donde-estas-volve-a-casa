package ttps.persistence.dao;

import org.junit.jupiter.api.*;
import ttps.spring.models.*;
import ttps.spring.persistence.dao.impl.AvistamientoDAOHibernateJPA;
import ttps.spring.persistence.dao.impl.MascotaDAOHibernateJPA;
import ttps.spring.persistence.dao.impl.UsuarioDAOHibernateJPA;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AvistamientoDAOTest {

    private static AvistamientoDAOHibernateJPA avistamientoDAO;
    private static UsuarioDAOHibernateJPA usuarioDAO;
    private static MascotaDAOHibernateJPA mascotaDAO;
    private static Avistamiento avistamientoTest;
    private static Usuario usuarioReportador;
    private static Mascota mascotaAvistada;

    @BeforeAll
    public static void setUp() {
        avistamientoDAO = new AvistamientoDAOHibernateJPA();
        usuarioDAO = new UsuarioDAOHibernateJPA();
        mascotaDAO = new MascotaDAOHibernateJPA();

        // Crear un usuario reportador
        usuarioReportador = new Usuario(
                "Pedro",
                "Martínez",
                "pedro.martinez@example.com",
                "password789",
                "3518888888",
                "Nueva Córdoba",
                "Córdoba"
        );
        usuarioReportador = usuarioDAO.persist(usuarioReportador);

        // Crear una mascota para avistar
        mascotaAvistada = new Mascota();
        mascotaAvistada.setNombre("Luna");
        mascotaAvistada.setTipo("Gato");
        mascotaAvistada.setRaza("Siamés");
        mascotaAvistada.setTamaño(Tamanio.PEQUENIO);
        mascotaAvistada.setColor("Blanco y marrón");
        mascotaAvistada.setFecha(LocalDate.now().minusDays(5));
        mascotaAvistada.setEstado(Estado.PERDIDO_AJENO);
        mascotaAvistada.setCoordenadas("-31.4167,-64.1833");
        mascotaAvistada.setDescripcion("Gata siamesa perdida");
        mascotaAvistada.setFotos(new ArrayList<>());


        mascotaAvistada = mascotaDAO.persist(mascotaAvistada);
    }

    @Test
    @Order(1)
    @DisplayName("Test CREATE - Crear un nuevo avistamiento")
    public void testCreateAvistamiento() {
        // Arrange
        avistamientoTest = new Avistamiento();
        avistamientoTest.setFecha(LocalDate.now());
        avistamientoTest.setCoordenada("-31.4200,-64.1885");
        avistamientoTest.setFotos("foto_avistamiento.jpg".getBytes());

        usuarioReportador.agregarAvistamiento(avistamientoTest, mascotaAvistada);

        // Act
        Avistamiento avistamientoCreado = avistamientoDAO.persist(avistamientoTest);

        // Assert
        assertNotNull(avistamientoCreado, "El avistamiento creado no debe ser null");
        assertTrue(avistamientoCreado.getId() > 0, "El ID debe ser mayor a 0");
        assertNotNull(avistamientoCreado.getUsuario(), "El avistamiento debe tener un usuario asignado");
        assertNotNull(avistamientoCreado.getMascota(), "El avistamiento debe tener una mascota asignada");
        assertEquals("-31.4200,-64.1885", avistamientoCreado.getCoordenada());
        assertEquals(LocalDate.now(), avistamientoCreado.getFecha());

        assertTrue(usuarioReportador.getAvistamientos().contains(avistamientoCreado),
                "El usuario debe tener el avistamiento en su lista");
        assertEquals(usuarioReportador.getId(), avistamientoCreado.getUsuario().getId(),
                "El avistamiento debe apuntar al usuario correcto");

        assertTrue(mascotaAvistada.getAvistamientos().contains(avistamientoCreado),
                "La mascota debe tener el avistamiento en su lista");
        assertEquals(mascotaAvistada.getId(), avistamientoCreado.getMascota().getId(),
                "El avistamiento debe apuntar a la mascota correcta");

        System.out.println("✓ Avistamiento creado con ID: " + avistamientoCreado.getId());
        System.out.println("✓ Bidireccionalidad Usuario-Avistamiento verificada");
        System.out.println("✓ Bidireccionalidad Mascota-Avistamiento verificada");
    }

    @Test
    @Order(2)
    @DisplayName("Test READ - Obtener avistamiento por ID")
    public void testGetAvistamiento() {
        // Arrange
        int avistamientoId = avistamientoTest.getId();

        // Act
        Avistamiento avistamientoObtenido = avistamientoDAO.get((long) avistamientoId);

        // Assert
        assertNotNull(avistamientoObtenido, "El avistamiento obtenido no debe ser null");
        assertEquals(avistamientoId, avistamientoObtenido.getId());
        assertEquals("-31.4200,-64.1885", avistamientoObtenido.getCoordenada());

        // Verificar que las relaciones bidireccionales se cargan correctamente
        assertNotNull(avistamientoObtenido.getUsuario(), "El usuario del avistamiento no debe ser null");
        assertNotNull(avistamientoObtenido.getMascota(), "La mascota del avistamiento no debe ser null");
        assertEquals(usuarioReportador.getId(), avistamientoObtenido.getUsuario().getId(),
                "El usuario debe coincidir");
        assertEquals(mascotaAvistada.getId(), avistamientoObtenido.getMascota().getId(),
                "La mascota debe coincidir");

        // Verificar que el avistamiento está activo
        assertTrue(avistamientoObtenido.isActivo(), "El avistamiento debe estar activo");

        System.out.println("✓ Avistamiento obtenido - Mascota: " +
                avistamientoObtenido.getMascota().getNombre() +
                " - Coordenada: " + avistamientoObtenido.getCoordenada());
        System.out.println("✓ Bidireccionalidad persistida correctamente en BD");
    }

    @Test
    @Order(3)
    @DisplayName("Test READ ALL - Obtener todos los avistamientos activos")
    public void testGetAllAvistamientos() {
        // Act
        List<Avistamiento> avistamientos = avistamientoDAO.getAll("fecha");

        // Assert
        assertNotNull(avistamientos, "La lista de avistamientos no debe ser null");
        assertFalse(avistamientos.isEmpty(), "La lista debe contener al menos un avistamiento");
        assertTrue(avistamientos.stream().anyMatch(a -> a.getId() == avistamientoTest.getId()),
                "La lista debe contener el avistamiento de prueba");

        // Verificar que todos los avistamientos tienen sus relaciones
        avistamientos.forEach(a -> {
            assertNotNull(a.getUsuario(), "Cada avistamiento debe tener un usuario");
            assertNotNull(a.getMascota(), "Cada avistamiento debe tener una mascota");
            assertTrue(a.isActivo(), "Solo deben retornarse avistamientos activos");
        });

        System.out.println("✓ Total de avistamientos activos en la base de datos: " + avistamientos.size());
        System.out.println("✓ Todas las relaciones bidireccionales están correctamente cargadas");
    }

    @Test
    @Order(4)
    @DisplayName("Test UPDATE - Actualizar un avistamiento")
    public void testUpdateAvistamiento() {
        // Arrange
        Avistamiento avistamientoParaActualizar = avistamientoDAO.get((long) avistamientoTest.getId());
        String nuevaCoordenada = "-31.4250,-64.1900";
        LocalDate nuevaFecha = LocalDate.now().minusDays(1);

        // Act
        avistamientoParaActualizar.setCoordenada(nuevaCoordenada);
        avistamientoParaActualizar.setFecha(nuevaFecha);
        Avistamiento avistamientoActualizado = avistamientoDAO.update(avistamientoParaActualizar);

        // Assert
        assertNotNull(avistamientoActualizado, "El avistamiento actualizado no debe ser null");
        assertEquals(nuevaCoordenada, avistamientoActualizado.getCoordenada());
        assertEquals(nuevaFecha, avistamientoActualizado.getFecha());

        // Verificar que las relaciones bidireccionales se mantienen
        assertNotNull(avistamientoActualizado.getUsuario(), "El usuario debe mantenerse");
        assertNotNull(avistamientoActualizado.getMascota(), "La mascota debe mantenerse");
        assertEquals(usuarioReportador.getId(), avistamientoActualizado.getUsuario().getId());
        assertEquals(mascotaAvistada.getId(), avistamientoActualizado.getMascota().getId());

        // Verificar que los cambios persisten en la base de datos
        Avistamiento avistamientoVerificado = avistamientoDAO.get((long) avistamientoTest.getId());
        assertEquals(nuevaCoordenada, avistamientoVerificado.getCoordenada());
        assertEquals(nuevaFecha, avistamientoVerificado.getFecha());

        System.out.println("✓ Avistamiento actualizado - Nueva coordenada: " +
                avistamientoActualizado.getCoordenada());
        System.out.println("✓ Bidireccionalidad mantenida después de la actualización");
    }

    @Test
    @Order(5)
    @DisplayName("Test DELETE - Borrado lógico de avistamiento")
    public void testDeleteAvistamiento() {
        // Arrange
        Avistamiento avistamientoAEliminar = avistamientoDAO.get((long) avistamientoTest.getId());

        // Act
        avistamientoDAO.delete(avistamientoAEliminar);

        // Assert - El registro sigue existiendo pero está marcado como inactivo
        Avistamiento avistamientoBorrado = avistamientoDAO.get((long) avistamientoTest.getId());
        assertNotNull(avistamientoBorrado, "El avistamiento con borrado lógico no debe ser null");
        assertFalse(avistamientoBorrado.isActivo(), "El avistamiento debe estar marcado como inactivo");

        System.out.println("✓ Avistamiento marcado como inactivo (borrado lógico) correctamente");
    }

    @Test
    @Order(6)
    @DisplayName("Test DELETE por ID - Borrado lógico por identificador")
    public void testDeletePorId() {
        // Arrange
        Avistamiento avistamientoParaBorradoLogico = new Avistamiento();
        avistamientoParaBorradoLogico.setFecha(LocalDate.now());
        avistamientoParaBorradoLogico.setCoordenada("-31.4300,-64.1900");
        avistamientoParaBorradoLogico.setFotos("foto2.jpg".getBytes());

        usuarioReportador.agregarAvistamiento(avistamientoParaBorradoLogico, mascotaAvistada);

        avistamientoParaBorradoLogico = avistamientoDAO.persist(avistamientoParaBorradoLogico);
        long idAvistamiento = avistamientoParaBorradoLogico.getId();

        // Verificar que se creó correctamente con bidireccionalidad
        assertTrue(avistamientoParaBorradoLogico.isActivo(), "Debe estar activo antes del borrado");
        assertNotNull(avistamientoParaBorradoLogico.getUsuario(), "Debe tener usuario asignado");
        assertNotNull(avistamientoParaBorradoLogico.getMascota(), "Debe tener mascota asignada");

        // Act - Borrado lógico por ID
        avistamientoDAO.delete(idAvistamiento);

        // Assert
        Avistamiento avistamientoBorradoLogico = avistamientoDAO.get(idAvistamiento);
        assertNotNull(avistamientoBorradoLogico, "El avistamiento con borrado lógico no debe ser null");
        assertFalse(avistamientoBorradoLogico.isActivo(), "El avistamiento debe estar marcado como inactivo");

        // Verificar que las relaciones bidireccionales se mantienen después del borrado lógico
        assertNotNull(avistamientoBorradoLogico.getUsuario(), "Las relaciones deben mantenerse");
        assertNotNull(avistamientoBorradoLogico.getMascota(), "Las relaciones deben mantenerse");

        System.out.println("✓ Avistamiento marcado como inactivo mediante delete(id) correctamente");
        System.out.println("✓ Bidireccionalidad preservada después del borrado lógico");
    }

    @AfterAll
    public static void tearDown() {
        // Limpiar datos de prueba (borrado lógico)
        if (mascotaAvistada != null) {
            mascotaDAO.delete(mascotaAvistada);
            System.out.println("✓ Mascota de prueba marcada como inactiva");
        }
        if (usuarioReportador != null) {
            usuarioDAO.delete(usuarioReportador);
            System.out.println("✓ Usuario de prueba marcado como inactivo");
        }
    }
}