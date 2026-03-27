package ttps.spring.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.infra.EmailYaRegistradoException;
import ttps.spring.infra.UsuarioYaRegistradoException;
import ttps.spring.infra.ValidacionException;
import ttps.spring.models.usuario.Usuario;
import ttps.spring.models.usuario.UsuarioRepository;
import ttps.spring.infra.UsuarioNoEncontradoException;
import ttps.spring.models.usuario.dto.RegistroUsuarioRequest;
import ttps.spring.models.usuario.dto.UsuarioUpdateRequest;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario crearUsuario(RegistroUsuarioRequest request) {
        boolean existe = this.existeUsuarioPorEmail(request.email());
        if (existe) {
            throw new EmailYaRegistradoException("El email " + request.email() + " ya está registrado");
        }
        boolean existeNombreUsuario = usuarioRepository.findByNombreUsuario(request.nombreUsuario()).isPresent();
        if (existeNombreUsuario) {
            throw new UsuarioYaRegistradoException("El nombre de usuario " + request.nombreUsuario() + " ya está registrado");
        }
        String passwordHasheada = passwordEncoder.encode(request.password());
        Usuario usuario = new Usuario(
                request.nombre(),
                request.apellido(),
                request.email(),
                request.nombreUsuario(),
                passwordHasheada,
                request.telefono(),
                request.genero(),
                request.edad(),
                request.provincia(),
                request.municipio(),
                request.departamento()
        );
        // Persistir para que tenga ID antes de ser usado por otros servicios/tests
        return usuarioRepository.save(usuario);
    }

    public Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(UsuarioNoEncontradoException::new);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> obtenerTodosLosUsuariosActivos() {
        return usuarioRepository.findAllByActivoTrue();
    }

    @Transactional
    public Usuario actualizarUsuario(Long id, UsuarioUpdateRequest request) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNoEncontradoException::new);

        if (request.nombre() != null) {
            usuario.setNombre(request.nombre());
        }

        if (request.apellido() != null) {
            usuario.setApellido(request.apellido());
        }

        if (request.email() != null) {
            if (!request.email().equals(usuario.getEmail())) {
                boolean existe = usuarioRepository.existsByEmail(request.email());
                if (existe) {
                    throw new EmailYaRegistradoException("El email " + request.email() + " ya está en uso");
                }
            }
            usuario.setEmail(request.email());
        }

        if (request.nombreUsuario() != null && !request.nombreUsuario().equals(usuario.getNombreUsuario())) {
            boolean existeNombreUsuario = usuarioRepository.existsByNombreUsuario(request.nombreUsuario());
            if (existeNombreUsuario) {
                throw new UsuarioYaRegistradoException("El nombre de usuario " + request.nombreUsuario() + " ya está en uso");
            }
            usuario.setNombreUsuario(request.nombreUsuario());
        }

        if (request.password() != null && !request.password().isBlank()) {
            if (passwordEncoder.matches(request.password(), usuario.getPassword())) {
                throw new ValidacionException("La nueva contraseña no puede ser igual a la anterior");
            }
            String passwordEncriptada = passwordEncoder.encode(request.password());
            usuario.cambiarContrasenia(passwordEncriptada);
        }

        if (request.telefono() != null && !request.telefono().equals(usuario.getTelefono())) {
            if (!request.telefono().matches("\\d{10}")) {
                throw new ValidacionException("El número de teléfono debe contener exactamente 10 dígitos");
            }
            boolean existe = usuarioRepository.existsByTelefono(request.telefono());
            if (existe) {
                throw new ValidacionException("El número de teléfono " + request.telefono() + " ya está en uso");
            }

            usuario.setTelefono(request.telefono());
        }

        if (request.genero() != null) {
            usuario.setGenero(request.genero());
        }

        if (request.edad() != null) {
            usuario.setEdad(request.edad());
        }

        if (request.provincia() != null) {
            usuario.setProvincia(request.provincia());
        }

        if (request.municipio() != null) {
            usuario.setMunicipio(request.municipio());
        }

        if (request.departamento() != null) {
            usuario.setDepartamento(request.departamento());
        }

        return usuario;
    }

    @Transactional
    public void eliminarUsuario(Long id) { //Borrado logico
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNoEncontradoException::new);
        usuario.setActivo(false);
    }

    @Transactional
    public void eliminarUsuario(String usuario) {
        Usuario usuarioEncontrado = usuarioRepository.findByNombreUsuario(usuario)
                .orElseThrow(UsuarioNoEncontradoException::new);
        this.eliminarUsuario(usuarioEncontrado.getId());
    }

    public boolean existeUsuarioPorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario obtenerUsuarioPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("No se encontro el usuario con nombre de usuario: " + nombreUsuario));
    }


    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(UsuarioNoEncontradoException::new);
    }
}