package ttps.persistence.dao.impl;

import ttps.models.Mascota;
import ttps.persistence.dao.impl.generic.GenericDAOHibernateJPA;
import ttps.persistence.dao.interfaces.MascotaDAO;

public class MascotaDAOHibernateJPA extends GenericDAOHibernateJPA<Mascota> implements MascotaDAO {
    public MascotaDAOHibernateJPA() {
        super(Mascota.class);
    }
}
