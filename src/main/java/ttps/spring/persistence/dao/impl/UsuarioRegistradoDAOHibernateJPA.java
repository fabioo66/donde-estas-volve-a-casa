package ttps.spring.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import ttps.spring.models.UsuarioRegistrado;
import ttps.spring.persistence.dao.impl.generic.GenericDAOHibernateJPA;
import ttps.spring.persistence.dao.interfaces.UsuarioRegistradoDAO;

@Repository
public class UsuarioRegistradoDAOHibernateJPA extends GenericDAOHibernateJPA<UsuarioRegistrado> implements UsuarioRegistradoDAO {
    public UsuarioRegistradoDAOHibernateJPA() {
        super(UsuarioRegistrado.class);
    }
}
