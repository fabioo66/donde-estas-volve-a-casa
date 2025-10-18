package ttps.persistence.dao.impl;

import ttps.models.Administrador;
import ttps.persistence.dao.impl.generic.GenericDAOHibernateJPA;
import ttps.persistence.dao.interfaces.AdministradorDAO;

public class AdministradorDAOHibernateJPA extends GenericDAOHibernateJPA<Administrador> implements AdministradorDAO {
    public AdministradorDAOHibernateJPA() {
        super(Administrador.class);
    }
}
