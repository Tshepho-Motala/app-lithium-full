package lithium.service.casino.provider.sportsbook.api.controllers;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status404NoSuchUserException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status500DomainConfigError;
import lithium.service.casino.provider.sportsbook.api.schema.validatesession.CustomerInfoResponse;
import lithium.service.casino.provider.sportsbook.services.ValidateTokenService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.util.ExceptionMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Slf4j
public class ValidateTokenController {

    @Autowired
    private ValidateTokenService service;

    @PostMapping("/validatetoken")
    public CustomerInfoResponse validate(@RequestParam(defaultValue = "en_US") String locale, Principal principal)
            throws Status401UnAuthorisedException, Status404NoSuchUserException, Status405UserDisabledException,
            Status500UnhandledCasinoClientException, Status500DomainConfigError,
            Status500LimitInternalSystemClientException, Status491PermanentSelfExclusionException,
            Status490SoftSelfExclusionException, Status500InternalServerErrorException,
            Status496PlayerCoolingOffException, Status512ProviderNotConfiguredException {
        try {
            CustomerInfoResponse response = service.validateToken(principal, locale);
            log.info("validate " + principal + " " + response);
            return response;
        } catch (ErrorCodeException ec) {
            log.warn("validate errorcode " + ec + " " + principal);
            throw ec;
        } catch (Exception e) {
            log.error("validate error " + ExceptionMessageUtil.allMessages(e) + " " + principal, e);
            throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e), e);
        }
    }

}
