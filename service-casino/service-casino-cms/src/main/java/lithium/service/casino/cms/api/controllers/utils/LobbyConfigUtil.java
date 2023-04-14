package lithium.service.casino.cms.api.controllers.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.casino.cms.api.schema.lobby.v1.LobbyItem;
import lithium.service.casino.cms.api.schema.lobby.v1.User;
import lithium.service.casino.cms.storage.entities.Lobby;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LobbyConfigUtil {

    public static Map<String, List<LobbyItem>> mapLobbyConfigsByPrimaryNavCode(Lobby lobby) throws IOException {
        Map<String, List<LobbyItem>> lobbyConfigsByPrimaryNavCode = new LinkedHashMap<>();

        ObjectMapper om = new ObjectMapper();

        LobbyItem[] configs = om.readValue(lobby.getCurrent().getJson(), LobbyItem[].class);
        for (LobbyItem config: configs) {
            if (config.getPage() == null || config.getPage().getChannel() == null) continue;
            String key = config.getPage().getPrimaryNavCode()  + "@" + config.getPage().getChannel();
            if (lobbyConfigsByPrimaryNavCode.get(key) == null) {
                List<LobbyItem> tempConfigs = new ArrayList<>();
                tempConfigs.add(config);
                lobbyConfigsByPrimaryNavCode.put(key, tempConfigs);
            } else {
                List<LobbyItem> tempConfigs = lobbyConfigsByPrimaryNavCode.get(key);
                tempConfigs.add(config);
            }
        }
        return lobbyConfigsByPrimaryNavCode;
    }

    public static List<lithium.service.casino.cms.api.schema.lobby.v1.Lobby> convertToV1Lobbies(Page<Lobby> lobbies) throws IOException {
        List<lithium.service.casino.cms.api.schema.lobby.v1.Lobby> lobbiesResponse = new ArrayList<>();
        for (lithium.service.casino.cms.storage.entities.Lobby lobbyEntity: lobbies) {
            Map<String, List<LobbyItem>> lobbyMapByPrimaryNav = mapLobbyConfigsByPrimaryNavCode(lobbyEntity);
            for (String key: lobbyMapByPrimaryNav.keySet()) {
                List<LobbyItem> lobbyItems = lobbyMapByPrimaryNav.get(key);
                lithium.service.casino.cms.api.schema.lobby.v1.Lobby lobby = new lithium.service.casino.cms.api.schema.lobby.v1.Lobby();
                ObjectMapper om = new ObjectMapper();
                lobby.setId(lobbyEntity.getId());
                lobby.setModifiedBy(om.convertValue(lobbyEntity.getCurrent().getCreatedBy(), User.class));
                lobby.setModifiedDate(lobbyEntity.getCurrent().getCreatedDate());
                lobby.setDescription(lobbyEntity.getCurrent().getDescription());
                lobby.setLobbyItems(lobbyItems);
                if (lobbyItems != null && lobbyItems.size() > 0) {
                    if (lobbyItems.get(0).getName() != null) {
                        lobby.setName(lobbyItems.get(0).getName());
                    } else {
                        // Currently they are not using the new CMS to create lobbies which means lobbies will not have names
                        // We use the primary_nav_code as a name
                        String newLobbyName = lobbyItems.get(0).getPage().getPrimaryNavCode();
                        lobby.setName(newLobbyName);
                        lobby.getLobbyItems().forEach(lobbyItem -> lobbyItem.setName(newLobbyName));
                    }
                }
                lobby.setVersion(lobbyEntity.getVersion());
                lobbiesResponse.add(lobby);
            }
        }
        return lobbiesResponse;
    }

}
