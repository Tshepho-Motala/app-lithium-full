package lithium.service.avatar.controllers;

import static lithium.service.Response.Status.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.avatar.data.entities.Avatar;
import lithium.service.avatar.services.AvatarService;

@RestController
@RequestMapping("/avatar/{domainName}")
public class AvatarController {
	@Autowired AvatarService service;
	
	@GetMapping("/all")
	public Response<List<Avatar>> all(@PathVariable("domainName") String domainName) {
		return Response.<List<Avatar>>builder().data(service.findEnabledByDomain(domainName)).status(OK).build();
	}
	
	@GetMapping("/getImage/{avatarId}")
	public ResponseEntity<byte[]> getAvatarImage(@PathVariable("domainName") String domainName, @PathVariable("avatarId") Long avatarId) {
		return service.getAvatarImageAsResponseEntity(avatarId);
	}
}
