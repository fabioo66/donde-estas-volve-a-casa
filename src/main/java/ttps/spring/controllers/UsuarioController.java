package ttps.spring.controllers;

// Spring Framework & Security
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

// OpenAPI / Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ttps.spring.models.usuario.dto.*;
import ttps.spring.models.usuario.Usuario;
import ttps.spring.services.UsuarioService;
import ttps.spring.security.JwtUtils;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "API para la gestión de usuarios y autenticación")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

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
            @Parameter(description = "Datos del nuevo usuario")
            @Valid @RequestBody RegistroUsuarioRequest request) {
            Usuario usuarioCreado = usuarioService.crearUsuario(request);
            UsuarioResponse userResponse = new UsuarioResponse(
                    usuarioCreado.getNombre(),
                    usuarioCreado.getApellido(),
                    usuarioCreado.getEmail(),
                    usuarioCreado.getNombreUsuario()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
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
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.contrasenia()
                        )
                );

        Usuario usuario = (Usuario) authentication.getPrincipal();

        String token = jwtUtils.generateToken(usuario);

        return ResponseEntity.ok(new LoginResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRol().name(),
                token,
                usuario.getNombre(),
                usuario.getApellido()
        ));
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

        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
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
            @Parameter(description = "Datos actualizados del usuario") @RequestBody UsuarioUpdateRequest request) {
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuarioActual = usuarioService.obtenerUsuario(id);

        if (!usuarioActual.getEmail().equals(emailAutenticado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para editar este perfil");
        }

        Usuario actualizado = usuarioService.actualizarUsuario(id, request);

        return ResponseEntity.ok(actualizado);
    }
}
