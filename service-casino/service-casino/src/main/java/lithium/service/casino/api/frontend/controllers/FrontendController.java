package lithium.service.casino.api.frontend.controllers;

import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@EnableCustomHttpErrorCodeExceptions
public abstract class FrontendController {

    @Autowired protected CachingDomainClientService cachingDomainClientService;
    @Autowired protected LimitInternalSystemService limits;
    @Autowired protected UserApiInternalClientService userApiInternalClientService;

    protected void allowedToTransact(LithiumTokenUtil tokenUtil
    ) throws
            Status401UnAuthorisedException,
            Status405UserDisabledException,
            Status473DomainBettingDisabledException,
            Status490SoftSelfExclusionException,
            Status491PermanentSelfExclusionException,
            Status511UpstreamServiceUnavailableException,
            Status496PlayerCoolingOffException {
        try {
            userApiInternalClientService.performUserChecks(tokenUtil.guid(), tokenUtil.getLocale().toString(),
                    tokenUtil.sessionId(), true, true, false);
            limits.checkPlayerRestrictions(tokenUtil.guid(), tokenUtil.getLocale().toString());
            cachingDomainClientService.checkBettingEnabled(tokenUtil.domainName(), tokenUtil.getLocale().toString());
        } catch (Status500UserInternalSystemClientException |
                 Status500LimitInternalSystemClientException |
                 Status550ServiceDomainClientException hardException) {
            log.error("Error performing transaction allowance lookup: " + hardException.getMessage());
            throw new Status511UpstreamServiceUnavailableException(hardException.getMessage());
        } catch (Status405UserDisabledException |
                 Status473DomainBettingDisabledException |
                 Status491PermanentSelfExclusionException |
                 Status490SoftSelfExclusionException softException) {
            log.debug("User not allowed to transact: " + softException.getMessage(), softException);
            throw softException;
        }
    }
}
