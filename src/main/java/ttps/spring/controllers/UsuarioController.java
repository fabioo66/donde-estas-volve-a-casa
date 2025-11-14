package ttps.spring.controllers;

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
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroUsuarioRequest request) {
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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
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
    public ResponseEntity<?> obtenerUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerUsuario(id);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarPerfil(@PathVariable Long id,
                                          @RequestBody RegistroUsuarioRequest request) {
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
