package lithium.service.casino.provider.iforium.service.impl;

import lithium.exceptions.ErrorCodeException;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.provider.iforium.model.request.BalanceRequest;
import lithium.service.casino.provider.iforium.model.response.BalanceResponse;
import lithium.service.casino.provider.iforium.service.BalanceService;
import lithium.service.casino.provider.iforium.util.LithiumClientUtils;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.system.SystemLoginEventsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static lithium.service.casino.provider.iforium.util.BalanceUtils.buildBalanceResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final CachingDomainClientService cachingDomainClientService;
    private final CasinoClientService casinoClientService;
    private final LithiumClientUtils lithiumClientUtils;

    public BalanceResponse balance(BalanceRequest balanceRequest, String domainName) throws ErrorCodeException, LithiumServiceClientFactoryException {
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);

        checkIfUserExists(balanceRequest.getOperatorAccountId());

        BigDecimal balance = casinoClientService.getPlayerBalance(
                domain.getName(),
                balanceRequest.getOperatorAccountId(),
                domain.getCurrency()
        ).getBalance();

        return buildBalanceResponse(domain, balance);
    }

    private void checkIfUserExists(String operatorAccountId) throws Status411UserNotFoundException, LithiumServiceClientFactoryException {
        SystemLoginEventsClient systemLoginEventsClient = lithiumClientUtils.getSystemLoginEventsClient();
        systemLoginEventsClient.getLastLoginEventForUser(operatorAccountId);
    }
}
