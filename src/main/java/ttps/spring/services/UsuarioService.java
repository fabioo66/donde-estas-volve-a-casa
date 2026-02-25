package ttps.spring.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.models.usuario.Usuario;
import ttps.spring.models.usuario.UsuarioRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public Usuario crearUsuario(Usuario usuario) {
        return usuarioDAO.persist(usuario);
    }

    public Usuario obtenerUsuario(Long id) {
        return usuarioDAO.get(id);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioDAO.getAll("id");
    }

    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioDAO.update(usuario);
    }

    public void eliminarUsuario(Long id) {
        usuarioDAO.delete(id);
    }

    public void eliminarUsuario(Usuario usuario) {
        usuarioDAO.delete(usuario);
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioDAO.findByEmail(email);
    }

    public Usuario obtenerUsuarioPorNombreUsuario(String nombreUsuario) {
        return usuarioDAO.findByNombreUsuario(nombreUsuario);
    }
}