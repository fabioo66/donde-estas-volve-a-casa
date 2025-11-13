package ttps.spring.persistence.dao.impl.generic;

import jakarta.persistence.*;
import org.springframework.transaction.annotation.Transactional;
import ttps.spring.persistence.dao.interfaces.generic.GenericDAO;

import java.util.List;

@Transactional
public class GenericDAOHibernateJPA<T> implements GenericDAO<T> {

    @PersistenceContext
    private EntityManager entityManager;
    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }
    public EntityManager getEntityManager() {
        return this.entityManager;
    }
    @Override
    public T persist(T entity) {
        getEntityManager().persist(entity);
        return entity;
    }
    protected Class<T> persistentClass;
    public GenericDAOHibernateJPA(Class<T> clase) {
        this.persistentClass = clase;
    }
    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    @Override
    public void delete(T entity) {
        T managedEntity = this.getEntityManager().merge(entity);
        try {
            // Intentar con primitive boolean primero, luego con Boolean wrapper
            try {
                managedEntity.getClass().getMethod("setActivo", boolean.class).invoke(managedEntity, false);
            } catch (NoSuchMethodException ex) {
                managedEntity.getClass().getMethod("setActivo", Boolean.class).invoke(managedEntity, Boolean.FALSE);
            }
            // Asegurar que los cambios se sincronicen con la base de datos
            this.getEntityManager().flush();
        } catch (Exception e) {
            throw new RuntimeException("La entidad no tiene el método setActivo para borrado lógico o ocurrió un error", e);
        }
    }

    @Override
    public void delete(Long id) {
        T entity = get(id);
        if (entity != null) {
            delete(entity);
        }
    }

    @Override
    public T get(Long id) {
        T entity = this.getEntityManager().find(persistentClass, id);
        return entity;
    }

    @Override
    public List<T> getAll(String columnOrder) {
        String order = (columnOrder == null || columnOrder.isBlank()) ? "id" : columnOrder;
        String jpql = "SELECT e FROM " + getPersistentClass().getSimpleName() + " e ORDER BY e." + order;
        TypedQuery<T> query = getEntityManager().createQuery(jpql, getPersistentClass());
        return query.getResultList();
    }

    @Override
    public T update(T entity) {
        T entityMerged = this.getEntityManager().merge(entity);
        return entityMerged;
    }
}
