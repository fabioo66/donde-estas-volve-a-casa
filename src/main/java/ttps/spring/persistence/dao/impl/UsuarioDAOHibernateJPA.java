package ttps.spring.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import ttps.spring.models.Usuario;
import ttps.spring.persistence.dao.impl.generic.GenericDAOHibernateJPA;
import ttps.spring.persistence.dao.interfaces.UsuarioDAO;

@Repository
public class UsuarioDAOHibernateJPA extends GenericDAOHibernateJPA<Usuario> implements UsuarioDAO {
    public UsuarioDAOHibernateJPA() {
        super(Usuario.class);
    }
}
