package lithium.service.casino.provider.incentive.services.pickanyentry;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.metrics.SW;
import lithium.modules.ModuleInfo;
import lithium.service.casino.provider.incentive.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.incentive.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.incentive.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.incentive.api.schema.pickany.entry.PickAnyEntryRequest;
import lithium.service.casino.provider.incentive.config.ProviderConfig;
import lithium.service.casino.provider.incentive.config.ProviderConfigService;
import lithium.service.casino.provider.incentive.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.incentive.context.PickAnyEntryContext;
import lithium.service.casino.provider.incentive.storage.entities.PickAnyEntry;
import lithium.service.casino.provider.incentive.storage.repositories.PickAnyEntryRepository;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.util.HmacSha256HashCalculator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PickAnyEntryPhase1Validate {

    @Autowired @Setter
    LimitInternalSystemService limits;

    @Autowired @Setter
    UserApiInternalClientService userApiInternalClientService;

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    PickAnyEntryRepository entryRepository;

    @Autowired @Setter
    ModuleInfo moduleInfo;

    public void validate(PickAnyEntryContext context) throws Status500UserInternalSystemClientException,
            Status401UnAuthorisedException, Status405UserDisabledException, Status500LimitInternalSystemClientException,
            Status491PermanentSelfExclusionException, Status490SoftSelfExclusionException,
            Status422DataValidationError, Status500ProviderNotConfiguredException, Status470HashInvalidException,
            Status409DuplicateSubmissionException, Status496PlayerCoolingOffException {
        userApiInternalClientService.performUserChecks(context.getPlayerGuid(), context.getLocale(),
                context.getSessionId(), true, true, false);
        limits.checkPlayerRestrictions(context.getPlayerGuid(), context.getLocale());

        PickAnyEntryRequest request = context.getRequest();

        if (request.getGameCode() == null || request.getGameCode().length() == 0) {
            throw new Status422DataValidationError("Game code should be specified");
        }

        if (request.getPicks() == null || request.getPicks().size() == 0) {
            throw new Status422DataValidationError("At least one pick should be specified");
        }

        ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), context.getDomainName());

        validateSha256(context, config.getHashPassword());
        validateExisting(context);
    }

    private void validateSha256(PickAnyEntryContext context, String preSharedKey)
            throws Status470HashInvalidException {
        SW.start("validateSha256");
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(preSharedKey);

        hasher.addItem(context.getRequest().getEntryTransactionId());
        hasher.addItem(context.getRequest().getPredictorId());
        hasher.addItem(context.getRequest().getEntryTimestamp());

        String expectedHash = hasher.calculateHash();
        if (!expectedHash.equals(context.getRequest().getSha256())) {
            log.warn(
                "Expected " + expectedHash + " sha256 but got " + context.getRequest().getSha256() + " " +
                "Hash calculated using " + hasher.getUnencryptedPayload() + " and key " + preSharedKey + " " + context);
            throw new Status470HashInvalidException();
        }
        SW.stop();
    }

    private void validateExisting(PickAnyEntryContext context) throws Status409DuplicateSubmissionException {
        PickAnyEntry entry = entryRepository.findByEntryTransactionId(context.getRequest().getEntryTransactionId());
        if (entry != null) {
            context.setEntry(entry);
            throw new Status409DuplicateSubmissionException("Entry already exists");
        }
    }

}
