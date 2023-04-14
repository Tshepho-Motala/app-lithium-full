package lithium.service.casino.cms.services;

import lithium.service.Response;
import lithium.service.casino.cms.storage.entities.Lobby;
import lithium.service.casino.cms.storage.entities.LobbyRevision;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.objects.User;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: app-lithium
 * User: Tapiwanashe Shoshore
 * Date: 10/25/2021
 * Time: 4:03 PM
 * Created with IntelliJ IDEA
 */
public class UserService {

    public static void setLobbyRevisionsByGuids(Page<Lobby> lobbies, List<User> usersByGuids, LobbyService lobbyService) {
        List<LobbyRevision> lobbyRevisions =new ArrayList<>();
        for (Lobby lobby : lobbies) {
            LobbyRevision lobbyRevision =lobby.getCurrent();
            LobbyService.setGuidsByRevision(lobbyRevision);
            lobbyRevisions.add(lobbyRevision);
        }
        lobbyService.setLobbyRevisionUserData(usersByGuids, lobbyRevisions);
    }

    public static List<User> getUsers(Page<Lobby> lobbies, LithiumServiceClientFactory serviceFactory) throws Exception {
        List<String> guids =new ArrayList<>();
        for (Lobby lobby : lobbies) {
            LobbyRevision lobbyRevision =lobby.getCurrent();
            LobbyService.setGuidsByRevision(lobbyRevision);
            guids.addAll(LobbyService.setGuidsByRevision(lobbyRevision));
        }

        UserApiInternalClient client = serviceFactory.target(UserApiInternalClient.class, true);
        Response<List<User>> response = client.getUsers(guids);
        return response.getData();
    }
}
