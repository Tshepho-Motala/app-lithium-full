package lithium.service.casino.provider.incentive.api.controllers;

import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.incentive.api.exceptions.Status404BetTransactionNotFoundException;
import lithium.service.casino.provider.incentive.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.incentive.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.incentive.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.incentive.api.schema.settlement.SettlementRequest;
import lithium.service.casino.provider.incentive.api.schema.settlement.SettlementResponse;
import lithium.service.casino.provider.incentive.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.incentive.services.PubSubVirtualService;
import lithium.service.casino.provider.incentive.services.SettlementService;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Slf4j
public class SettlementController {

    @Autowired @Setter
    SettlementService settlementService;

    @PostMapping("/settlement")
    public SettlementResponse settlement(
        @RequestBody SettlementRequest request,
        Principal principal
    ) throws
        Status404BetTransactionNotFoundException,
        Status409DuplicateSubmissionException,
        Status422DataValidationError,
        Status470HashInvalidException,
        Status500UnhandledCasinoClientException,
        Status500ProviderNotConfiguredException
    {
        try {
            SettlementResponse response = settlementService.settle(request);
            log.debug("settlement " + request + " " + response);
            return response;
        } catch (Exception e) {
            log.warn("settlement error " + ExceptionMessageUtil.allMessages(e) + " " + request);
            throw e;
        }
    }
}
