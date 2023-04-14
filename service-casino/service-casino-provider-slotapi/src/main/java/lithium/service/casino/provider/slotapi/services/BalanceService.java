package lithium.service.casino.provider.slotapi.services;

import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status404NoSuchUserException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status500DomainConfigError;
import lithium.service.casino.provider.slotapi.api.schema.balance.BalanceResponse;
import lithium.service.casino.provider.slotapi.api.schema.validatesession.ValidateSessionResponse;
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
public class BalanceService {

    @Autowired
    private CasinoClientService casinoService;

    @Autowired
    private CachingDomainClientService cachingDomainClientService;

    @Autowired
    private LithiumTokenUtilService tokenService;

    @Autowired
    private LimitInternalSystemService limits;

    @TimeThisMethod
    public BalanceResponse balance(Principal principal, String locale) throws
            Status500DomainConfigError, Status500UnhandledCasinoClientException,
            Status500LimitInternalSystemClientException, Status491PermanentSelfExclusionException,
            Status490SoftSelfExclusionException, Status496PlayerCoolingOffException {

        LithiumTokenUtil util = tokenService.getUtil(principal);
        Domain domain = getDomain(util.domainName());
        limits.checkPlayerRestrictions(util.guid(), locale);

        BalanceResponse response = new BalanceResponse();
        Long balanceCents = casinoService.getPlayerBalance(util.domainName(), util.guid(),
                domain.getCurrency()).getBalanceCents();
        response.setBalance(CurrencyAmount.fromCents(balanceCents).toAmount().doubleValue());
        response.setCurrencyCode(domain.getCurrency());
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

}
