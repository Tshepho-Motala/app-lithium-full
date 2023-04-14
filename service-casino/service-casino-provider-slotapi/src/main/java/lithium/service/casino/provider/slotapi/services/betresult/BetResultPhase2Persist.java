package lithium.service.casino.provider.slotapi.services.betresult;

import lithium.metrics.SW;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.slotapi.api.schema.betresult.BetResultRequest;
import lithium.service.casino.provider.slotapi.context.BetResultContext;
import lithium.service.casino.provider.slotapi.storage.entities.BetResult;
import lithium.service.casino.provider.slotapi.storage.entities.BetResultKind;
import lithium.service.casino.provider.slotapi.storage.entities.BetRound;
import lithium.service.casino.provider.slotapi.storage.entities.Currency;
import lithium.service.casino.provider.slotapi.storage.repositories.BetResultKindRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.BetResultRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.BetRoundRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.CurrencyRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class BetResultPhase2Persist {

    @Autowired @Setter
    BetResultRepository betResultRepository;

    @Autowired @Setter
    BetResultKindRepository betResultKindRepository;

    @Autowired @Setter
    BetRoundRepository betRoundRepository;

    @Autowired @Setter
    CurrencyRepository currencyRepository;

    public void persist(
        BetResultContext context,
        BetResultRequest request
    ) throws
        Status409DuplicateSubmissionException,
        Status500UnhandledCasinoClientException
    {
        SW.start("persist.settlement");

        Currency currency = currencyRepository.findOrCreateByCode(
                request.getCurrencyCode(), () -> new Currency());
        BetResultKind betResultKind = betResultKindRepository.findOrCreateByCode(
                request.getKind().toString(), () -> new BetResultKind());

        if (context.getRequest().getSequenceNumber() != context.getBetRound().getSequenceNumber() + 1) {
            throw new Status500UnhandledCasinoClientException("Sequence number out of order for this round. Expected : "+(context.getBetRound().getSequenceNumber() + 1));
        }

        BetResult betResult = betResultRepository.findByBetResultTransactionId(request.getBetResultTransactionId());
        if (betResult != null) {
            context.setBetResult(betResult);
            throw new Status409DuplicateSubmissionException(
                    "BetResult with transaction ID " + betResult.getBetResultTransactionId() + " is already registered");
        }

        betResult = new BetResult();
        betResult.setBetRound(context.getBetRound());
        betResult.setRoundComplete(request.isRoundComplete());
        betResult.setCurrency(currency);
        betResult.setBetResultKind(betResultKind);
        betResult.setReturns(request.getReturns());
        betResult.setTransactionTimestamp(new Date(request.getTransactionTimestamp()));
        betResult.setBetResultTransactionId(request.getBetResultTransactionId());

        betResult.getBetRound().setSequenceNumber(context.getRequest().getSequenceNumber());

        try {
            betResult = betResultRepository.save(betResult);
        } catch (DataIntegrityViolationException e) {
            throw new Status409DuplicateSubmissionException(
                    "Settlement with transaction ID " + request.getBetResultTransactionId() + " is already registered");
        }

        context.getBetRound().setBetResult(betResult);
        context.getBetRound().setComplete(request.isRoundComplete());
        betRoundRepository.save(context.getBetRound());

        SW.stop();

        context.setBetResult(betResult);

    }

}
