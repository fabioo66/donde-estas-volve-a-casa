package ttps.persistence.dao;

import org.junit.jupiter.api.*;
import ttps.spring.models.Usuario;
import ttps.spring.persistence.dao.impl.UsuarioDAOHibernateJPA;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioDAOTest {

    private static UsuarioDAOHibernateJPA usuarioDAO;
    private static Usuario usuarioTest;

    @BeforeAll
    public static void setUp() {
        usuarioDAO = new UsuarioDAOHibernateJPA();
    }

    @Test
    @Order(1)
    @DisplayName("Test CREATE - Crear un nuevo usuario")
    public void testCreateUsuario() {
        // Arrange
        usuarioTest = new Usuario(
                "Juan",
                "Pérez",
                "juan.perez@example.com",
                "password123",
                "3515555555",
                "Centro",
                "Córdoba"
        );

        // Act
        Usuario usuarioCreado = usuarioDAO.persist(usuarioTest);

        // Assert
        assertNotNull(usuarioCreado, "El usuario creado no debe ser null");
        assertTrue(usuarioCreado.getId() > 0, "El ID debe ser mayor a 0");
        assertEquals("Juan", usuarioCreado.getNombre());
        assertEquals("Pérez", usuarioCreado.getApellido());
        assertEquals("juan.perez@example.com", usuarioCreado.getEmail());

        System.out.println("✓ Usuario creado con ID: " + usuarioCreado.getId());
    }

    @Test
    @Order(2)
    @DisplayName("Test READ - Obtener usuario por ID")
    public void testGetUsuario() {
        // Arrange
        long usuarioId = usuarioTest.getId();

        // Act
        Usuario usuarioObtenido = usuarioDAO.get(usuarioId);

        // Assert
        assertNotNull(usuarioObtenido, "El usuario obtenido no debe ser null");
        assertEquals(usuarioId, usuarioObtenido.getId());
        assertEquals("Juan", usuarioObtenido.getNombre());
        assertEquals("juan.perez@example.com", usuarioObtenido.getEmail());

        System.out.println("✓ Usuario obtenido: " + usuarioObtenido.getNombre() + " " + usuarioObtenido.getApellido());
    }

    @Test
    @Order(3)
    @DisplayName("Test READ ALL - Obtener todos los usuarios")
    public void testGetAllUsuarios() {
        // Act
        List<Usuario> usuarios = usuarioDAO.getAll("nombre");

        // Assert
        assertNotNull(usuarios, "La lista de usuarios no debe ser null");
        assertFalse(usuarios.isEmpty(), "La lista debe contener al menos un usuario");
        assertTrue(usuarios.stream().anyMatch(u -> u.getId() == usuarioTest.getId()),
                "La lista debe contener el usuario de prueba");

        System.out.println("✓ Total de usuarios en la base de datos: " + usuarios.size());
    }

    @Test
    @Order(4)
    @DisplayName("Test UPDATE - Actualizar un usuario")
    public void testUpdateUsuario() {
        // Arrange
        Usuario usuarioParaActualizar = usuarioDAO.get(usuarioTest.getId());
        String nuevoTelefono = "3516666666";
        String nuevaCiudad = "Villa Carlos Paz";

        // Act
        usuarioParaActualizar.setTelefono(nuevoTelefono);
        usuarioParaActualizar.setCiudad(nuevaCiudad);
        Usuario usuarioActualizado = usuarioDAO.update(usuarioParaActualizar);

        // Assert
        assertNotNull(usuarioActualizado, "El usuario actualizado no debe ser null");
        assertEquals(nuevoTelefono, usuarioActualizado.getTelefono());
        assertEquals(nuevaCiudad, usuarioActualizado.getCiudad());

        // Verificar que los cambios persisten en la base de datos
        Usuario usuarioVerificado = usuarioDAO.get(usuarioTest.getId());
        assertEquals(nuevoTelefono, usuarioVerificado.getTelefono());
        assertEquals(nuevaCiudad, usuarioVerificado.getCiudad());

        System.out.println("✓ Usuario actualizado - Nuevo teléfono: " + usuarioActualizado.getTelefono());
    }

    @Test
    @Order(5)
    @DisplayName("Test DELETE - Borrado lógico de usuario")
    public void testDeleteUsuario() {
        // Arrange
        Usuario usuarioAEliminar = usuarioDAO.get(usuarioTest.getId());

        // Act
        usuarioDAO.delete(usuarioAEliminar);

        // Assert - El registro sigue existiendo pero está marcado como inactivo
        Usuario usuarioBorrado = usuarioDAO.get(usuarioTest.getId());
        assertNotNull(usuarioBorrado, "El usuario con borrado lógico no debe ser null");
        assertFalse(usuarioBorrado.isActivo(), "El usuario debe estar marcado como inactivo");

        System.out.println("✓ Usuario marcado como inactivo (borrado lógico) correctamente");
    }

    @Test
    @Order(6)
    @DisplayName("Test DELETE por ID - Borrado lógico por identificador")
    public void testDeletePorId() {
        // Arrange
        Usuario usuarioParaBorradoLogico = new Usuario(
                "Ana",
                "López",
                "ana.lopez@example.com",
                "password999",
                "3519999999",
                "Alta Córdoba",
                "Córdoba"
        );
        usuarioParaBorradoLogico = usuarioDAO.persist(usuarioParaBorradoLogico);
        long idUsuario = usuarioParaBorradoLogico.getId();

        // Act - Borrado lógico por ID
        usuarioDAO.delete(idUsuario);

        // Assert
        Usuario usuarioBorradoLogico = usuarioDAO.get(idUsuario);
        assertNotNull(usuarioBorradoLogico, "El usuario con borrado lógico no debe ser null");
        assertFalse(usuarioBorradoLogico.isActivo(), "El usuario debe estar marcado como inactivo");

        System.out.println("✓ Usuario marcado como inactivo mediante delete(id) correctamente");
    }

    @Test
    @Order(7)
    @DisplayName("Test VERIFICAR CONTRASEÑA - Verificar contraseña correcta e incorrecta")
    public void testVerificarContrasenia() {
        // Arrange
        String contraseniaOriginal = "miClaveSegura123";
        Usuario usuario = new Usuario(
                "Pedro",
                "Martínez",
                "pedro.martinez@example.com",
                contraseniaOriginal,
                "3517777777",
                "Nueva Córdoba",
                "Córdoba"
        );
        usuario = usuarioDAO.persist(usuario);

        // Act & Assert - Verificar contraseña correcta
        assertTrue(usuario.verificarContrasenia(contraseniaOriginal),
                "La contraseña original debe ser verificada correctamente");

        // Act & Assert - Verificar contraseña incorrecta
        assertFalse(usuario.verificarContrasenia("contraseñaIncorrecta"),
                "Una contraseña incorrecta no debe ser verificada");

        // Assert - La contraseña almacenada debe estar hasheada (no debe ser igual al texto plano)
        assertNotEquals(contraseniaOriginal, usuario.getContrasenia(),
                "La contraseña almacenada debe estar hasheada, no en texto plano");

        // Assert - El hash debe tener el formato correcto de BCrypt (60 caracteres, empieza con $2a$)
        assertTrue(usuario.getContrasenia().startsWith("$2a$"),
                "El hash debe tener el formato BCrypt ($2a$)");
        assertEquals(60, usuario.getContrasenia().length(),
                "El hash BCrypt debe tener 60 caracteres");

        System.out.println("✓ Verificación de contraseña funciona correctamente");
        System.out.println("  - Contraseña original: " + contraseniaOriginal);
        System.out.println("  - Hash almacenado: " + usuario.getContrasenia());
    }

    @Test
    @Order(8)
    @DisplayName("Test CAMBIAR CONTRASEÑA - Actualizar contraseña usando setContrasenia")
    public void testCambiarContrasenia() {
        // Arrange
        String contraseniaInicial = "claveInicial456";
        String nuevaContrasenia = "claveNueva789";
        Usuario usuario = new Usuario(
                "María",
                "García",
                "maria.garcia@example.com",
                contraseniaInicial,
                "3518888888",
                "Güemes",
                "Córdoba"
        );
        usuario = usuarioDAO.persist(usuario);
        String hashInicial = usuario.getContrasenia();

        // Act - Cambiar la contraseña usando setContrasenia
        usuario.setContrasenia(nuevaContrasenia);
        Usuario usuarioActualizado = usuarioDAO.update(usuario);

        // Assert - La contraseña inicial ya no debe ser válida
        assertFalse(usuarioActualizado.verificarContrasenia(contraseniaInicial),
                "La contraseña inicial ya no debe ser válida");

        // Assert - La nueva contraseña debe ser válida
        assertTrue(usuarioActualizado.verificarContrasenia(nuevaContrasenia),
                "La nueva contraseña debe ser válida");

        // Assert - El hash debe haber cambiado
        assertNotEquals(hashInicial, usuarioActualizado.getContrasenia(),
                "El hash de la contraseña debe haber cambiado");

        // Verificar que los cambios persisten en la base de datos
        Usuario usuarioVerificado = usuarioDAO.get(usuarioActualizado.getId());
        assertTrue(usuarioVerificado.verificarContrasenia(nuevaContrasenia),
                "La nueva contraseña debe persistir en la base de datos");

        System.out.println("✓ Cambio de contraseña funciona correctamente");
        System.out.println("  - Hash inicial: " + hashInicial);
        System.out.println("  - Hash nuevo:   " + usuarioActualizado.getContrasenia());
    }
}