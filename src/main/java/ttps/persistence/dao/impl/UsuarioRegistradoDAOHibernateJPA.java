package ttps.persistence.dao.impl;

import ttps.models.UsuarioRegistrado;
import ttps.persistence.dao.impl.generic.GenericDAOHibernateJPA;
import ttps.persistence.dao.interfaces.UsuarioRegistradoDAO;

public class UsuarioRegistradoDAOHibernateJPA extends GenericDAOHibernateJPA<UsuarioRegistrado> implements UsuarioRegistradoDAO {
    public UsuarioRegistradoDAOHibernateJPA() {
        super(UsuarioRegistrado.class);
    }
}
