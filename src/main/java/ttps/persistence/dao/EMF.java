// java
package ttps.persistence.dao;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EMF {
    private static EntityManagerFactory em;

    static {
        try {
            em = Persistence.createEntityManagerFactory("my-persistence-unit");
        } catch (Throwable t) {
            System.err.println("Error al crear EntityManagerFactory: " + t.getMessage());
            t.printStackTrace(); // muestra la causa real
            throw new ExceptionInInitializerError(t); // evita seguir con la app en estado inconsistente
        }
    }

    public static EntityManagerFactory getEMF() {
        if (em == null) {
            throw new IllegalStateException("EntityManagerFactory no inicializado. Revisa errores previos.");
        }
        return em;
    }

    private EMF() {}
}
