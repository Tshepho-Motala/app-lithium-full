package lithium.service.avatar.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.avatar.data.entities.Avatar;
import lithium.service.avatar.data.entities.UserAvatar;
import lithium.service.avatar.services.UserAvatarService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/useravatar/{domainName}/{userName}")
@Slf4j
public class UserAvatarController {
	@Autowired UserAvatarService service;
	
	@GetMapping
	public Response<UserAvatar> get(@PathVariable("domainName") String domainName, @PathVariable("userName") String userName) {
		return Response.<UserAvatar>builder().data(service.findByUserGuid(domainName, userName)).status(OK).build();
	}
	
	@PostMapping("/{avatarId}")
	public Response<UserAvatar> set(@PathVariable("domainName") String domainName, @PathVariable("userName") String userName, @PathVariable("avatarId") Avatar avatar, LithiumTokenUtil tokenUtil) {
		UserAvatar userAvatar = null;
		try {
			userAvatar = service.setUserAvatar(domainName+"/"+userName, avatar, tokenUtil);
			return Response.<UserAvatar>builder().data(userAvatar).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<UserAvatar>builder().data(userAvatar).status(INTERNAL_SERVER_ERROR).build();
		}
	}
}
