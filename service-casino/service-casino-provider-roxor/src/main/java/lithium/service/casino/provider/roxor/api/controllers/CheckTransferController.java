package lithium.service.casino.provider.roxor.api.controllers;

import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status404NotFoundException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.api.schema.checktransfer.CheckTransferRequest;
import lithium.service.casino.provider.roxor.api.schema.checktransfer.CheckTransferResponse;
import lithium.service.casino.provider.roxor.services.CheckTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rgp")
public class CheckTransferController {
    @Autowired CheckTransferService checkTransferService;

    @PostMapping("/transfer")
    public @ResponseBody
    CheckTransferResponse checkTransfer(
            @RequestHeader(value = "SessionKey", required = false) String sessionKey,
            @RequestHeader(value = "X-Forward-For", required = false) String xForwardFor,
            @RequestBody CheckTransferRequest checkTransferRequest
    ) throws
            Status400BadRequestException,
            Status401NotLoggedInException,
            Status404NotFoundException,
            Status500RuntimeException
    {
        log.info("checkTransfer request with SessionKey : " + sessionKey + " xForwardFor : " + xForwardFor +
                " and Body : " + checkTransferRequest.toString());

        CheckTransferResponse checkTransferResponse = checkTransferService.checkTransfer(
                sessionKey,
                xForwardFor,
                checkTransferRequest
        );

        log.info("checkTransfer response for SessionKey : " + sessionKey+ " with body " + checkTransferResponse.toString());
        return checkTransferResponse;
    }
}
