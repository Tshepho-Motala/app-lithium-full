package lithium.service.games.provider.google.rge.client;

import lithium.service.Response;
import lithium.service.games.provider.google.rge.client.objects.response.Recommendation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("service-games-provider-google-rge")
public interface RecommendedGamesGoogleRgeClient {

    @RequestMapping(value = "/system/recommended-games", method = RequestMethod.POST)
    public List<Recommendation> getGameRecommendation(@RequestParam("userGuid") String userGuid);

}
