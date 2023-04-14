package lithium.service.casino.provider.iforium.controller;

import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.iforium.constant.Constants;
import lithium.service.casino.provider.iforium.model.request.CreateSessionTokenRequest;
import lithium.service.casino.provider.iforium.model.request.RedeemSessionTokenRequest;
import lithium.service.casino.provider.iforium.model.response.RedeemSessionTokenResponse;
import lithium.service.casino.provider.iforium.model.response.SessionTokenResponse;
import lithium.service.casino.provider.iforium.service.SessionService;
import lithium.service.casino.provider.iforium.util.SecurityConfigUtils;
import lithium.service.casino.provider.iforium.util.WebUtils;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.exceptions.Status412LoginEventNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static lithium.service.casino.provider.iforium.util.RequestUtils.getDomainNameFromPlayerGuid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SessionController {

    private final SessionService service;
    private final SecurityConfigUtils securityConfigUtils;

    @PostMapping("${api.iforium.session.redeem-token.path}")
    public RedeemSessionTokenResponse redeemToken(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization,
                                                  @RequestHeader(value = Constants.X_FORWARDED_FOR) String xForwardedFor,
                                                  @Valid @RequestBody RedeemSessionTokenRequest redeemSessionTokenRequest
    ) throws
            LithiumServiceClientFactoryException,
            Status412LoginEventNotFoundException,
            Status550ServiceDomainClientException,
            Status512ProviderNotConfiguredException {
        return service.redeemToken(redeemSessionTokenRequest, authorization, xForwardedFor);
    }

    @PostMapping("${api.iforium.session.create-token.path}")
    public SessionTokenResponse createToken(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization,
                                            @RequestHeader(value = Constants.X_FORWARDED_FOR) String xForwardedFor,
                                            @Valid @RequestBody CreateSessionTokenRequest createSessionTokenRequest
    ) throws
            LithiumServiceClientFactoryException,
            Status411UserNotFoundException,
            Status512ProviderNotConfiguredException {
        String domainName = getDomainNameFromPlayerGuid(createSessionTokenRequest.getOperatorAccountId());
        securityConfigUtils.validateSecurity(authorization, WebUtils.getFirstIpFromXForwardedFor(xForwardedFor), domainName);

        return service.createToken(createSessionTokenRequest);
    }
}
