package lithium.service.casino.provider.slotapi.services.betresult;

import lithium.metrics.SW;
import lithium.modules.ModuleInfo;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.slotapi.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status474BetRoundNotFoundException;
import lithium.service.casino.provider.slotapi.api.schema.betresult.BetResultRequest;
import lithium.service.casino.provider.slotapi.config.ProviderConfig;
import lithium.service.casino.provider.slotapi.config.ProviderConfigService;
import lithium.service.casino.provider.slotapi.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.slotapi.context.BetResultContext;
import lithium.service.casino.provider.slotapi.storage.entities.BetRound;
import lithium.service.casino.provider.slotapi.storage.repositories.BetRoundRepository;
import lithium.util.HmacSha256HashCalculator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BetResultPhase1Validate {

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    ModuleInfo module;

    @Autowired @Setter
    BetRoundRepository betRoundRepository;

    public void validate(BetResultContext context) throws
        Status422DataValidationError,
        Status470HashInvalidException,
        Status474BetRoundNotFoundException,
        Status500ProviderNotConfiguredException,
        Status500UnhandledCasinoClientException
    {

        SW.start("findBetRound");
        BetRound round = betRoundRepository.findByGuid(context.getRequest().getRoundId());
        if (round == null) {
            throw new Status474BetRoundNotFoundException();
        }
        SW.stop();

        context.setDomainName(round.getUser().getDomain().getName());
        context.setUserGuid(round.getUser().getGuid());
        context.setBetRound(round);

        if (context.getRequest().getSequenceNumber() != round.getSequenceNumber() + 1) {
            throw new Status500UnhandledCasinoClientException("Sequence number out of order for this round. Expected : "+(round.getSequenceNumber() + 1));
        }

        SW.start("getproviderconfig");
        ProviderConfig config = configService.getConfig(module.getModuleName(), context.getDomainName());
        SW.stop();

        validateSha256(context.getRequest(), config.getHashPassword());
        validateKind(context, context.getRequest());
    }

    private void validateKind(BetResultContext context, BetResultRequest request) throws Status422DataValidationError {
        switch (request.getKind()) {
            case WIN:
            case FREE_WIN:
                if (request.getReturns() == 0.0) {
                    throw new Status422DataValidationError("Returns may not be 0 on a WIN");
                }
                break;
            case LOSS:
            case FREE_LOSS:
                if (request.getReturns() != 0.0) {
                    throw new Status422DataValidationError("Returns should be 0 when LOST");
                }
                break;
            case VOID:
                //TODO do we calculate the total bet - wins so far and ensure that the amount matches?
                if (request.getReturns() == 0.0) {
                    throw new Status422DataValidationError("Returns may not be 0 on a VOID");
                }
                break;
            default:
                throw new Status422DataValidationError("Invalid result (should be WIN, LOSS, FREE_WIN, FREE_LOSS or VOID): " + request.getKind());
        }
    }

    private void validateSha256(BetResultRequest request, String preSharedKey)
            throws Status470HashInvalidException {
        SW.start("validateSha256");
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(preSharedKey);
        hasher.addItem(request.getTransactionTimestamp());
        hasher.addItem(request.getBetResultTransactionId());
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
