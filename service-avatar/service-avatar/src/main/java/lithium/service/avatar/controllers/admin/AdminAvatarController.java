package lithium.service.avatar.controllers.admin;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.avatar.data.entities.Avatar;
import lithium.service.avatar.services.AvatarService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/avatar/{domainName}")
@Slf4j
public class AdminAvatarController {
	@Autowired AvatarService service;
	
	@GetMapping("/table")
	public DataTableResponse<Avatar> avatarTable(
		@PathVariable("domainName") String domainName,
		LithiumTokenUtil tokenUtil,
		DataTableRequest request
	) {
		Page<Avatar> table = service.findByDomain(domainName,
				request.getSearchValue(), request.getPageRequest(), tokenUtil);
		return new DataTableResponse<>(request, table);
	}
	
	@PostMapping("/add")
	public Response<Avatar> add(@PathVariable("domainName") String domainName, @RequestBody lithium.service.avatar.client.objects.Avatar avatarPost) {
		Avatar avatar = null;
		try {
			avatar = service.add(domainName, avatarPost);
			return Response.<Avatar>builder().data(avatar).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Avatar>builder().data(avatar).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/view/{avatarId}")
	public Response<Avatar> view(@PathVariable("domainName") String domainName, @PathVariable("avatarId") Avatar avatar) {
		return Response.<Avatar>builder().data(avatar).status(OK).build();
	}
	
	@DeleteMapping("/delete/{id}")
	public Response<Boolean> delete(@PathVariable("domainName") String domainName, @PathVariable("id") Long avatarId) {
		try {
			service.delete(domainName, avatarId);
			return Response.<Boolean>builder().data(true).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Boolean>builder().data(false).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/toggleEnable/{id}")
	public Response<Avatar> toggleEnable(@PathVariable("domainName") String domainName, @PathVariable("id") Avatar avatar) {
		try {
			avatar = service.toggleEnable(domainName, avatar);
			return Response.<Avatar>builder().data(avatar).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Avatar>builder().data(avatar).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/setAsDefault/{id}")
	public Response<Avatar> setAsDefault(@PathVariable("domainName") String domainName, @PathVariable("id") Avatar avatar) {
		try {
			avatar = service.setAsDefault(domainName, avatar);
			return Response.<Avatar>builder().data(avatar).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Avatar>builder().data(avatar).status(INTERNAL_SERVER_ERROR).build();
		}
	}
}
