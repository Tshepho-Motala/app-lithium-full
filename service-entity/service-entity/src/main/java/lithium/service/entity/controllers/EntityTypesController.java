package lithium.service.entity.controllers;

import static lithium.service.Response.Status.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.entity.data.entities.Domain;
import lithium.service.entity.data.entities.EntityType;
import lithium.service.entity.data.repositories.EntityTypeRepository;
import lithium.service.entity.services.DomainService;
import lithium.tokens.LithiumTokenUtil;

@RestController
@RequestMapping("/entitytypes")
public class EntityTypesController {
	@Autowired private DomainService domainService;
	@Autowired private EntityTypeRepository repo;

	@GetMapping
	public Response<List<EntityType>> list(LithiumTokenUtil tokenUtil) throws Exception {
		Domain domain = domainService.findOrCreate(tokenUtil.playerDomainWithRole("ENTITIES_MANAGE").getName());
		List<EntityType> list = repo.findByDomainOrderByName(domain);
		if (list.size() == 0) {
			repo.save(EntityType.builder().domain(domain).name("LOCATION_OWNER").description("Location Owner").build());
			repo.save(EntityType.builder().domain(domain).name("MACHINE_OWNER").description("Machine Owner").build());
			repo.save(EntityType.builder().domain(domain).name("AFFILIATE").description("Affiliate").build());
			list = repo.findByDomainOrderByName(domain);
		}
//		try { Thread.sleep(5000); } catch(Exception e) {};
		return Response.<List<EntityType>>builder().data(list).status(OK).build();
	}

}
