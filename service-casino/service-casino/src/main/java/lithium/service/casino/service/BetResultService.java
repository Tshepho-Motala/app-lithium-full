package lithium.service.casino.service;

import lithium.service.casino.client.objects.response.LastBetResultResponse;
import lithium.service.casino.data.entities.BetResult;
import lithium.service.casino.data.entities.BetRound;
import lithium.service.casino.data.entities.Domain;
import lithium.service.casino.data.entities.Provider;
import lithium.service.casino.data.repositories.BetRoundRepository;
import lithium.service.casino.data.repositories.DomainRepository;
import lithium.service.casino.data.repositories.ProviderRepository;
import lithium.service.casino.exceptions.Status474BetRoundNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BetResultService {

    @Autowired
    private BetRoundRepository betRoundRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private DomainRepository domainRepository;

    public LastBetResultResponse retrieveLastBetResultByRoundGuid(String domainName, String providerGuid, String roundId)
            throws Status474BetRoundNotFoundException {
        Domain domain = domainRepository.findOrCreateByName(domainName, () -> new Domain());
        Provider provider = providerRepository.findOrCreateByGuid(providerGuid, () -> Provider.builder()
                .domain(domain).build());
        BetRound betRound = betRoundRepository.findByProviderAndGuid(provider, roundId);
        try {
            if (betRound == null) {
                throw new Status474BetRoundNotFoundException();
            }

            BetResult lastBetResult = betRound.getLastBetResult();
            if(lastBetResult == null) {
                return null;
            }
            return LastBetResultResponse.builder()
                    .roundComplete(lastBetResult.isRoundComplete())
                    .transactionTimestamp(lastBetResult.getTransactionTimestamp())
                    .betResultKindCode(lastBetResult.getBetResultKind().getCode())
                    .returns(lastBetResult.getReturns())
                    .build();
        } catch (Status474BetRoundNotFoundException e) {
            log.warn("Bet round not found | domainName: {}, providerGuid: {}, roundId: {}", domainName,
                    providerGuid, roundId);
            throw e;
        }

    }

}
