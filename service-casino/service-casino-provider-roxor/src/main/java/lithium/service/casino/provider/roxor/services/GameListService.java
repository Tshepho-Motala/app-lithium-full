package lithium.service.casino.provider.roxor.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.modules.ModuleInfo;
import lithium.service.casino.provider.roxor.api.schema.gamelist.GameInternal;
import lithium.service.casino.provider.roxor.api.schema.gamelist.GameListInternal;
import lithium.service.casino.provider.roxor.config.ProviderConfig;
import lithium.service.casino.provider.roxor.config.ProviderConfigService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.games.client.objects.Game;
import lithium.service.games.client.objects.Label;
import lithium.util.ExceptionMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class GameListService {
    @Autowired ProviderConfigService providerConfigService;
    @Autowired ModuleInfo moduleInfo;
    @Autowired LithiumServiceClientFactory services;

    public List<Game> getGameList(String domainName) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ProviderConfig pc = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);
            String gameListUrl = "";
            if (pc.getGameListUrl() != null && !pc.getGameListUrl().trim().isEmpty()) {
                gameListUrl = pc.getGameListUrl();
            }
            log.info("Game list url: " + gameListUrl);
            GameListInternal gameListInternal = null;
            if (gameListUrl.trim().isEmpty()) {
                File resource = new ClassPathResource("roxor_gamelist.json").getFile();
                String gameListJsonString = new String(Files.readAllBytes(resource.toPath()));
                gameListInternal = parseData(gameListJsonString);
            } else {
                gameListInternal = restTemplate.getForObject(gameListUrl, GameListInternal.class);
            }
            log.info(gameListInternal.toString());

            List<Game> gameList = new ArrayList<Game>();
            for(GameInternal gameInternal : gameListInternal.getGameList()) {
                HashMap<String, Label> labels = new HashMap<>();
                if(gameInternal.getPlatform() !=null) {
                    labels.put("os", Label.builder().name("os").value(gameInternal.getPlatform()).domainName(domainName).build());
                }
                if(gameInternal.getCategory() != null) {
                    labels.put("category", Label.builder().name("category").value(gameInternal.getCategory()).domainName(domainName).build());
                }

                Game game = Game.builder()
                        .providerGameId(gameInternal.getId())
                        .providerGuid(moduleInfo.getModuleName())
                        .name(gameInternal.getName())
                        .freeSpinEnabled(gameInternal.getFreeSpinEnabled())
                        .freeSpinValueRequired(gameInternal.getFreeSpinValueRequired())
                        .labels(labels)
                        .build();
                gameList.add(game);
            }

            return gameList;
        } catch (Exception e) {
            log.error("gameList-Service " + ExceptionMessageUtil.allMessages(e), e);
            return null;
        }
    }

    private GameListInternal parseData(String input) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper.readValue(input, GameListInternal.class);
    }
}
