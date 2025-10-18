package ttps.persistence.dao.impl;

import ttps.models.Avistamiento;
import ttps.persistence.dao.impl.generic.GenericDAOHibernateJPA;
import ttps.persistence.dao.interfaces.AvistamientoDAO;

public class AvistamientoDAOHibernateJPA extends GenericDAOHibernateJPA<Avistamiento> implements AvistamientoDAO {
    public AvistamientoDAOHibernateJPA() {
        super(Avistamiento.class);
    }
}
