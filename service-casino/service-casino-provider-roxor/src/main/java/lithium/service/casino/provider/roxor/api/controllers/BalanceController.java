package lithium.service.casino.provider.roxor.api.controllers;

import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.api.schema.SuccessResponse;
import lithium.service.casino.provider.roxor.api.schema.balance.GetPlayerBalanceRequest;
import lithium.service.casino.provider.roxor.services.BalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rgp")
public class BalanceController {
    @Autowired BalanceService service;

    @PostMapping("/balance")
    public @ResponseBody
    SuccessResponse balance(
            @RequestHeader(value = "SessionKey", required = false) String sessionKey,
            @RequestHeader(value = "X-Forward-For", required = false) String xForwardFor,
            @RequestBody GetPlayerBalanceRequest playerBalanceRequest
    ) throws
            Status400BadRequestException,
            Status401NotLoggedInException,
            Status500RuntimeException
    {
        log.info("balanceEnquiry request with SessionKey : " + sessionKey + " xForwardFor : " + xForwardFor +
                " and Body : " + playerBalanceRequest.toString());

        SuccessResponse response = service.balance(
                sessionKey,
                xForwardFor,
                playerBalanceRequest
        );

        log.info("balanceEnquiry response for SessionKey : " + sessionKey+ " with body " + response.toString());
        return response;
    }
}
