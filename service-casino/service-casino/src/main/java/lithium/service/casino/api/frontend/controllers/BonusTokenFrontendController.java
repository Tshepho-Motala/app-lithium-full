package lithium.service.casino.api.frontend.controllers;

import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.service.casino.client.objects.PlayerBonusToken;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.service.BonusTokenService;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.tokens.LithiumTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@EnableCustomHttpErrorCodeExceptions
@AllArgsConstructor
public class BonusTokenFrontendController extends FrontendController {

    private BonusTokenService bonusTokenService;

    @PostMapping("/frontend/bonus-token/v1/list/active")
    @ResponseBody
    public List<PlayerBonusToken> getActivePlayerBonusTokenList(
            LithiumTokenUtil tokenUtil
    ) throws
            Status401UnAuthorisedException,
            Status405UserDisabledException,
            Status473DomainBettingDisabledException,
            Status490SoftSelfExclusionException,
            Status491PermanentSelfExclusionException,
            Status511UpstreamServiceUnavailableException,
            Status496PlayerCoolingOffException {

        allowedToTransact(tokenUtil);

        return bonusTokenService.findActiveBonusTokensForPlayer(tokenUtil.guid());
    }
}
