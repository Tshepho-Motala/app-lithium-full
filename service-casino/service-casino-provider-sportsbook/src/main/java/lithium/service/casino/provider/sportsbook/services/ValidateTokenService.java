package lithium.service.casino.provider.sportsbook.services;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.math.CurrencyAmount;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status404NoSuchUserException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status500DomainConfigError;
import lithium.service.casino.provider.sportsbook.api.schema.validatesession.CustomerInfoResponse;
import lithium.service.casino.provider.sportsbook.shared.service.GuidConverterService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Slf4j
@Service
public class ValidateTokenService {
    @Autowired
    GuidConverterService guidConverterService;

    @Autowired
    private CasinoClientService casinoService;

    @Autowired
    private CachingDomainClientService cachingDomainClientService;

    @Autowired
    private LithiumTokenUtilService tokenService;

    @Autowired
    private LithiumServiceClientFactory services;

    @Autowired
    private LimitInternalSystemService limits;

    @Autowired
    private UserApiInternalClientService userApiInternalClientService;

    @TimeThisMethod
    public CustomerInfoResponse validateToken(Principal principal, String locale) throws
            LithiumServiceClientFactoryException, Status401UnAuthorisedException, Status404NoSuchUserException,
            Status405UserDisabledException, Status500DomainConfigError, Status500UnhandledCasinoClientException,
            Status500LimitInternalSystemClientException, Status491PermanentSelfExclusionException,
            Status490SoftSelfExclusionException, Status496PlayerCoolingOffException,
            Status512ProviderNotConfiguredException {
        SW.start("token");
        LithiumTokenUtil util = tokenService.getUtil(principal);
        SW.stop();
        SW.start("user");
        // We needed to add the user checks in here, i.e enabled, session timeout, etc.
        User user = null;
        try {
            user = userApiInternalClientService.performUserChecks(util.guid(), locale, util.sessionId(),
                    true, true, false);
        } catch (Status500UserInternalSystemClientException e) {
            throw new Status404NoSuchUserException();
        } catch (Status401UnAuthorisedException | Status405UserDisabledException e) {
            throw e;
        }
        SW.stop();
        SW.start("domain");
        Domain domain = getDomain(util.domainName());
        SW.stop();
        SW.start("limit");
        limits.checkPlayerRestrictions(util.guid(), locale);
        SW.stop();
        SW.start("casino.balance");
        CurrencyAmount balance = CurrencyAmount.fromCents(casinoService.getPlayerBalance(util.domainName(), util.guid(), domain.getCurrency()).getBalanceCents());
        SW.stop();

        CustomerInfoResponse response = new CustomerInfoResponse();

        response.setGuid(guidConverterService.convertFromLithiumToSportbook(util.guid()));
        response.setUsername(StringUtil.nullSafeToLowerCase(user.getUsername()));
        response.setCity((user.getResidentialAddress() != null) ? user.getResidentialAddress().getCity() : null);
        response.setCountry(StringUtil.allowNull(() -> domain.getDefaultCountry()));
        response.setSessionId(util.sessionId());
        response.setCurrencyCode(domain.getCurrency());
        response.setBalance(balance);

        return response;
    }

    private Domain getDomain(String domainName) throws Status500DomainConfigError {
        try {
            Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
            return domain;
        } catch (Exception e) {
            throw new Status500DomainConfigError(e);
        }
    }

//    private User getUser(String playerguid) throws LithiumServiceClientFactoryException, Status404NoSuchUserException {
//        UserApiInternalClient cl = getUserService();
//        Response<User> response = cl.getUser(playerguid);
//        if (response.isSuccessful()) return response.getData();
//        throw new Status404NoSuchUserException();
//    }
//
//    private UserApiInternalClient getUserService() throws LithiumServiceClientFactoryException {
//        UserApiInternalClient cl = null;
//        cl = services.target(UserApiInternalClient.class, "service-user", true);
//        return cl;
//    }
}
