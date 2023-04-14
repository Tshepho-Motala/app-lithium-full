package lithium.service.entity.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.entity.client.objects.AddressBasic;
import lithium.service.entity.data.entities.Entity;
import lithium.service.entity.services.EntityService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/entity/{id}")
public class EntityController {
	@Autowired EntityService entityService;
	@Autowired ChangeLogService changelogService;
	
	@GetMapping
	public Response<Entity> get(
		@PathVariable("id") Entity entity,
		Authentication authentication
	) {
		return Response.<Entity>builder().data(entity).status(OK).build();
	}
	
	@PutMapping
	public Response<Entity> save(
		@PathVariable("id") Entity entity,
		@RequestBody lithium.service.entity.client.objects.Entity entityPost,
		Principal principal
	) throws Exception {
		Entity e = null;
		try {
			e = entityService.saveEntity(entity, entityPost, principal);
			return Response.<Entity>builder().data(e).status(OK).build();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return Response.<Entity>builder().data(e).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping(value="/savebankdetails")
	public Response<Entity> saveBankDetails(
		@PathVariable("id") Entity entity,
		@RequestBody lithium.service.entity.client.objects.Entity entityPost,
		Principal principal
	) {
		Entity e = null;
		try {
			e = entityService.saveBankDetails(entity, entityPost, principal);
			return Response.<Entity>builder().data(e).status(OK).build();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return Response.<Entity>builder().data(e).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping(value="/savestatus")
	public Response<Entity> saveStatus(
		@RequestBody lithium.service.entity.client.objects.StatusUpdate statusUpdate,
		Principal principal
	) throws Exception {
		Entity e = null;
		try {
			e = entityService.saveStatus(statusUpdate, principal);
			return Response.<Entity>builder().status(OK).data(e).build();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return Response.<Entity>builder().status(INTERNAL_SERVER_ERROR).data(e).build();
		}
	}
	
	@PostMapping(value="/saveaddress")
	public Response<Entity> saveAddress(
		@RequestBody AddressBasic addressBasic,
		Principal principal
	) {
		Entity e = null;
		try {
			e = entityService.saveAddress(addressBasic, principal);
			return Response.<Entity>builder().status(OK).data(e).build();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return Response.<Entity>builder().status(INTERNAL_SERVER_ERROR).data(e).build();
		}
	}
	
	@GetMapping(value = "/changelogs")
	private @ResponseBody Response<ChangeLogs> changeLogs(
		@PathVariable Long id,
		@RequestParam int p
	) throws Exception {
		return changelogService.listLimited(
			ChangeLogRequest.builder()
				.entityRecordId(id)
				.entities(new String[] { "entity" })
				.page(p)
				.build()
		);
	}
}
