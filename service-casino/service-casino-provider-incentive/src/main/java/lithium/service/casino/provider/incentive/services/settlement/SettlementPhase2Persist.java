package lithium.service.casino.provider.incentive.services.settlement;

import lithium.metrics.SW;
import lithium.service.casino.provider.incentive.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.incentive.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.incentive.api.schema.settlement.SettlementRequest;
import lithium.service.casino.provider.incentive.api.schema.settlement.SettlementRequestSelection;
import lithium.service.casino.provider.incentive.context.SettlementContext;
import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.service.casino.provider.incentive.storage.entities.BetSelection;
import lithium.service.casino.provider.incentive.storage.entities.Currency;
import lithium.service.casino.provider.incentive.storage.entities.SelectionResult;
import lithium.service.casino.provider.incentive.storage.entities.Settlement;
import lithium.service.casino.provider.incentive.storage.entities.SettlementResult;
import lithium.service.casino.provider.incentive.storage.entities.SettlementSelection;
import lithium.service.casino.provider.incentive.storage.repositories.BetRepository;
import lithium.service.casino.provider.incentive.storage.repositories.BetSelectionRepository;
import lithium.service.casino.provider.incentive.storage.repositories.CurrencyRepository;
import lithium.service.casino.provider.incentive.storage.repositories.SelectionResultRepository;
import lithium.service.casino.provider.incentive.storage.repositories.SettlementRepository;
import lithium.service.casino.provider.incentive.storage.repositories.SettlementResultRepository;
import lithium.service.casino.provider.incentive.storage.repositories.SettlementSelectionRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class SettlementPhase2Persist {

    @Autowired @Setter
    BetRepository betRepository;

    @Autowired @Setter
    SettlementRepository settlementRepository;

    @Autowired @Setter
    SettlementResultRepository settlementResultRepository;

    @Autowired @Setter
    SelectionResultRepository selectionResultRepository;

    @Autowired @Setter
    CurrencyRepository currencyRepository;

    @Autowired @Setter
    SettlementSelectionRepository settlementSelectionRepository;

    @Autowired @Setter
    BetSelectionRepository betSelectionRepository;

    public void persist(SettlementContext context, SettlementRequest request) throws
            Status409DuplicateSubmissionException {

        SW.start("persist.settlement");

        Currency currency = currencyRepository.findOrCreateByCode(
                request.getCurrencyCode(), () -> new Currency());
        SettlementResult result = settlementResultRepository.findOrCreateByCode(
                request.getResult(), () -> new SettlementResult());

        Settlement settlement = new Settlement();
        settlement.setBet(context.getBet());
        settlement.setCurrency(currency);
        settlement.setSettlementResult(result);
        settlement.setReturns(request.getReturns());
        settlement.setTransactionTimestamp(new Date(request.getTransactionTimestamp()));
        settlement.setSettlementTransactionId(request.getSettlementTransactionId());

        try {
            settlement = settlementRepository.save(settlement);
        } catch (DataIntegrityViolationException e) {
            throw new Status409DuplicateSubmissionException(
                    "Settlement with transaction ID " + request.getSettlementTransactionId() + " is already registered");
        }

        Bet bet = context.getBet();
        bet.setSettlement(settlement);
        bet = betRepository.save(bet);
        context.setBet(bet);

        SW.stop();
        for (SettlementRequestSelection selectionRequest : request.getSelections()) {

            SW.start("persist.selection." + selectionRequest.getSelectionId());

            BetSelection betSelection = context.getBetSelections().stream()
                    .filter((selection) -> selection.getSelection().getGuid().equals(selectionRequest.getSelectionId()))
                    .findFirst().get();

            SelectionResult selectionResult = selectionResultRepository
                    .findOrCreateByCode(selectionRequest.getResult(), () -> new SelectionResult());

            SettlementSelection selection = new SettlementSelection();
            selection.setBetSelection(betSelection);
            selection.setSelectionResult(selectionResult);
            selection.setSettlement(settlement);

            selection = settlementSelectionRepository.save(selection);

            betSelection.setSettlementSelection(selection);
            betSelection = betSelectionRepository.save(betSelection);

            SW.stop();

        }

        context.setSettlement(settlement);

    }

}
