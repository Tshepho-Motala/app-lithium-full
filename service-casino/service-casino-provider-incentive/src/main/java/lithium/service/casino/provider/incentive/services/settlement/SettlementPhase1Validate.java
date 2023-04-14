package lithium.service.casino.provider.incentive.services.settlement;

import lithium.metrics.SW;
import lithium.modules.ModuleInfo;
import lithium.service.casino.provider.incentive.api.exceptions.Status404BetTransactionNotFoundException;
import lithium.service.casino.provider.incentive.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.incentive.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.incentive.api.schema.settlement.SettlementRequest;
import lithium.service.casino.provider.incentive.api.schema.settlement.SettlementRequestSelection;
import lithium.service.casino.provider.incentive.api.schema.settlement.SettlementResultEnum;
import lithium.service.casino.provider.incentive.config.ProviderConfig;
import lithium.service.casino.provider.incentive.config.ProviderConfigService;
import lithium.service.casino.provider.incentive.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.incentive.context.SettlementContext;
import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.service.casino.provider.incentive.storage.entities.BetSelection;
import lithium.service.casino.provider.incentive.storage.repositories.BetRepository;
import lithium.service.casino.provider.incentive.storage.repositories.BetSelectionRepository;
import lithium.service.casino.provider.incentive.storage.repositories.EventRepository;
import lithium.util.HmacSha256HashCalculator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SettlementPhase1Validate {

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    ModuleInfo module;

    @Autowired @Setter
    BetRepository betRepository;

    @Autowired @Setter
    BetSelectionRepository betSelectionRepository;

    public void validate(SettlementContext context, SettlementRequest request) throws
            Status404BetTransactionNotFoundException,
            Status500ProviderNotConfiguredException, Status470HashInvalidException, Status422DataValidationError {

        SW.start("findbet");
        Bet bet = betRepository.findByBetTransactionId(request.getBetTransactionId());
        if (bet == null) {
            throw new Status404BetTransactionNotFoundException();
        }
        SW.stop();

        context.setDomainName(bet.getPlacement().getDomain().getName());
        context.setBet(bet);

        SW.start("getproviderconfig");
        ProviderConfig config = configService.getConfig(module.getModuleName(), context.getDomainName());
        SW.stop();

        validateSha256(request, config.getHashPassword());
        validateSelections(context, request);
        validateResult(context, request);
    }

    private void validateResult(SettlementContext context, SettlementRequest request) throws Status422DataValidationError {
        switch (request.getResult()) {
            case "WIN":
                context.setResult(SettlementResultEnum.WIN);
                if (request.getReturns() == 0.0) {
                    throw new Status422DataValidationError("Returns may not be 0 on a WIN");
                }
                break;
            case "LOST":
                context.setResult(SettlementResultEnum.LOST);
                if (request.getReturns() != 0.0) {
                    throw new Status422DataValidationError("Returns should be 0 when LOST");
                }
                break;
            case "VOID":
                context.setResult(SettlementResultEnum.VOID);
                if (context.getBet().getTotalStake() != request.getReturns()) {
                    // First need confirmation that this will be in the payload
                    // throw new Status422DataValidationError("Returns should match placement amount when result is VOID");
                }
                break;
            default:
                throw new Status422DataValidationError("Invalid result (should be WIN, LOST or VOID): " + request.getResult());
        }
    }


    private void validateSelections(SettlementContext context, SettlementRequest request) throws Status422DataValidationError {

        SW.start("validateSelections");

        if (request.getSelections() == null) {
            throw new Status422DataValidationError("No selections specified");
        }

        List<BetSelection> betSelections = betSelectionRepository.findByBet(context.getBet());
        int betSelectionCount = betSelections.size();
        int settlementSelectionCount = request.getSelections().size();

        if (betSelectionCount != settlementSelectionCount) {
            throw new Status422DataValidationError("Bet had " + betSelectionCount
                    + " selections but settlement has " + settlementSelectionCount);
        }

        context.setBetSelections(betSelections);

        for (SettlementRequestSelection requestSelection : request.getSelections()) {
            boolean found = false;
            for (BetSelection betSelection : betSelections) {
                if (betSelection.getSelection().getGuid().equals(requestSelection.getSelectionId())) found = true;
            }
            if (!found) {
                throw new Status422DataValidationError("Bet selection " + requestSelection.getSelectionId()
                        + " not found in bet " + context.getBet().getBetTransactionId());
            }
        }

        SW.stop();
    }

    private void validateSha256(SettlementRequest request, String preSharedKey)
            throws Status470HashInvalidException {
        SW.start("validateSha256");
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(preSharedKey);
        hasher.addItem(request.getSettlementTransactionId());
        hasher.addItem(request.getBetTransactionId());
        String expectedHash = hasher.calculateHash();
        if (!expectedHash.equals(request.getSha256())) {
            log.warn("Expected " + expectedHash + " sha256 but got " + request.getSha256() + " " + request);
            log.warn("Hash calculated using " + hasher.getUnencryptedPayload() + " and key " + preSharedKey);
            throw new Status470HashInvalidException();
        }
        SW.stop();
    }

}
