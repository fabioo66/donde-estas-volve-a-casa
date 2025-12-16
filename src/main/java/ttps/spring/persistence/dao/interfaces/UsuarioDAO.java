package ttps.spring.persistence.dao.interfaces;

import ttps.spring.models.Usuario;
import ttps.spring.persistence.dao.interfaces.generic.GenericDAO;

public interface UsuarioDAO extends GenericDAO<Usuario> {
    // Buscar usuario por email (para login y validaciones)
    Usuario findByEmail(String email);

    // Buscar usuario por nombre de usuario (para validaciones)
    Usuario findByNombreUsuario(String nombreUsuario);
}
