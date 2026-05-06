package ttps.persistence.dao;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ttps.spring.Application;
import ttps.spring.models.tipo_mascota.Tipo_mascota;
import ttps.spring.models.tipo_mascota.DTO.Tipo_mascotaResponse;
import ttps.spring.services.TipoMascotaService;
import ttps.spring.models.tipo_mascota.Tipo_mascotaRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TipoMascotaDAOTest {

    @Autowired
    private TipoMascotaService tipoService;

    @Autowired
    private Tipo_mascotaRepository tipoRepo;

    private Tipo_mascota creado;

    @Test
    @Order(1)
    @DisplayName("CREATE - Crear TipoMascota y verificar normalización")
    public void testCreateTipo() {
        Tipo_mascotaResponse resp = tipoService.createTipoMascota("Perro");
        assertNotNull(resp);
        assertNotNull(resp.id());
        assertEquals("PERRO", resp.nombre());

        creado = tipoRepo.findById(resp.id()).orElse(null);
        assertNotNull(creado);

        System.out.println("✓ TipoMascota creado: " + resp.nombre());
    }

    @Test
    @Order(2)
    @DisplayName("READ ALL - Listar tipos existentes")
    public void testFindAll() {
        List<Tipo_mascotaResponse> all = tipoService.findAll();
        assertNotNull(all);
        assertTrue(all.stream().anyMatch(t -> t.id().equals(creado.getId())));
    }

    @Test
    @Order(3)
    @DisplayName("UPDATE - Actualizar nombre de tipo")
    public void testUpdateNombre() {
        Tipo_mascotaResponse updated = tipoService.updateNombreById(creado.getId(), "PerroModificado");
        assertNotNull(updated);
        assertEquals("PERROMODIFICADO", updated.nombre());
    }

    @Test
    @Order(4)
    @DisplayName("DELETE - Borrar tipo por id")
    public void testDelete() {
        tipoService.deleteById(creado.getId());
        assertFalse(tipoRepo.findById(creado.getId()).isPresent());
    }
}

