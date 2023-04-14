package lithium.service.casino.provider.incentive.services.pickanysettlement;

import lithium.exceptions.Status444ReferencedEntityNotFound;
import lithium.metrics.SW;
import lithium.modules.ModuleInfo;
import lithium.service.casino.provider.incentive.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.incentive.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.incentive.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.incentive.api.schema.pickany.settlement.PickAnySettlementRequest;
import lithium.service.casino.provider.incentive.api.schema.pickany.settlement.PickAnySettlementRequestPick;
import lithium.service.casino.provider.incentive.config.ProviderConfig;
import lithium.service.casino.provider.incentive.config.ProviderConfigService;
import lithium.service.casino.provider.incentive.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.incentive.context.PickAnyEntryContext;
import lithium.service.casino.provider.incentive.context.PickAnySettlementContext;
import lithium.service.casino.provider.incentive.storage.entities.PickAnyEntry;
import lithium.service.casino.provider.incentive.storage.entities.PickAnySettlement;
import lithium.service.casino.provider.incentive.storage.repositories.PickAnyEntryRepository;
import lithium.service.casino.provider.incentive.storage.repositories.PickAnySettlementRepository;
import lithium.util.HmacSha256HashCalculator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PickAnySettlementPhase1Validate {

    @Autowired
    PickAnySettlementRepository settlementRepository;

    @Autowired
    PickAnyEntryRepository entryRepository;

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    ModuleInfo moduleInfo;

    public void validate(PickAnySettlementContext context) throws Status470HashInvalidException,
            Status409DuplicateSubmissionException, Status444ReferencedEntityNotFound, Status500ProviderNotConfiguredException,
            Status422DataValidationError {

        validateData(context);
        validateExisting(context);
        validateEntryExists(context);

        ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), context.getEntry().getDomain().getName());
        validateSha256(context, config.getHashPassword());
    }

    private void validateData(PickAnySettlementContext context) throws Status422DataValidationError {
        PickAnySettlementRequest request = context.getRequest();
        if (request.getSettlementTransactionId() == null || request.getSettlementTransactionId().length() == 0)
            throw new Status422DataValidationError("settlementTransactionId may not be empty");
        if (request.getEntryTransactionId() == null || request.getEntryTransactionId().length() == 0)
            throw new Status422DataValidationError("entryTransactionId may not be empty");
        if (request.getSettlementTimestamp() == null)
            throw new Status422DataValidationError("settlementTimestamp may not be empty");
        if (request.getTotalPointsResult() == null)
            throw new Status422DataValidationError("totalPointsResult may not be empty");
        if (request.getPicks() == null || request.getPicks().isEmpty())
            throw new Status422DataValidationError("Array of picks may not be empty");
        for (PickAnySettlementRequestPick pick : request.getPicks()) {
            if (pick.getEventId() == null)
                throw new Status422DataValidationError("Pick eventId may not be null");
            if (pick.getPointsResult() == null)
                throw new Status422DataValidationError("Pick pointsResult may not be null");
            if (pick.getEventHomeScore() == null)
                throw new Status422DataValidationError("Pick eventHomeScore may not be null");
            if (pick.getEventAwayScore() == null) {
                throw new Status422DataValidationError("Pick eventAwayScore may not be null");
            }
        }
    }

    private void validateEntryExists(PickAnySettlementContext context) throws Status444ReferencedEntityNotFound,
            Status422DataValidationError {
        PickAnyEntry entry = entryRepository.findByEntryTransactionId(context.getRequest().getEntryTransactionId());
        if (entry == null) {
            throw new Status444ReferencedEntityNotFound("The referenced PickAnyEntry could not be found");
        }
        if (entry.getSettlement() != null) {
            throw new Status422DataValidationError("The referenced PickAnyEntry is already settled");
        }
        context.setEntry(entry);
    }

    private void validateSha256(PickAnySettlementContext context, String preSharedKey)
            throws Status470HashInvalidException {
        SW.start("validateSha256");
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(preSharedKey);

        hasher.addItem(context.getRequest().getEntryTransactionId());
        hasher.addItem(context.getRequest().getSettlementTransactionId());
        hasher.addItem(context.getRequest().getSettlementTimestamp());

        String expectedHash = hasher.calculateHash();
        if (!expectedHash.equals(context.getRequest().getSha256())) {
            log.warn(
                    "Expected " + expectedHash + " sha256 but got " + context.getRequest().getSha256() + " " +
                            "Hash calculated using " + hasher.getUnencryptedPayload() + " and key " + preSharedKey + " " + context);
            throw new Status470HashInvalidException();
        }
        SW.stop();
    }

    private void validateExisting(PickAnySettlementContext context) throws Status409DuplicateSubmissionException {
        PickAnySettlement settlement =
                settlementRepository.findBySettlementTransactionId(context.getRequest().getSettlementTransactionId());
        if (settlement != null) {
            context.setSettlement(settlement);
            throw new Status409DuplicateSubmissionException("Settlement already exists");
        }
    }
}
