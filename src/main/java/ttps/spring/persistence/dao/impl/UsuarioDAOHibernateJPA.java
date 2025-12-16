package ttps.spring.persistence.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import ttps.spring.models.Usuario;
import ttps.spring.persistence.dao.impl.generic.GenericDAOHibernateJPA;
import ttps.spring.persistence.dao.interfaces.UsuarioDAO;

@Repository
public class UsuarioDAOHibernateJPA extends GenericDAOHibernateJPA<Usuario> implements UsuarioDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public UsuarioDAOHibernateJPA() {
        super(Usuario.class);
    }

    @Override
    public Usuario findByEmail(String email) {
        try {
            return entityManager.createQuery(
                    "SELECT u FROM Usuario u WHERE u.email = :email AND u.activo = true", Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Usuario findByNombreUsuario(String nombreUsuario) {
        try {
            return entityManager.createQuery(
                    "SELECT u FROM Usuario u WHERE u.nombreUsuario = :nombreUsuario AND u.activo = true", Usuario.class)
                    .setParameter("nombreUsuario", nombreUsuario)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
