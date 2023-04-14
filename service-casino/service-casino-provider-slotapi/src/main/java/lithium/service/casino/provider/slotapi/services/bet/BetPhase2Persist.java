package lithium.service.casino.provider.slotapi.services.bet;

import lithium.metrics.SW;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.slotapi.context.BetContext;
import lithium.service.casino.provider.slotapi.storage.entities.Bet;
import lithium.service.casino.provider.slotapi.storage.entities.BetRequestKind;
import lithium.service.casino.provider.slotapi.storage.entities.BetRound;
import lithium.service.casino.provider.slotapi.storage.entities.Currency;
import lithium.service.casino.provider.slotapi.storage.entities.Domain;
import lithium.service.casino.provider.slotapi.storage.entities.Game;
import lithium.service.casino.provider.slotapi.storage.entities.User;
import lithium.service.casino.provider.slotapi.storage.repositories.BetRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.BetRequestKindRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.BetRoundRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.CurrencyRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.DomainRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.GameRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class BetPhase2Persist {

    final @Setter
    UserRepository userRepository;

    final @Setter
    CurrencyRepository currencyRepository;

    final @Setter
    BetRepository betRepository;

    final @Setter
    DomainRepository domainRepository;

    final @Setter
    GameRepository gameRepository;

    final @Setter
    BetRoundRepository betRoundRepository;

    final @Setter
    BetRequestKindRepository betRequestKindRepository;

    public void persist(
        BetContext context,
        String playerGuid,
        String domainName
    ) throws
        Status409DuplicateSubmissionException,
        Status500UnhandledCasinoClientException
    {

        log.debug("bet.persist " + context);

        SW.start("bet.persist.findorcreates");

        Currency currency = currencyRepository.findOrCreateByCode(context.getRequest().getCurrencyCode(), () -> new Currency());
        Domain domain = domainRepository.findOrCreateByName(domainName, () -> new Domain());
        User user = userRepository.findOrCreateByGuid(playerGuid, () -> User.builder().domain(domain).build());
        Game game = gameRepository.findOrCreateByGuid(context.getRequest().getGameId(), () -> new Game());
        BetRequestKind betRequestKind = betRequestKindRepository.findOrCreateByCode(context.getRequest().getKind().toString(),
                () -> new BetRequestKind());

        SW.stop();
        SW.start("bet.persist.findbetround");

        BetRound betRound = betRoundRepository.findOrCreateByGuid(context.getRequest().getRoundId(), () ->
                BetRound.builder().game(game).user(user).sequenceNumber(0).complete(false)
                        .sessionId(context.getSessionId()).build());

        SW.stop();
        SW.start("bet.persist.bet");

        Bet bet = betRepository.findByBetTransactionId(context.getRequest().getBetTransactionId());
        if (bet != null) {
            context.setBet(bet);
            throw new Status409DuplicateSubmissionException(
                    "Bet with transaction ID " + bet.getBetTransactionId() + " is already registered");
        }

        if (context.getRequest().getSequenceNumber() != betRound.getSequenceNumber() + 1) {
            throw new Status500UnhandledCasinoClientException("Sequence number out of order for this round. Expected : "+(betRound.getSequenceNumber() + 1));
        }

        bet = new Bet();
        bet.setAmount(context.getRequest().getAmount());
        bet.setBetTransactionId(context.getRequest().getBetTransactionId());
        bet.setKind(betRequestKind);
        bet.setCurrency(currency);
        bet.setBetRound(betRound);
        bet.setTransactionTimestamp(new Date(context.getRequest().getTransactionTimestamp()));

        betRound.setSequenceNumber(context.getRequest().getSequenceNumber());
        context.setBet(bet);

        try {
            betRoundRepository.save(betRound);
            bet = betRepository.save(bet);
        } catch (DataIntegrityViolationException e) {

            throw new Status409DuplicateSubmissionException(
                    "Bet with transaction ID " + bet.getBetTransactionId() + " is already registered");
        }

        SW.stop();
    }

}
