package lithium.service.casino.provider.incentive.services;

import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.incentive.api.exceptions.Status404NoSuchUserException;
import lithium.service.casino.provider.incentive.api.exceptions.Status500DomainConfigError;
import lithium.service.casino.provider.incentive.api.schema.validatesession.ValidateSessionResponse;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.objects.User;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lithium.math.CurrencyAmount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Slf4j
@Service
public class ValidateSessionService {

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

    @TimeThisMethod
    public ValidateSessionResponse validateSession(Principal principal, String locale) throws
            LithiumServiceClientFactoryException, Status404NoSuchUserException,
            Status500DomainConfigError, Status500UnhandledCasinoClientException,
            Status500LimitInternalSystemClientException, Status491PermanentSelfExclusionException,
            Status490SoftSelfExclusionException, Status496PlayerCoolingOffException {
        LithiumTokenUtil util = tokenService.getUtil(principal);

        User user = getUser(util.guid());
        Domain domain = getDomain(util.domainName());

        limits.checkPlayerRestrictions(util.guid(), locale);

        ValidateSessionResponse response = new ValidateSessionResponse();
        CurrencyAmount balance = CurrencyAmount.fromCents(casinoService.getPlayerBalance(util.domainName(), util.guid(),
                domain.getCurrency()).getBalanceCents());
        response.setBalance(balance);
        response.setCellphoneNumber(user.getCellphoneNumber());
        response.setCurrencyCode(domain.getCurrency());
        response.setGuid(util.guid());
        if (user.getUsername() != null) {
            response.setUsername(user.getUsername().toLowerCase());
        }
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

    private User getUser(String playerguid) throws LithiumServiceClientFactoryException, Status404NoSuchUserException {
        UserApiInternalClient cl = getUserService();
        Response<User> response = cl.getUser(playerguid);
        if (response.isSuccessful()) return response.getData();
        throw new Status404NoSuchUserException();
    }

    private UserApiInternalClient getUserService() throws LithiumServiceClientFactoryException {
        UserApiInternalClient cl = null;
        cl = services.target(UserApiInternalClient.class, "service-user", true);
        return cl;
    }
}
