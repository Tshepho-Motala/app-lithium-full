package lithium.service.casino.provider.incentive.services.placement;

import lithium.metrics.SW;
import lithium.modules.ModuleInfo;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.objects.PlayerBonusToken;
import lithium.service.casino.exceptions.Status423InvalidBonusTokenException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.incentive.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.incentive.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.incentive.api.schema.placement.request.PlacementRequest;
import lithium.service.casino.provider.incentive.api.schema.placement.request.PlacementRequestBet;
import lithium.service.casino.provider.incentive.config.ProviderConfig;
import lithium.service.casino.provider.incentive.config.ProviderConfigService;
import lithium.service.casino.provider.incentive.config.Status500ProviderNotConfiguredException;
import lithium.util.HmacSha256HashCalculator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PlacementPhase1Validate {

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @Autowired @Setter
    private CasinoClientService casinoClientService;

    public void validate(PlacementRequest request, String domainName)
            throws
                Status422DataValidationError,
            Status500ProviderNotConfiguredException,
                Status470HashInvalidException {
        if (request.getBets() == null || request.getBets().size() == 0) {
            throw new Status422DataValidationError("At least one bet needs to be specified");
        }
        if (request.getBets().size() > 1) {
            throw new Status422DataValidationError("Multiple bets are not currently supported");
        }

        ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), domainName);

        validateSha256(request, config.getHashPassword());
    }

    private void validateSha256(PlacementRequest request, String preSharedKey)
            throws Status470HashInvalidException {
        SW.start("validateSha256");
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(preSharedKey);
        hasher.addItem(request.getUserId());
        for (PlacementRequestBet bet : request.getBets()) {
            hasher.addItem(bet.getBetTransactionId());
        }
        String expectedHash = hasher.calculateHash();
        if (!expectedHash.equals(request.getSha256())) {
            log.warn("Expected " + expectedHash + " sha256 but got " + request.getSha256() + " " + request);
            log.warn("Hash calculated using " + hasher.getUnencryptedPayload() + " and key " + preSharedKey);
            throw new Status470HashInvalidException();
        }
        SW.stop();
    }

    public void validateVirtualCoin(PlacementRequest placementRequest, String playerGuid)
            throws
            Status422DataValidationError,
            Status500UnhandledCasinoClientException {
        try {
            for (PlacementRequestBet bet : placementRequest.getBets())
                if (bet.getVirtualCoinId() != null)  {
                    PlayerBonusToken playerBonusToken =
                            casinoClientService.validateBonusToken(playerGuid, bet.getVirtualCoinId());
                    if (bet.getTotalOdds() < playerBonusToken.getMinimumOdds()) {
                        throw new Status422DataValidationError("Minimum odds requirement not met for virtual coin bet: " + playerBonusToken.getMinimumOdds());
                    }
                    if (bet.getTotalStake() > 0.0) {
                        throw new Status422DataValidationError("Virtual coin transactions can not have a stake value greater that zero: " + bet.getTotalStake());
                    }
                }
        } catch (Status423InvalidBonusTokenException e) {
            throw new Status422DataValidationError(e.getMessage());
        }
    }
}
