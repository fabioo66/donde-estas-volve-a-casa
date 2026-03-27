package ttps.persistence.dao;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ttps.spring.Application;
import ttps.spring.models.raza.DTO.RazaRequest;
import ttps.spring.models.raza.DTO.RazaResponse;
import ttps.spring.models.raza.Raza;
import ttps.spring.models.raza.RazaRepository;
import ttps.spring.models.tipo_mascota.Tipo_mascota;
import ttps.spring.models.tipo_mascota.Tipo_mascotaRepository;
import ttps.spring.services.RazaService;
import ttps.spring.infra.RecursoNoEncontradoException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RazaDAOTest {

	@Autowired
	private RazaService razaService;

	@Autowired
	private Tipo_mascotaRepository tipoRepo;

	@Autowired
	private RazaRepository razaRepo;

	private Tipo_mascota tipo;
	private RazaResponse razaResp;

	@BeforeAll
	public void initTipo() {
		tipo = tipoRepo.save(new Tipo_mascota("Gato"));
	}

	@Test
	@Order(1)
	@DisplayName("CREATE - Crear Raza y normalizar nombre")
	public void testCreateRaza() {
		Raza r = razaService.findOrCreateByNombreAndTipoId("Siámes", tipo.getId());
		assertNotNull(r);
		assertEquals("SIAMES", r.getNombreNormalizado());
		razaResp = RazaResponse.from(r);
	}

	@Test
	@Order(2)
	@DisplayName("READ - Obtener Raza por ID")
	public void testGetRaza() {
		RazaResponse got = razaService.getById(razaResp.id());
		assertNotNull(got);
		assertEquals(razaResp.id(), got.id());
	}

	@Test
	@Order(3)
	@DisplayName("LIST BY TIPO - Listar razas por tipo")
	public void testListByTipo() {
		List<RazaResponse> list = razaService.listByTipoId(tipo.getId());
		assertNotNull(list);
		assertTrue(list.stream().anyMatch(r -> r.id().equals(razaResp.id())));
	}

	@Test
	@Order(4)
	@DisplayName("UPDATE - Actualizar nombre de raza y verificar normalizado")
	public void testUpdateRaza() {
		RazaRequest req = new RazaRequest(razaResp.id(), "Siames Modificado", tipo.getId());
		RazaResponse updated = razaService.updateRaza(razaResp.id(), req);
		assertNotNull(updated);

		Raza entidad = razaRepo.findById(razaResp.id()).orElseThrow();
		assertEquals("SIAMES MODIFICADO", entidad.getNombreNormalizado());
	}

	@Test
	@Order(5)
	@DisplayName("DELETE - Borrar raza por id y comprobar ausencia")
	public void testDeleteRaza() {
		razaService.deleteRaza(razaResp.id());
		Assertions.assertThrows(RecursoNoEncontradoException.class, () -> razaService.getById(razaResp.id()));
	}
}


