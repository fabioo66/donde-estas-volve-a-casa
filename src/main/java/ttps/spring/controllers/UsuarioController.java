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
import ttps.spring.dto.ActualizarPerfilRequest;
import ttps.spring.dto.LoginRequest;
import ttps.spring.dto.LoginResponse;
import ttps.spring.dto.RegistroUsuarioRequest;
import ttps.spring.models.Usuario;
import ttps.spring.models.UsuarioRegistrado;
import ttps.spring.services.UsuarioService;
import ttps.utils.PasswordUtils;
import ttps.utils.JwtUtils;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios y autenticación")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtUtils jwtUtils;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, JwtUtils jwtUtils) {
        this.usuarioService = usuarioService;
        this.jwtUtils = jwtUtils;
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
            Usuario existente = usuarioService.obtenerUsuarioPorEmail(request.getEmail());
            if (existente != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("El email ya esta registrado");
            }

            // Validar que el nombre de usuario no exista
            Usuario existenteNombre = usuarioService.obtenerUsuarioPorNombreUsuario(request.getNombreUsuario());
            if (existenteNombre != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("El nombre de usuario ya esta registrado");
            }

            UsuarioRegistrado usuario = new UsuarioRegistrado();
            usuario.setNombreUsuario(request.getNombreUsuario());
            usuario.setNombre(request.getNombre());
            usuario.setApellido(request.getApellido());
            usuario.setEmail(request.getEmail());
            usuario.setContrasenia(request.getPassword());
            usuario.setTelefono(request.getTelefono());
            usuario.setGenero(request.getGenero());
            usuario.setEdad(request.getEdad());
            usuario.setProvincia(request.getProvincia());
            usuario.setMunicipio(request.getMunicipio());
            usuario.setDepartamento(request.getDepartamento());

            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
        } catch (Exception e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();

            // Manejar error de constraint de unicidad (duplicate entry)
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("Duplicate entry")) {
                if (errorMessage.contains("nombre_usuario") || errorMessage.contains("UK_puhr3k3l7bj71hb7hk7ktpxn0")) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("El nombre de usuario ya esta registrado");
                } else if (errorMessage.contains("email")) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("El email ya esta registrado");
                }
            }

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

            // Generar token JWT
            String token = jwtUtils.generateToken(usuario);

            String tipo = usuario.getClass().getSimpleName().toUpperCase();
            LoginResponse response = new LoginResponse(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getEmail(),
                    tipo,
                    token // Incluir el token en la respuesta
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
            @ApiResponse(responseCode = "401", description = "Contraseña actual incorrecta"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> editarPerfil(
            @Parameter(description = "ID del usuario") @PathVariable Long id,
            @Parameter(description = "Datos actualizados del usuario") @RequestBody ActualizarPerfilRequest request) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(id);
            if (usuario == null) {
                System.out.println("Usuario no encontrado: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }

            // Si se está intentando cambiar la contraseña, validar la contraseña actual
            if (request.getPasswordNueva() != null && !request.getPasswordNueva().isEmpty()) {
                System.out.println("Procesando cambio de contraseña...");

                // Validar que se haya enviado la contraseña actual
                if (request.getPasswordActual() == null || request.getPasswordActual().isEmpty()) {
                    System.out.println("Error: No se proporcionó la contraseña actual");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Debe proporcionar la contraseña actual para cambiarla");
                }

                // Verificar que la contraseña actual sea correcta
                if (!PasswordUtils.verifyPassword(request.getPasswordActual(), usuario.getContrasenia())) {
                    System.out.println("Error: Contraseña actual incorrecta");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("La contraseña actual es incorrecta");
                }

                // Si la validación es correcta, actualizar la contraseña
                usuario.setContrasenia(request.getPasswordNueva());
                System.out.println("Contraseña validada y actualizada correctamente");
            }

            // Actualizar otros campos
            usuario.setNombreUsuario(request.getNombreUsuario());
            usuario.setNombre(request.getNombre());
            usuario.setApellido(request.getApellido());
            usuario.setEmail(request.getEmail());
            usuario.setTelefono(request.getTelefono());
            usuario.setGenero(request.getGenero());
            usuario.setEdad(request.getEdad());
            usuario.setProvincia(request.getProvincia());
            usuario.setMunicipio(request.getMunicipio());
            usuario.setDepartamento(request.getDepartamento());

            Usuario actualizado = usuarioService.actualizarUsuario(usuario);
            System.out.println("Perfil actualizado exitosamente para usuario: " + id);

            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            System.err.println("Error al actualizar perfil: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar perfil: " + e.getMessage());
        }
    }
}
