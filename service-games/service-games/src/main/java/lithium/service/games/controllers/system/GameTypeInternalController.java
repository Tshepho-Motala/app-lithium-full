package lithium.service.games.controllers.system;

import lithium.service.games.client.objects.GameType;
import lithium.service.games.client.system.GameTypeClient;
import lithium.service.games.services.GameTypeService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameTypeInternalController implements GameTypeClient {
    @Autowired
    private GameTypeService gameTypeService;

    @Autowired
    private ModelMapper mapper;

    @Override
    public List<GameType> getGameTypesForDomain(@PathVariable("domainName") String domainName) {
        List<lithium.service.games.data.entities.GameType> gameTypes = gameTypeService.findByDomain(domainName);
        return mapper.map(gameTypes, new TypeToken<List<GameType>>(){}.getType());
    }
}
