package ttps.utils;

import jakarta.persistence.EntityManager;
import ttps.persistence.dao.EMF;

public class DatabaseInitializer {

    public static void createTables() {
        System.out.println("🚀 Inicializando base de datos...");

        try {
            // Al obtener el EntityManagerFactory, Hibernate crea las tablas automáticamente
            EntityManager em = EMF.getEMF().createEntityManager();

            System.out.println("✅ EntityManager creado exitosamente");
            System.out.println("✅ Las tablas deberían haberse creado automáticamente");

            // Cerrar el EntityManager
            em.close();

            System.out.println("🎉 Base de datos inicializada correctamente");

        } catch (Exception e) {
            System.err.println("❌ Error al inicializar la base de datos:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        createTables();
    }
}
