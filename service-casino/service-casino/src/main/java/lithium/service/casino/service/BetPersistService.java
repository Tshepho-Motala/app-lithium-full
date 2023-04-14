package lithium.service.casino.service;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.casino.data.entities.Bet;
import lithium.service.casino.data.entities.BetRequestKind;
import lithium.service.casino.data.entities.BetRound;
import lithium.service.casino.data.entities.Currency;
import lithium.service.casino.data.entities.Domain;
import lithium.service.casino.data.entities.Game;
import lithium.service.casino.data.entities.Provider;
import lithium.service.casino.data.entities.User;
import lithium.service.casino.data.enums.BetRequestKindEnum;
import lithium.service.casino.data.repositories.BetRepository;
import lithium.service.casino.data.repositories.BetRequestKindRepository;
import lithium.service.casino.data.repositories.BetRoundRepository;
import lithium.service.casino.data.repositories.CurrencyRepository;
import lithium.service.casino.data.repositories.DomainRepository;
import lithium.service.casino.data.repositories.GameRepository;
import lithium.service.casino.data.repositories.ProviderRepository;
import lithium.service.casino.data.repositories.UserRepository;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class BetPersistService {
    @Autowired private BetRepository betRepository;
    @Autowired private BetRequestKindRepository betRequestKindRepository;
    @Autowired private BetRoundRepository betRoundRepository;
    @Autowired private CurrencyRepository currencyRepository;
    @Autowired private DomainRepository domainRepository;
    @Autowired private GameRepository gameRepository;
    @Autowired private ProviderRepository providerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PubSubCasinoService pubSubCasinoService;
    @TimeThisMethod
    public void persist(String currencyCode, String gameId, BetRequestKindEnum kind, String providerGuid, String roundId,
            String betTransactionId, boolean checkSequence, Integer sequenceNumber, double amount, long transactionTimestamp,
            String playerGuid, String domainName, Long lithiumAccountingId, Double balanceAfter, boolean roundFinished, Long sessionId)
            throws Status409DuplicateSubmissionException, Status500UnhandledCasinoClientException {
        log.trace("bet.persist [currencyCode="+currencyCode+", "+gameId+", kind="+kind.toString()
                +", providerGuid="+providerGuid+", roundId="+roundId+", betTransactionId="+betTransactionId
                +", checkSequence="+checkSequence+", sequenceNumber="+sequenceNumber+", amount="+amount
                +", transactionTimestamp="+transactionTimestamp+", playerGuid="+playerGuid+", domainName="+domainName+"]");

        SW.start("bet.persist.findorcreates");
        Currency currency = currencyRepository.findOrCreateByCode(currencyCode, () -> new Currency());
        Domain domain = domainRepository.findOrCreateByName(domainName, () -> new Domain());
        User user = userRepository.findOrCreateByGuid(playerGuid, () -> User.builder().domain(domain).build());
        Game game = gameRepository.findOrCreateByGuid(gameId, () -> new Game());
        BetRequestKind betRequestKind = betRequestKindRepository.findOrCreateByCode(kind.toString(),
                () -> new BetRequestKind());
        SW.stop();

        SW.start("bet.persist.findbetround");
        Provider provider = providerRepository.findOrCreateByGuid(providerGuid, () -> Provider.builder().domain(domain)
                .build());
        BetRound betRound = betRoundRepository.findByProviderAndGuid(provider, roundId);
        if (betRound == null) {
            betRound = betRoundRepository.save(
                    BetRound.builder()
                        .provider(provider)
                        .guid(roundId)
                        .game(game)
                        .user(user)
                        .complete(roundFinished)
                        .roundReturnsTotal(0)
                        .build()
            );
            if (checkSequence) betRound.setSequenceNumber(0);
        }
        SW.stop();

        SW.start("bet.persist.bet");
        Bet bet = betRepository.findByProviderAndBetTransactionId(provider, betTransactionId);
        if (bet != null) {
            throw new Status409DuplicateSubmissionException(
                    "Bet with transaction ID " + bet.getBetTransactionId() + " is already registered");
        }

        if (checkSequence) {
            if (sequenceNumber != betRound.getSequenceNumber() + 1) {
                throw new Status500UnhandledCasinoClientException("Sequence number out of order for this round. " +
                        "Expected : " + (betRound.getSequenceNumber() + 1));
            }
        }

        bet = new Bet();
        bet.setAmount(amount);
        bet.setBetTransactionId(betTransactionId);
        bet.setKind(betRequestKind);
        bet.setCurrency(currency);
        bet.setBetRound(betRound);
        bet.setTransactionTimestamp(new Date(transactionTimestamp));
        bet.setLithiumAccountingId(lithiumAccountingId);
        bet.setBalanceAfter(balanceAfter);
        bet.setProvider(provider);
        bet.setSessionId(sessionId);

        betRound.setSequenceNumber(sequenceNumber);

        try {
            betRoundRepository.save(betRound);
            bet = betRepository.save(bet);
            pubSubCasinoService.buildPubSubCasinoPlacementMessage(domainName,bet);
        } catch (DataIntegrityViolationException e) {
            throw new Status409DuplicateSubmissionException(
                    "Bet with transaction ID " + bet.getBetTransactionId() + " is already registered");
        }
        SW.stop();
    }
}
