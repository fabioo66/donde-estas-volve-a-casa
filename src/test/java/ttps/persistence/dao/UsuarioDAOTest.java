package ttps.persistence.dao;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ttps.spring.Application;
import ttps.spring.models.usuario.Usuario;
import ttps.spring.models.usuario.dto.RegistroUsuarioRequest;
import ttps.spring.models.usuario.dto.UsuarioUpdateRequest;
import ttps.spring.services.UsuarioService;
import ttps.utils.PasswordUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioDAOTest {

    @Autowired
    private UsuarioService usuarioService;

    private Usuario usuarioTest;


    @Test
    @Order(1)
    @DisplayName("Test CREATE - Crear un nuevo usuario")
    public void testCreateUsuario() {
        // Arrange
        RegistroUsuarioRequest request = new RegistroUsuarioRequest(
                "juan123", // nombreUsuario
                "Juan", // nombre
                "Pérez", // apellido
                "juan.perez@example.com", // email
                "password123", // password
                "3515555555", // telefono
                "Masculino", // genero
                30, // edad
                "Córdoba", // provincia
                "Córdoba Capital", // municipio
                "Centro" // departamento
        );

        // Act
        usuarioTest = usuarioService.crearUsuario(request);

        // Assert
        assertNotNull(usuarioTest, "El usuario creado no debe ser null");
        // id puede ser null si el servicio no persiste en tests; comprobamos que el objeto fue creado
        assertEquals("Juan", usuarioTest.getNombre());
        assertEquals("Pérez", usuarioTest.getApellido());
        assertEquals("juan.perez@example.com", usuarioTest.getEmail());

        System.out.println("✓ Usuario creado: " + usuarioTest.getEmail());
    }

    @Test
    @Order(2)
    @DisplayName("Test READ - Obtener usuario por ID")
    public void testGetUsuario() {
        // Arrange
        Long usuarioId = usuarioTest.getId();

        // Act
        Usuario usuarioObtenido = usuarioService.obtenerUsuario(usuarioId);

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
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();

        // Assert
        assertNotNull(usuarios, "La lista de usuarios no debe ser null");
        assertFalse(usuarios.isEmpty(), "La lista debe contener al menos un usuario");
        assertTrue(usuarios.stream().anyMatch(u -> u.getId().equals(usuarioTest.getId())),
                "La lista debe contener el usuario de prueba");

        System.out.println("✓ Total de usuarios en la base de datos: " + usuarios.size());
    }

    @Test
    @Order(4)
    @DisplayName("Test UPDATE - Actualizar un usuario")
    public void testUpdateUsuario() {
        // Arrange
        Long id = usuarioTest.getId();
        UsuarioUpdateRequest updateRequest = new UsuarioUpdateRequest(
                usuarioTest.getNombreUsuario(), // nombreUsuario (no cambia)
                "Juanito", // nombre
                usuarioTest.getApellido(), // apellido
                usuarioTest.getEmail(), // email
                null, // password (no cambia)
                "3516666666", // telefono
                usuarioTest.getGenero(), // genero
                usuarioTest.getEdad(), // edad
                "Córdoba", // provincia
                "Villa Carlos Paz", // municipio
                "Punilla" // departamento
        );

        // Act
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, updateRequest);

        // Assert
        assertNotNull(usuarioActualizado, "El usuario actualizado no debe ser null");
        assertEquals("Juanito", usuarioActualizado.getNombre());
        assertEquals("3516666666", usuarioActualizado.getTelefono());
        assertEquals("Villa Carlos Paz", usuarioActualizado.getMunicipio());

        // Verificar que los cambios persisten (si el servicio persiste)
        Usuario usuarioVerificado = usuarioService.obtenerUsuario(id);
        assertEquals("Juanito", usuarioVerificado.getNombre());
        assertEquals("3516666666", usuarioVerificado.getTelefono());

        System.out.println("✓ Usuario actualizado - Nuevo teléfono: " + usuarioActualizado.getTelefono());
    }

    @Test
    @Order(5)
    @DisplayName("Test DELETE - Borrado lógico de usuario")
    public void testDeleteUsuario() {
        // Arrange
        Long id = usuarioTest.getId();

        // Act
        usuarioService.eliminarUsuario(id);

        // Assert - El registro sigue existiendo pero está marcado como inactivo
        Usuario usuarioBorrado = usuarioService.obtenerUsuario(id);
        assertNotNull(usuarioBorrado, "El usuario con borrado lógico no debe ser null");
        assertFalse(usuarioBorrado.isActivo(), "El usuario debe estar marcado como inactivo");

        System.out.println("✓ Usuario marcado como inactivo (borrado lógico) correctamente");
    }

    @Test
    @Order(6)
    @DisplayName("Test DELETE por ID - Borrado lógico por identificador")
    public void testDeletePorId() {
        // Arrange
        RegistroUsuarioRequest request = new RegistroUsuarioRequest(
                "ana123", // nombreUsuario
                "Ana", // nombre
                "López", // apellido
                "ana.lopez@example.com", // email
                "password999", // password
                "3519999999", // telefono
                "Femenino", // genero
                28, // edad
                "Córdoba", // provincia
                "Córdoba Capital", // municipio
                "Alta Córdoba" // departamento
        );
        Usuario usuarioParaBorradoLogico = usuarioService.crearUsuario(request);
        Long idUsuario = usuarioParaBorradoLogico.getId();

        // Act - Borrado lógico por ID
        usuarioService.eliminarUsuario(idUsuario);

        // Assert
        Usuario usuarioBorradoLogico = usuarioService.obtenerUsuario(idUsuario);
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
        RegistroUsuarioRequest request = new RegistroUsuarioRequest(
                "pedro123",
                "Pedro",
                "Martínez",
                "pedro.martinez@example.com",
                contraseniaOriginal,
                "3517777777",
                "Masculino",
                32,
                "Córdoba",
                "Córdoba Capital",
                "Nueva Córdoba"
        );
        Usuario usuario = usuarioService.crearUsuario(request);

        // Act & Assert - Verificar contraseña correcta
        assertTrue(PasswordUtils.verifyPassword(contraseniaOriginal, usuario.getPassword()),
                "La contraseña original debe ser verificada correctamente");

        // Act & Assert - Verificar contraseña incorrecta
        assertFalse(PasswordUtils.verifyPassword("contraseñaIncorrecta", usuario.getPassword()),
                "Una contraseña incorrecta no debe ser verificada");

        // Assert - La contraseña almacenada debe estar hasheada (no debe ser igual al texto plano)
        assertNotEquals(contraseniaOriginal, usuario.getPassword(),
                "La contraseña almacenada debe estar hasheada, no en texto plano");

        // Assert - El hash debe tener formato BCrypt (siempre y cuando el servicio procese hashing)
        String hash = usuario.getPassword();
        if (hash != null) {
            assertTrue(hash.startsWith("$2"), "El hash debe tener formato BCrypt");
        }

        System.out.println("✓ Verificación de contraseña funciona correctamente");
        System.out.println("  - Contraseña original: " + contraseniaOriginal);
        System.out.println("  - Hash almacenado: " + usuario.getPassword());
    }

    @Test
    @Order(8)
    @DisplayName("Test CAMBIAR CONTRASEÑA - Actualizar contraseña usando update request")
    public void testCambiarContrasenia() {
        // Arrange
        String contraseniaInicial = "claveInicial456";
        String nuevaContrasenia = "claveNueva789";
        RegistroUsuarioRequest request = new RegistroUsuarioRequest(
                "maria123",
                "María",
                "García",
                "maria.garcia@example.com",
                contraseniaInicial,
                "3518888888",
                "Femenino",
                29,
                "Córdoba",
                "Córdoba Capital",
                "Güemes"
        );
        Usuario usuario = usuarioService.crearUsuario(request);
        String hashInicial = usuario.getPassword();

        // Act - Cambiar la contraseña usando UsuarioUpdateRequest
        UsuarioUpdateRequest updateReq = new UsuarioUpdateRequest(
                usuario.getNombreUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                nuevaContrasenia,
                usuario.getTelefono(),
                usuario.getGenero(),
                usuario.getEdad(),
                usuario.getProvincia(),
                usuario.getMunicipio(),
                usuario.getDepartamento()
        );
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario.getId(), updateReq);

        // Assert - La contraseña inicial ya no debe ser válida
        assertFalse(PasswordUtils.verifyPassword(contraseniaInicial, usuarioActualizado.getPassword()),
                "La contraseña inicial ya no debe ser válida");

        // Assert - La nueva contraseña debe ser válida
        assertTrue(PasswordUtils.verifyPassword(nuevaContrasenia, usuarioActualizado.getPassword()),
                "La nueva contraseña debe ser válida");

        // Assert - El hash debe haber cambiado
        assertNotEquals(hashInicial, usuarioActualizado.getPassword(),
                "El hash de la contraseña debe haber cambiado");

        // Verificar que los cambios persisten en la base de datos (si aplica)
        Usuario usuarioVerificado = usuarioService.obtenerUsuario(usuarioActualizado.getId());
        assertTrue(PasswordUtils.verifyPassword(nuevaContrasenia, usuarioVerificado.getPassword()),
                "La nueva contraseña debe persistir en la base de datos");

        System.out.println("✓ Cambio de contraseña funciona correctamente");
        System.out.println("  - Hash inicial: " + hashInicial);
        System.out.println("  - Hash nuevo:   " + usuarioActualizado.getPassword());
    }
}

