package lithium.service.casino.cms.api.controllers.backoffice;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.casino.cms.api.objects.LobbyRequest;
import lithium.service.casino.cms.services.LobbyService;
import lithium.service.casino.cms.services.UserService;
import lithium.service.casino.cms.storage.entities.Lobby;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.client.objects.User;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backoffice/{domainName}/lobbies")
@Slf4j
public class LobbiesController {
	@Autowired
	private LobbyService service;
	@Autowired
	private LithiumServiceClientFactory serviceFactory;

	@GetMapping("/lobby-exists")
	private Response<Boolean> lobbyExists(@PathVariable("domainName") String domainName) {
		return Response.<Boolean>builder().data(service.lobbyExists(domainName)).status(Response.Status.OK).build();
	}

	@GetMapping("/table")
	private DataTableResponse<Lobby> table(@PathVariable("domainName") String domainName, DataTableRequest request) throws Exception {
		Page<Lobby> lobbies = service.findLobbies(domainName, request.getPageRequest());
		return new DataTableResponse<>(request, lobbies);
	}

	@PostMapping("/add")
	private Response<Lobby> add(@PathVariable("domainName") String domainName, @RequestBody LobbyRequest request,
			LithiumTokenUtil tokenUtil) throws Status500InternalServerErrorException {
		return Response.<Lobby>builder()
			.data(service.add(domainName, tokenUtil.guid(), request.getDescription(), request.getJson()))
			.status(Response.Status.OK)
			.build();
	}
}
