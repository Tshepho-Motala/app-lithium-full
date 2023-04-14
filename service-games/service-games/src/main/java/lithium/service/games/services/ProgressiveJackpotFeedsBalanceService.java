package lithium.service.games.services;

import lithium.service.games.data.entities.progressivejackpotfeeds.ProgressiveJackpotBalance;
import lithium.service.games.data.entities.progressivejackpotfeeds.ProgressiveJackpotGameBalance;
import lithium.service.games.data.repositories.ProgressiveJackpotBalanceRepository;
import lithium.service.games.data.repositories.ProgressiveJackpotGameBalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProgressiveJackpotFeedsBalanceService {
    @Autowired private ProgressiveJackpotBalanceRepository balanceRepo;
    @Autowired private ProgressiveJackpotGameBalanceRepository gameBalanceRepo;
    @Autowired private ModelMapper modelMapper;

    @Cacheable(value="lithium.service.games.progressive-jackpots.balances-by-domain", key="{#root.args[0]}",
            unless = "#result == null")
    public List<lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotBalance>
            getProgressiveJackpotFeedsByDomain(String domainName) {
        log.warn("Progressive Jackpot Balances retrieved from DB rather than cache");
        List<ProgressiveJackpotBalance> progressiveJackpotBalances = balanceRepo
                .findByGameSupplierDomainName(domainName);
        return modelMapper.map(progressiveJackpotBalances,
                new TypeToken<List<lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotBalance>>(){}.getType());
    }

    @Cacheable(value="lithium.service.games.progressive-jackpots.game-balances-by-domain", key="{#root.args[0]}",
            unless = "#result == null")
    public List<lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotGameBalance>
            getProgressiveJackpotGameBalanceByDomain(String domainName) {
        log.warn("Progressive Jackpot Game Balances retrieved from DB rather than cache");
        List<ProgressiveJackpotGameBalance> progressiveJackpotBalances = gameBalanceRepo
                .findByGameDomainName(domainName);
        return modelMapper.map(progressiveJackpotBalances,
                new TypeToken<List<lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotGameBalance>>(){}.getType());
    }

    public Page<ProgressiveJackpotGameBalance> findByDomain(String domainName, Pageable pageable) {
        return gameBalanceRepo.findByGameDomainName(domainName, pageable);
    }

    public List<ProgressiveJackpotGameBalance> findByDomain(String domainName) {
        return gameBalanceRepo.findByGameDomainName(domainName);
    }
}
