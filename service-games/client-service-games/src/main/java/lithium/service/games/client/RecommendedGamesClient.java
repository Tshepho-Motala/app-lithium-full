package lithium.service.games.client;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.games.client.objects.RecommendedGameBasic;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "service-games")
public interface RecommendedGamesClient {
    @RequestMapping("/system/recommended-games")
    List<RecommendedGameBasic> getRecommendedGamesBasics(@RequestParam("userGuid") String userGuid,
                                                         @RequestParam("liveCasino") Boolean liveCasino,
                                                         @RequestParam("channel") String channel,
                                                         @RequestParam(name = "locale", defaultValue = "en_US") String locale)
			throws LithiumServiceClientFactoryException, Status550ServiceDomainClientException,
			Status474DomainProviderDisabledException, Status500InternalServerErrorException;
}
