package lithium.service.casino.provider.iforium.service.impl;

import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.iforium.exception.SessionKeyExpiredException;
import lithium.service.casino.provider.iforium.exception.SessionTokenExpiredException;
import lithium.service.casino.provider.iforium.model.SessionTokenInfo;
import lithium.service.casino.provider.iforium.model.request.CreateSessionTokenRequest;
import lithium.service.casino.provider.iforium.model.request.RedeemSessionTokenRequest;
import lithium.service.casino.provider.iforium.model.response.ErrorCodes;
import lithium.service.casino.provider.iforium.model.response.RedeemSessionTokenResponse;
import lithium.service.casino.provider.iforium.model.response.Result;
import lithium.service.casino.provider.iforium.model.response.SessionTokenResponse;
import lithium.service.casino.provider.iforium.service.SessionService;
import lithium.service.casino.provider.iforium.util.NullSafetyUtils;
import lithium.service.casino.provider.iforium.util.SecurityConfigUtils;
import lithium.service.casino.provider.iforium.util.SessionUtils;
import lithium.service.casino.provider.iforium.util.WebUtils;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.exceptions.Status412LoginEventNotFoundException;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.system.SystemLoginEventsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    private final LithiumServiceClientFactory lithiumServiceClientFactory;
    private final CachingDomainClientService cachingDomainClientService;
    private final SecurityConfigUtils securityConfigUtils;

    private final long sessionTokenTtl;

    public SessionServiceImpl(LithiumServiceClientFactory lithiumServiceClientFactory,
                              CachingDomainClientService cachingDomainClientService,
                              SecurityConfigUtils securityConfigUtils,
                              @Value("${api.iforium.session.redeem-token.ttl}") Long sessionTokenTtl
    ) {
        this.lithiumServiceClientFactory = lithiumServiceClientFactory;
        this.cachingDomainClientService = cachingDomainClientService;
        this.securityConfigUtils = securityConfigUtils;
        this.sessionTokenTtl = sessionTokenTtl;
    }

    @Override
    public String wrapSessionToken(String sessionKey) {
        return SessionUtils.wrapSessionToken(sessionKey);
    }

    @Override
    public RedeemSessionTokenResponse redeemToken(RedeemSessionTokenRequest redeemSessionTokenRequest, String authorization,
                                                  String xForwardedFor
    ) throws
            LithiumServiceClientFactoryException,
            Status412LoginEventNotFoundException,
            Status550ServiceDomainClientException,
            Status512ProviderNotConfiguredException {
        SessionTokenInfo sessionTokenInfo = SessionUtils.decomposeSessionToken(redeemSessionTokenRequest.getSessionToken());
        validateIssuedTime(sessionTokenInfo.getIssuedTimeMillis());

        SystemLoginEventsClient systemLoginEventsClient = getSystemLoginEventsClient();
        LoginEvent loginEvent = systemLoginEventsClient.findBySessionKey(sessionTokenInfo.getSessionKey());

        String operatorAccountId = NullSafetyUtils.getUserGuid(loginEvent);
        String countryCode = NullSafetyUtils.getCountryCode(loginEvent);

        String domainName = NullSafetyUtils.getDomainName(loginEvent);

        securityConfigUtils.validateSecurity(authorization, WebUtils.getFirstIpFromXForwardedFor(xForwardedFor), domainName);

        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);

        String currencyCode = NullSafetyUtils.getCurrency(domain);

        return new RedeemSessionTokenResponse(
                ErrorCodes.SUCCESS.getCode(),
                Result.builder()
                      .operatorAccountId(operatorAccountId)
                      .countryCode(countryCode.toUpperCase())
                      .gatewaySessionToken(sessionTokenInfo.getSessionKey())
                      .currencyCode(currencyCode)
                      .build()
        );
    }

    @Override
    public SessionTokenResponse createToken(CreateSessionTokenRequest createSessionTokenRequest
    ) throws
            LithiumServiceClientFactoryException,
            Status411UserNotFoundException {
        SystemLoginEventsClient systemLoginEventsClient = getSystemLoginEventsClient();

        LoginEvent loginEvent = systemLoginEventsClient.getLastLoginEventForUser(createSessionTokenRequest.getOperatorAccountId());

        if (loginEvent.getLogout() != null && loginEvent.getLogout().before(Timestamp.valueOf(LocalDateTime.now()))) {
            throw new SessionKeyExpiredException(
                    String.format("SessionKey=%s for OperatorAccountId=%s is expired.", loginEvent.getSessionKey(),
                                  createSessionTokenRequest.getOperatorAccountId()));
        }

        return SessionUtils.buildSessionTokenResponse(wrapSessionToken(loginEvent.getSessionKey()));
    }

    private void validateIssuedTime(long issuedTimeMillis) {
        long elapsedTimeMillis = System.currentTimeMillis() - issuedTimeMillis;
        if (elapsedTimeMillis > sessionTokenTtl) {
            throw new SessionTokenExpiredException(
                    String.format("Elapsed time is greater than session token ttl, elapsedTime=%s, sessionTokenTtl=%s",
                                  elapsedTimeMillis,
                                  sessionTokenTtl)
            );
        }
    }

    private SystemLoginEventsClient getSystemLoginEventsClient() throws LithiumServiceClientFactoryException {
        return lithiumServiceClientFactory.target(SystemLoginEventsClient.class, "service-user", true);
    }
}
