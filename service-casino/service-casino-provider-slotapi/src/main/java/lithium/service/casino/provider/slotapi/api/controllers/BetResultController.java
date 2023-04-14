package lithium.service.casino.provider.slotapi.api.controllers;

import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.slotapi.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status474BetRoundNotFoundException;
import lithium.service.casino.provider.slotapi.api.schema.betresult.BetResultRequest;
import lithium.service.casino.provider.slotapi.api.schema.betresult.BetResultResponse;
import lithium.service.casino.provider.slotapi.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.slotapi.services.BetResultService;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BetResultController {
    @Autowired @Setter
    BetResultService service;

    @PostMapping("/betresult")
    public BetResultResponse settlement(
        @RequestBody BetResultRequest request
    ) throws
        Status422DataValidationError,
        Status470HashInvalidException,
        Status474BetRoundNotFoundException,
        Status500UnhandledCasinoClientException,
        Status500ProviderNotConfiguredException
    {
        try {
            BetResultResponse response = service.betResult(request);
            log.debug("betresult " + request + " " + response);
            return response;
        } catch (Exception e) {
            log.warn("betresult error " + ExceptionMessageUtil.allMessages(e) + " " + request);
            throw e;
        }
    }
}
