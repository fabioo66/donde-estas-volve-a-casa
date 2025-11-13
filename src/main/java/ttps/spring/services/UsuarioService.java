package ttps.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.models.Usuario;
import ttps.spring.persistence.dao.interfaces.UsuarioDAO;

import java.util.List;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    @Autowired
    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

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
}