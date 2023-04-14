package lithium.service.casino.provider.iforium.controller;

import lithium.exceptions.ErrorCodeException;
import lithium.service.casino.provider.iforium.constant.Constants;
import lithium.service.casino.provider.iforium.model.request.BalanceRequest;
import lithium.service.casino.provider.iforium.model.response.BalanceResponse;
import lithium.service.casino.provider.iforium.service.BalanceService;
import lithium.service.casino.provider.iforium.util.SecurityConfigUtils;
import lithium.service.casino.provider.iforium.util.WebUtils;
import lithium.service.client.LithiumServiceClientFactoryException;
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
public class BalanceController {

    private final BalanceService service;
    private final SecurityConfigUtils securityConfigUtils;

    @PostMapping("${api.iforium.balance}")
    public BalanceResponse balance(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization,
                                   @RequestHeader(value = Constants.X_FORWARDED_FOR) String xForwardedFor,
                                   @Valid @RequestBody BalanceRequest balanceRequest
    ) throws ErrorCodeException,
            LithiumServiceClientFactoryException {
        String domainName = getDomainNameFromPlayerGuid(balanceRequest.getOperatorAccountId());
        securityConfigUtils.validateSecurity(authorization, WebUtils.getFirstIpFromXForwardedFor(xForwardedFor), domainName);

        return service.balance(balanceRequest, domainName);
    }
}
