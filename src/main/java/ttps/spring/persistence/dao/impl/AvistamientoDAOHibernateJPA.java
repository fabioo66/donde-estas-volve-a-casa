package ttps.spring.persistence.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ttps.spring.models.Avistamiento;
import ttps.spring.persistence.dao.impl.generic.GenericDAOHibernateJPA;
import ttps.spring.persistence.dao.interfaces.AvistamientoDAO;

import java.util.List;

@Repository
public class AvistamientoDAOHibernateJPA extends GenericDAOHibernateJPA<Avistamiento> implements AvistamientoDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public AvistamientoDAOHibernateJPA() {
        super(Avistamiento.class);
    }

    @Override
    public List<Avistamiento> findByMascotaId(Long mascotaId) {
        return entityManager.createQuery(
                "SELECT a FROM Avistamiento a WHERE a.mascota.id = :mascotaId ORDER BY a.fecha DESC",
                Avistamiento.class)
                .setParameter("mascotaId", mascotaId)
                .getResultList();
    }

    @Override
    public int contarAvistamientosPendientes() {
        // Contamos todos los avistamientos activos (asumimos que no hay campo de estado en avistamiento)
        // Si hay un campo activo, usaríamos: WHERE a.activo = true
        return Math.toIntExact((Long) entityManager.createQuery(
                "SELECT COUNT(a) FROM Avistamiento a")
                .getSingleResult());
    }
}
