package lithium.service.games.services;

import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.games.data.entities.GameSupplier;
import lithium.service.games.data.entities.progressivejackpotfeeds.Module;
import lithium.service.games.data.entities.progressivejackpotfeeds.ProgressiveJackpotFeed;
import lithium.service.games.data.repositories.ModuleRepository;
import lithium.service.games.data.repositories.ProgressiveJackpotBalanceRepository;
import lithium.service.games.data.repositories.ProgressiveJackpotFeedRepository;
import lithium.service.games.data.repositories.ProgressiveJackpotGameBalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ProgressiveJackpotFeedService {
    @Autowired private CachingDomainClientService cachingDomainClientService;
    @Autowired private GameSupplierService gameSupplierService;
    @Autowired private ModuleRepository moduleRepository;
    @Autowired private ProgressiveJackpotFeedRepository progressiveJackpotFeedRepository;

    public Page<ProgressiveJackpotFeed> getAllRegisteredProgressiveFeedsByDomain(String domainName, Pageable pageable) {
        return progressiveJackpotFeedRepository.findByGameSupplierDomainName(domainName, pageable);
    }

    public List<ProgressiveJackpotFeed> getAllEnabledProgressiveJackpotFeeds() {
        return progressiveJackpotFeedRepository.findAllByEnabledIsTrue();
    }

    public ProgressiveJackpotFeed getRegisteredFeedById(Long id) {
        return progressiveJackpotFeedRepository.findOne(id);
    }

    @Transactional
    public ProgressiveJackpotFeed toggledEnabledById(Long id) {
        ProgressiveJackpotFeed progressiveJackpotFeedRegistration = progressiveJackpotFeedRepository.findOne(id);
        progressiveJackpotFeedRegistration.setEnabled(!progressiveJackpotFeedRegistration.getEnabled());
        progressiveJackpotFeedRegistration.setLastUpdatedOn(new Date());
        return progressiveJackpotFeedRepository.save(progressiveJackpotFeedRegistration);
    }

    public void saveProgressiveJackpotFeed(
            lithium.service.games.client.objects.ProgressiveJackpotFeedRegistration registration) {
        List<lithium.service.domain.client.objects.Domain> playerDomains = cachingDomainClientService
                .getDomainClient().findAllPlayerDomains().getData();

        playerDomains.stream().forEach(playerDomain -> {
            GameSupplier gameSupplier = gameSupplierService.findByDomainAndSupplierName(playerDomain.getName(),
                    registration.getGameSupplier());
            Module module = moduleRepository.findOrCreateByName(registration.getModule(), Module::new);

            if (gameSupplier == null) {
                log.error("The game supplier {} does not exist for {}", registration.getGameSupplier(),
                        playerDomain.getName());
                return;
            }

            // FIXME: If a new player domain is added, the applicable feed will not be created until the casino providers
            //        are restarted.
            ProgressiveJackpotFeed feed = progressiveJackpotFeedRepository.findByGameSupplier(gameSupplier);
            if (feed == null) {
                progressiveJackpotFeedRepository.save(ProgressiveJackpotFeed.builder()
                        .enabled(false)
                        .registeredOn(new Date())
                        .lastUpdatedOn(new Date())
                        .gameSupplier(gameSupplier)
                        .module(module)
                        .build());
            }
        });
    }
}
