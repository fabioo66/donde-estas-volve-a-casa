package ttps.spring.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import ttps.spring.models.Mascota;
import ttps.spring.persistence.dao.impl.generic.GenericDAOHibernateJPA;
import ttps.spring.persistence.dao.interfaces.MascotaDAO;

@Repository
public class MascotaDAOHibernateJPA extends GenericDAOHibernateJPA<Mascota> implements MascotaDAO {
    public MascotaDAOHibernateJPA() {
        super(Mascota.class);
    }
}
