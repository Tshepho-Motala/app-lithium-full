package lithium.service.casino.service;

import java.util.Optional;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.casino.data.entities.BetResult;
import lithium.service.casino.data.entities.BetResultKind;
import lithium.service.casino.data.entities.BetRound;
import lithium.service.casino.data.entities.Currency;
import lithium.service.casino.data.entities.Domain;
import lithium.service.casino.data.entities.Game;
import lithium.service.casino.data.entities.Provider;
import lithium.service.casino.data.entities.User;
import lithium.service.casino.data.enums.BetResultRequestKindEnum;
import lithium.service.casino.data.repositories.BetResultKindRepository;
import lithium.service.casino.data.repositories.BetResultRepository;
import lithium.service.casino.data.repositories.BetRoundRepository;
import lithium.service.casino.data.repositories.CurrencyRepository;
import lithium.service.casino.data.repositories.DomainRepository;
import lithium.service.casino.data.repositories.GameRepository;
import lithium.service.casino.data.repositories.ProviderRepository;
import lithium.service.casino.data.repositories.UserRepository;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status474BetRoundNotFoundException;
import lithium.service.casino.exceptions.Status475NullVariablesException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class BetResultPersistService {
    @Autowired private BetResultRepository betResultRepository;
    @Autowired private BetResultKindRepository betResultKindRepository;
    @Autowired private BetRoundRepository betRoundRepository;
    @Autowired private CurrencyRepository currencyRepository;
    @Autowired private DomainRepository domainRepository;
    @Autowired private GameRepository gameRepository;
    @Autowired private ProviderRepository providerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PubSubCasinoService pubSubCasinoService;

    @TimeThisMethod
    public void persist(String userGuid, String gameGuid, String domainName, String providerGuid, String roundId,
            String currencyCode, BetResultRequestKindEnum kind, boolean checkSequence,
            Integer sequenceNumber, String betResultTransactionId, boolean roundComplete, double returns,
            long transactionTimestamp, Long lithiumAccountingId) throws Status409DuplicateSubmissionException,
            Status500UnhandledCasinoClientException {
        log.trace("betresult.persist [userGuid=" + userGuid + ", gameGuid=" + gameGuid
                + ", domainName=" + domainName + ", providerGuid=" + providerGuid
                + ", roundId="+ roundId + ", currencyCode=" + currencyCode + ", kind=" + kind
                + ", checkSequence=" + checkSequence + ", sequenceNumber=" + sequenceNumber
                + ", betResultTransactionId=" + betResultTransactionId + ", roundComplete=" + roundComplete
                + ", returns=" + returns + ", transactionTimestamp=" + transactionTimestamp
                + ", lithiumAccountingId=" + lithiumAccountingId + "]");

        SW.start("betresult.persist.findBetRound");
        Domain domain = domainRepository.findOrCreateByName(domainName, () -> new Domain());
        Provider provider = providerRepository.findOrCreateByGuid(providerGuid, () -> Provider.builder()
                .domain(domain).build());
        BetRound round = betRoundRepository.findByProviderAndGuid(provider, roundId);
        if (round == null) {
            User user = userRepository.findOrCreateByGuid(userGuid, () -> User.builder().domain(domain).build());
            Game game = gameRepository.findOrCreateByGuid(gameGuid, () -> new Game());
            round = betRoundRepository.save(
                    BetRound.builder()
                            .provider(provider)
                            .guid(roundId)
                            .game(game)
                            .user(user)
                            .complete(false)
                            .roundReturnsTotal(0)
                            .sequenceNumber(sequenceNumber)
                            .build()
            );
        }
        SW.stop();

        SW.start("betresult.persist.settlement");
        Currency currency = currencyRepository.findOrCreateByCode(currencyCode, () -> new Currency());
        BetResultKind betResultKind = betResultKindRepository.findOrCreateByCode(
                kind.toString(), () -> new BetResultKind());

        if (checkSequence) {
            if (sequenceNumber != round.getSequenceNumber() + 1) {
                throw new Status500UnhandledCasinoClientException("Sequence number out of order for this round."
                        + " Expected : " + (round.getSequenceNumber() + 1));
            }
        }

        BetResult betResult = betResultRepository.findByProviderAndBetResultTransactionId(provider,
                betResultTransactionId);
        if (betResult != null) {
            throw new Status409DuplicateSubmissionException(
                    "BetResult with transaction ID " + betResult.getBetResultTransactionId() + " is already registered");
        }

        betResult = new BetResult();
        betResult.setBetRound(round);
        betResult.setRoundComplete(roundComplete);
        betResult.setCurrency(currency);
        betResult.setBetResultKind(betResultKind);
        betResult.setReturns(returns);
        betResult.setTransactionTimestamp(new Date(transactionTimestamp));
        betResult.setBetResultTransactionId(betResultTransactionId);
        betResult.setProvider(round.getProvider());
        betResult.setLithiumAccountingId(lithiumAccountingId);

        betResult.getBetRound().setSequenceNumber(sequenceNumber);

        try {
            betResult = betResultRepository.save(betResult);
        } catch (DataIntegrityViolationException e) {
            throw new Status409DuplicateSubmissionException(
                    "Settlement with transaction ID " + betResultTransactionId + " is already registered");
        }

        round.setRoundReturnsTotal(round.getRoundReturnsTotal() + betResult.getReturns());
        round.setLastBetResult(betResult);
        round.setComplete(roundComplete);
        betRoundRepository.save(round);
        pubSubCasinoService.buildPubSubCasinoSettlementMessage(domainName,betResult);
        SW.stop();
    }

    @TimeThisMethod
    public void completeBetRound(String domainName, String providerGuid, String roundId, Optional<String>gameGuid, Optional<String> userGuid)
            throws Status475NullVariablesException {
        try {
            log.trace("completeBetRound | domainName: {}, providerGuid: {}, roundId: {}", domainName, providerGuid,
                    roundId);
            Domain domain = domainRepository.findOrCreateByName(domainName, () -> new Domain());
            String gameGuidValue = gameGuid.orElseThrow(() -> new Status475NullVariablesException("gameGuid not provided"));
            String userGuidValue = userGuid.orElseThrow(() -> new Status475NullVariablesException("userGuid not provided"));
            Provider provider = providerRepository.findOrCreateByGuid(providerGuid, () -> Provider.builder()
                    .domain(domain).build());
            BetRound betRound = betRoundRepository.findByProviderAndGuid(provider, roundId);
            Game game = gameRepository.findOrCreateByGuid(gameGuidValue, () -> new Game());
            User user = userRepository.findOrCreateByGuid(userGuidValue, () -> User.builder().domain(domain).build());
            if (betRound == null && gameGuidValue != null && userGuidValue != null ){
                betRound = betRoundRepository.save(
                        BetRound.builder()
                                .provider(provider)
                                .game(game)
                                .guid(roundId)
                                .user(user)
                                .complete(false)
                                .roundReturnsTotal(0)
                                .build()
                );
            }
            processLastBetResult(betRound);
        } catch (Status475NullVariablesException e) {
            log.error("An unexpected error occurred while trying to complete the bet round | {} | domainName: {},"
                    + " providerGuid: {}, roundId: {}, gameGuidValue: {}, userGuidValue: {},", e.getMessage(), domainName, providerGuid, roundId, gameGuid, userGuid, e);
            throw e;
        }
    }

    private void processLastBetResult(BetRound betRound) {
        BetResult lastBetResult = betRound.getLastBetResult();
        if (lastBetResult != null) {
            lastBetResult.setRoundComplete(true);
            lastBetResult = betResultRepository.save(lastBetResult);
        }
        betRound.setLastBetResult(lastBetResult);
        betRound.setComplete(true);
        betRound = betRoundRepository.save(betRound);
        log.trace("Bet round completed | betRound: {}", betRound);
    }

    @TimeThisMethod
    public void completeBetRound(String domainName, String providerGuid, String roundId)
            throws Status474BetRoundNotFoundException {
        try {
            log.trace("completeBetRound | domainName: {}, providerGuid: {}, roundId: {}", domainName, providerGuid,
                    roundId);
            Domain domain = domainRepository.findOrCreateByName(domainName, () -> new Domain());
            Provider provider = providerRepository.findOrCreateByGuid(providerGuid, () -> Provider.builder()
                    .domain(domain).build());
            BetRound betRound = betRoundRepository.findByProviderAndGuid(provider, roundId);
           if (betRound == null)
                throw new Status474BetRoundNotFoundException();

            processLastBetResult(betRound);
        } catch (Status474BetRoundNotFoundException e) {
            log.warn("Bet round not found | domainName: {}, providerGuid: {}, roundId: {}", domainName,
                    providerGuid, roundId);
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred while trying to complete the bet round | {} | domainName: {},"
                    + " providerGuid: {}, roundId: {}", e.getMessage(), domainName, providerGuid, roundId, e);
            throw e;
        }
    }
}
