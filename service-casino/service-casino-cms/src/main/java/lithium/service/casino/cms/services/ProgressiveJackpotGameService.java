package lithium.service.casino.cms.services;

import lithium.metrics.TimeThisMethod;
import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotGameBalance;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.games.client.ProgressiveJackpotFeedsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class ProgressiveJackpotGameService {

    @Autowired
    private LithiumServiceClientFactory services;

    @TimeThisMethod
    public Map<String, Set<String>> getGameGuidProgressiveIdListMap(String domainName) {
        List<ProgressiveJackpotGameBalance> gameGuidProgressiveList = getProgressiveJackpotFeedsClient().get()
                .getProgressiveGameBalances(domainName);
        Map<String, Set<String>> gameGuidProgressiveIdListMap = new HashMap<>();
        gameGuidProgressiveList.forEach(progressiveJackpotGameBalance -> {
            Set<String> progressiveIds = gameGuidProgressiveIdListMap.get(progressiveJackpotGameBalance.getGame().getGuid());
            if (progressiveIds == null) {
                progressiveIds = new HashSet<>();
                gameGuidProgressiveIdListMap.put(progressiveJackpotGameBalance.getGame().getGuid(), progressiveIds);
            }
            if (progressiveJackpotGameBalance.getGame().getGameSupplier() != null) {
                progressiveIds.add(progressiveJackpotGameBalance.getProgressiveId());
            }
        });
        return gameGuidProgressiveIdListMap;
    }

    private Optional<ProgressiveJackpotFeedsClient> getProgressiveJackpotFeedsClient() {
        return getClient(ProgressiveJackpotFeedsClient.class, "service-games");
    }

    private <E> Optional<E> getClient(Class<E> theClass, String url) {
        E clientInstance = null;

        try {
            clientInstance = services.target(theClass, url, true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error(e.getMessage(), e);
        }

        return Optional.ofNullable(clientInstance);
    }
}
