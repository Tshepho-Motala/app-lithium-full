package lithium.service.casino.cms.api.controllers.backoffice;

import lithium.service.casino.cms.api.controllers.utils.LobbyConfigUtil;
import lithium.service.casino.cms.services.LobbyService;
import lithium.service.casino.cms.api.schema.lobby.v1.Lobby;
import lithium.service.casino.cms.storage.entities.LobbyRevision;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("backoffice/{domainName}/v1")
@Slf4j
public class LobbiesV1Controller {

    @Autowired
    private LobbyService service;

    @GetMapping("/lobbies")
    public List<Lobby> findLobbies(@PathVariable String domainName,
                                           @RequestParam(required = false, defaultValue = "0") Integer page,
                                           @RequestParam(required = false, defaultValue = "20") Integer size) throws Exception {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<lithium.service.casino.cms.storage.entities.Lobby> lobbies = service.findLobbies(domainName, pageRequest);
        return LobbyConfigUtil.convertToV1Lobbies(lobbies);
    }
}
