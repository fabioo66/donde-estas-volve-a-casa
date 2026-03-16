package ttps.spring.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import ttps.spring.models.Administrador;
import ttps.spring.persistence.dao.impl.generic.GenericDAOHibernateJPA;
import ttps.spring.persistence.dao.interfaces.AdministradorDAO;

@Repository
public class AdministradorDAOHibernateJPA extends GenericDAOHibernateJPA<Administrador> implements AdministradorDAO {
    public AdministradorDAOHibernateJPA() {
        super(Administrador.class);
    }
}
