package ttps.persistence.dao.impl;

import ttps.models.Usuario;
import ttps.persistence.dao.impl.generic.GenericDAOHibernateJPA;
import ttps.persistence.dao.interfaces.UsuarioDAO;

public class UsuarioDAOHibernateJPA extends GenericDAOHibernateJPA<Usuario> implements UsuarioDAO {
    public UsuarioDAOHibernateJPA() {
        super(Usuario.class);
    }
}
