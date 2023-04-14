package lithium.service.casino.provider.roxor.api.controllers;

import lithium.modules.ModuleInfo;
import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.api.schema.auth.AuthenticatePlayerRequest;
import lithium.service.casino.provider.roxor.api.schema.auth.AuthenticatePlayerResponse;
import lithium.service.casino.provider.roxor.config.ProviderConfigService;
import lithium.service.casino.provider.roxor.services.AuthenticatePlayerService;
import lithium.service.client.LithiumServiceClientFactory;
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
public class AuthenticatePlayerController {
    @Autowired AuthenticatePlayerService authenticatePlayerService;
    @Autowired LithiumServiceClientFactory services;
    @Autowired ProviderConfigService providerConfigService;
    @Autowired ModuleInfo moduleInfo;

    @PostMapping("/authenticatePlayer")
    public @ResponseBody
    AuthenticatePlayerResponse authenticatePlayer(
            @RequestHeader(value = "SessionKey", required = false) String sessionKey,
            @RequestHeader(value = "X-Forward-For", required = false) String xForwardFor,
            @RequestBody AuthenticatePlayerRequest authenticatePlayerRequest
    ) throws
            Status400BadRequestException,
            Status401NotLoggedInException,
            Status500RuntimeException
    {
        log.info("authenticatePlayer request with SessionKey : " + sessionKey + " xForwardFor : " + xForwardFor +
                " and Body : " + authenticatePlayerRequest.toString());

        AuthenticatePlayerResponse response = authenticatePlayerService.authenticatePlayer(
                sessionKey,
                xForwardFor,
                authenticatePlayerRequest
        );

        log.info("authenticatePlayer response for SessionKey : " + sessionKey+ " with body " + response.toString());
        return response;
    }
}
