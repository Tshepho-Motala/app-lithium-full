package lithium.service.entity.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.entity.data.entities.Entity;
import lithium.service.entity.data.entities.EntityType;
import lithium.service.entity.services.EntityService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/entities")
@Slf4j
public class EntitiesController {
	@Autowired EntityService entityService;
	
	@GetMapping("/table")
	public DataTableResponse<Entity> table( 
		DataTableRequest request, 
		LithiumTokenUtil tokenUtil,
		@RequestParam(name="entityTypeId", required=false) EntityType entityType
	) throws Exception {
		return entityService.table(tokenUtil.playerDomainWithRole("ENTITIES_MANAGE").getName(), request, entityType);
	}

	@PostMapping
	public Response<Entity> create(
		@RequestBody lithium.service.entity.client.objects.Entity entity,
		LithiumTokenUtil tokenUtil,
		Principal principal
	) throws Exception {
		Entity e = null;
		try {
			e = entityService.createEntity(tokenUtil.playerDomainWithRole("ENTITIES_MANAGE").getName(), entity, principal);
			return Response.<Entity>builder().data(e).status(OK).build();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return Response.<Entity>builder().data(e).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/findByUuid/{uuid}")
	public Response<Entity> findByUuid(@PathVariable("uuid") String uuid) {
		Entity e = null;
		try {
			e = entityService.findByUuid(uuid);
			return Response.<Entity>builder().data(e).status(OK).build();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return Response.<Entity>builder().data(e).status(INTERNAL_SERVER_ERROR).build();
		}
	}
}
