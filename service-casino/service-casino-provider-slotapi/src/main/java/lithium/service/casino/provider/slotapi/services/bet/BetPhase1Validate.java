package lithium.service.casino.provider.slotapi.services.bet;

import lithium.metrics.SW;
import lithium.modules.ModuleInfo;
import lithium.service.casino.provider.slotapi.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.slotapi.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.slotapi.api.schema.bet.BetRequest;
import lithium.service.casino.provider.slotapi.config.ProviderConfig;
import lithium.service.casino.provider.slotapi.config.ProviderConfigService;
import lithium.service.casino.provider.slotapi.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.slotapi.context.BetContext;
import lithium.util.HmacSha256HashCalculator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BetPhase1Validate {

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    ModuleInfo moduleInfo;

    public void validate(BetContext context, BetRequest request, String domainName)
            throws
                Status422DataValidationError,
            Status500ProviderNotConfiguredException,
                Status470HashInvalidException {

        if (request.getRoundId() == null || request.getRoundId().length() == 0) {
            throw new Status422DataValidationError("A round ID must be specified");
        }

        ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), domainName);
        validateSha256(request, config.getHashPassword());
    }

    private void validateSha256(BetRequest request, String preSharedKey)
            throws Status470HashInvalidException {
        SW.start("validateSha256");
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(preSharedKey);
        hasher.addItem(request.getTransactionTimestamp());
        hasher.addItem(request.getBetTransactionId());
        hasher.addItem(request.getRoundId());
        String expectedHash = hasher.calculateHash();
        if (!expectedHash.equals(request.getSha256())) {
            log.warn("Expected " + expectedHash + " sha256 but got " + request.getSha256() + " " + request);
            log.warn("Hash calculated using " + hasher.getUnencryptedPayload() + " and key " + preSharedKey);
            throw new Status470HashInvalidException();
        }
        SW.stop();
    }

}
