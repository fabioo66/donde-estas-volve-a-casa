package ttps.persistence;

import ttps.persistence.dao.impl.UsuarioDAOHibernateJPA;

public class FactoryDAO {
    public static UsuarioDAOHibernateJPA getUsuarioDAO() {
        return new UsuarioDAOHibernateJPA();
    }

}
