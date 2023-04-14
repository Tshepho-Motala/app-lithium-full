package lithium.service.casino.provider.incentive.services.pickanysettlement;

import lithium.exceptions.Status444ReferencedEntityNotFound;
import lithium.service.casino.provider.incentive.api.schema.pickany.settlement.PickAnySettlementRequest;
import lithium.service.casino.provider.incentive.api.schema.pickany.settlement.PickAnySettlementRequestPick;
import lithium.service.casino.provider.incentive.context.PickAnySettlementContext;
import lithium.service.casino.provider.incentive.storage.entities.PickAnyEntryPick;
import lithium.service.casino.provider.incentive.storage.entities.PickAnySettlement;
import lithium.service.casino.provider.incentive.storage.entities.PickAnySettlementPick;
import lithium.service.casino.provider.incentive.storage.repositories.PickAnyEntryPickRepository;
import lithium.service.casino.provider.incentive.storage.repositories.PickAnyEntryRepository;
import lithium.service.casino.provider.incentive.storage.repositories.PickAnyGameRepository;
import lithium.service.casino.provider.incentive.storage.repositories.PickAnySettlementPickRepository;
import lithium.service.casino.provider.incentive.storage.repositories.PickAnySettlementRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class PickAnySettlementPhase2Persist {

    @Autowired @Setter
    PickAnySettlementRepository settlementRepository;

    @Autowired @Setter
    PickAnySettlementPickRepository settlementPickRepository;

    @Autowired @Setter
    PickAnyEntryPickRepository entryPickRepository;

    @Autowired @Setter
    PickAnyEntryRepository entryRepository;

    @Autowired @Setter
    PickAnyGameRepository gameRepository;

    public void persist(PickAnySettlementContext context) throws Status444ReferencedEntityNotFound {

        PickAnySettlementRequest request = context.getRequest();
        PickAnySettlement settlement = new PickAnySettlement();
        settlement.setEntry(context.getEntry());
        settlement.setSettlementTransactionId(request.getSettlementTransactionId());
        settlement.setSettlementTimestamp(new Date(request.getSettlementTimestamp()));
        settlement.setTotalPointsResult(request.getTotalPointsResult());
        settlement = settlementRepository.save(settlement);

        for (PickAnySettlementRequestPick requestPick : request.getPicks()) {
            PickAnySettlementPick pick = new PickAnySettlementPick();
            PickAnyEntryPick entryPick = entryPickRepository.findByEntryAndIncentiveEventId(context.getEntry(), requestPick.getEventId());
            if (entryPick == null) throw new Status444ReferencedEntityNotFound(
                    "No corresponding pick on the entry found for event id " + requestPick.getEventId());
            pick.setEntryPick(entryPick);
            pick.setSettlement(settlement);
            pick.setHomeScoreResult(requestPick.getEventHomeScore());
            pick.setAwayScoreResult(requestPick.getEventAwayScore());
            pick.setPointsResult(requestPick.getPointsResult());
            settlementPickRepository.save(pick);
        }

        context.getEntry().setSettlement(settlement);
        entryRepository.save(context.getEntry());

        context.setSettlement(settlement);
    }

}
