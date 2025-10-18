package ttps.persistence;

import ttps.persistence.dao.impl.*;
import ttps.persistence.dao.interfaces.AdministradorDAO;
import ttps.persistence.dao.interfaces.UsuarioRegistradoDAO;

public class FactoryDAO {
    public static UsuarioDAOHibernateJPA getUsuarioDAO() {
        return new UsuarioDAOHibernateJPA();
    }

    public static MascotaDAOHibernateJPA getMascotaDAO() {
        return new MascotaDAOHibernateJPA();
    }

    public static AvistamientoDAOHibernateJPA getAvistamientoDAO() {
        return new AvistamientoDAOHibernateJPA();
    }
    
    public static UsuarioRegistradoDAO getUsuarioRegistradoDAO() {
        return new UsuarioRegistradoDAOHibernateJPA();
    }

    public static AdministradorDAO getAdministradorDAO() {
        return new AdministradorDAOHibernateJPA();
    }
}
