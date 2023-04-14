package lithium.service.casino.provider.slotapi.api.controllers;

import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status404NoSuchUserException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status500DomainConfigError;
import lithium.service.casino.provider.slotapi.api.schema.balance.BalanceResponse;
import lithium.service.casino.provider.slotapi.api.schema.validatesession.ValidateSessionResponse;
import lithium.service.casino.provider.slotapi.services.BalanceService;
import lithium.service.casino.provider.slotapi.services.ValidateSessionService;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.util.ExceptionMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Slf4j
public class BalanceController {

    @Autowired
    private BalanceService service;

    @GetMapping("/balance")
    public BalanceResponse balance(@RequestParam(defaultValue = "en_US") String locale, Principal principal)
            throws Status500UnhandledCasinoClientException,
            Status500DomainConfigError, Status500LimitInternalSystemClientException,
            Status491PermanentSelfExclusionException, Status490SoftSelfExclusionException,
            Status496PlayerCoolingOffException {
        try {
            BalanceResponse response = service.balance(principal, locale);
            log.debug("balance " + principal + " " + response);
            return response;
        } catch (Exception e) {
            log.warn("balance error " + ExceptionMessageUtil.allMessages(e) + " " + principal);
            throw e;
        }
    }

}
