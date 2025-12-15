package ttps.spring.persistence.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ttps.spring.models.Estado;
import ttps.spring.models.Mascota;
import ttps.spring.persistence.dao.impl.generic.GenericDAOHibernateJPA;
import ttps.spring.persistence.dao.interfaces.MascotaDAO;

import java.util.List;

@Repository
public class MascotaDAOHibernateJPA extends GenericDAOHibernateJPA<Mascota> implements MascotaDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public MascotaDAOHibernateJPA() {
        super(Mascota.class);
    }

    @Override
    public List<Mascota> findByUsuarioActivas(Long usuarioId) {
        return entityManager.createQuery(
                "SELECT m FROM Mascota m WHERE m.usuario.id = :usuarioId AND m.activo = true", Mascota.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    @Override
    public List<Mascota> findByUsuario(Long usuarioId) {
        return entityManager.createQuery(
                "SELECT m FROM Mascota m WHERE m.usuario.id = :usuarioId", Mascota.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    @Override
    public List<Mascota> findMascotasPerdidas() {
        return entityManager.createQuery(
                "SELECT m FROM Mascota m WHERE (m.estado = :perdidoPropio OR m.estado = :perdidoAjeno) AND m.activo = true",
                Mascota.class)
                .setParameter("perdidoPropio", Estado.PERDIDO_PROPIO)
                .setParameter("perdidoAjeno", Estado.PERDIDO_AJENO)
                .getResultList();
    }
}
