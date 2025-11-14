package ttps.spring.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import ttps.spring.models.Avistamiento;
import ttps.spring.persistence.dao.impl.generic.GenericDAOHibernateJPA;
import ttps.spring.persistence.dao.interfaces.AvistamientoDAO;

@Repository
public class AvistamientoDAOHibernateJPA extends GenericDAOHibernateJPA<Avistamiento> implements AvistamientoDAO {
    public AvistamientoDAOHibernateJPA() {
        super(Avistamiento.class);
    }
}
