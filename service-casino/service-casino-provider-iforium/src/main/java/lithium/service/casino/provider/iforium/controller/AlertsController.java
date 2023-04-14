package lithium.service.casino.provider.iforium.controller;

import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.iforium.constant.Constants;
import lithium.service.casino.provider.iforium.model.request.AlertWalletCallbackNotificationRequest;
import lithium.service.casino.provider.iforium.model.response.Response;
import lithium.service.casino.provider.iforium.service.AlertsService;
import lithium.service.casino.provider.iforium.util.SecurityConfigUtils;
import lithium.service.casino.provider.iforium.util.WebUtils;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static lithium.service.casino.provider.iforium.util.RequestUtils.getDomainNameFromPlayerGuid;

@RequiredArgsConstructor
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AlertsController {

    private final AlertsService service;
    private final SecurityConfigUtils securityConfigUtils;

    @PostMapping("${api.iforium.alerts.alert-wallet-callback-notification}")
    public Response alertWalletCallbackNotification(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization,
                                                    @RequestHeader(value = Constants.X_FORWARDED_FOR) String xForwardedFor,
                                                    @Valid @RequestBody AlertWalletCallbackNotificationRequest alertWalletCallbackNotificationRequest
    ) throws Status512ProviderNotConfiguredException, LithiumServiceClientFactoryException, Status550ServiceDomainClientException {
        String domainName = getDomainNameFromPlayerGuid(alertWalletCallbackNotificationRequest.getOperatorAccountId());
        securityConfigUtils.validateSecurity(authorization, WebUtils.getFirstIpFromXForwardedFor(xForwardedFor), domainName);

        return service.alertWalletCallbackNotification(alertWalletCallbackNotificationRequest);
    }
}
