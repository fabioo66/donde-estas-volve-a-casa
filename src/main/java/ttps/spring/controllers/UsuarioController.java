package ttps.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttps.spring.dto.LoginRequest;
import ttps.spring.dto.LoginResponse;
import ttps.spring.dto.RegistroUsuarioRequest;
import ttps.spring.models.Usuario;
import ttps.spring.models.UsuarioRegistrado;
import ttps.spring.services.UsuarioService;
import ttps.utils.PasswordUtils;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios y autenticación")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta de usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> registrarUsuario(
            @Parameter(description = "Datos del nuevo usuario") @RequestBody RegistroUsuarioRequest request) {
        try {
            // Validar que el email no exista
            if (usuarioService.obtenerUsuarioPorEmail(request.getEmail()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("El email ya esta registrado");
            }

            UsuarioRegistrado usuario = new UsuarioRegistrado();
            usuario.setNombre(request.getNombre());
            usuario.setApellido(request.getApellido());
            usuario.setEmail(request.getEmail());
            usuario.setContrasenia(request.getPassword()); // setContrasenia ya hashea automáticamente
            usuario.setTelefono(request.getTelefono());
            // Usar barrio y ciudad en lugar de direccion
            if (request.getDireccion() != null) {
                usuario.setBarrio(request.getDireccion());
            }

            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar usuario: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
            description = "Autentica a un usuario con email y contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> login(
            @Parameter(description = "Credenciales de login") @RequestBody LoginRequest request) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorEmail(request.getEmail());

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Credenciales invalidas");
            }

            if (!PasswordUtils.verifyPassword(request.getPassword(), usuario.getContrasenia())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Credenciales invalidas");
            }

            String tipo = usuario.getClass().getSimpleName().toUpperCase();
            LoginResponse response = new LoginResponse(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getEmail(),
                    tipo
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en login: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID",
            description = "Retorna los detalles de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> obtenerUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerUsuario(id);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar perfil de usuario",
            description = "Actualiza la información del perfil de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> editarPerfil(
            @Parameter(description = "ID del usuario") @PathVariable Long id,
            @Parameter(description = "Datos actualizados del usuario") @RequestBody RegistroUsuarioRequest request) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(id);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }

            usuario.setNombre(request.getNombre());
            usuario.setApellido(request.getApellido());
            usuario.setEmail(request.getEmail());
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                usuario.setContrasenia(request.getPassword()); // setContrasenia ya hashea automáticamente
            }

            usuario.setTelefono(request.getTelefono());
            if (request.getDireccion() != null) {
                usuario.setBarrio(request.getDireccion());
            }

            Usuario actualizado = usuarioService.actualizarUsuario(usuario);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar perfil: " + e.getMessage());
        }
    }
}
