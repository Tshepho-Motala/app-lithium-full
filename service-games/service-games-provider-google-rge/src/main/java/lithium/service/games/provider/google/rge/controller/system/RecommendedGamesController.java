package lithium.service.games.provider.google.rge.controller.system;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.Response;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.games.provider.google.rge.client.objects.response.Recommendation;
import lithium.service.games.provider.google.rge.services.RecommendedGamesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/system/recommended-games")
@RestController
public class RecommendedGamesController {

    @Autowired
    private RecommendedGamesService recommendedGamesService;

    @PostMapping
    public List<Recommendation> getGameRecommendations(@RequestParam("userGuid") String userGuid) throws Status500InternalServerErrorException {
        try {
            return recommendedGamesService.getRecommendations(userGuid);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Status500InternalServerErrorException(e.getMessage(), e);
        }

    }

}
