package lithium.service.cashier.processor.mvend.services;

import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.mvend.api.exceptions.Status101UserNotFoundException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status106InvalidUserIDOrKeyException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status107InvalidHashException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status900InvalidHashException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status901InvalidOrMissingParameters;
import lithium.service.cashier.processor.mvend.api.exceptions.Status999GeneralFailureException;
import lithium.service.cashier.processor.mvend.context.BalanceRequestContext;
import lithium.service.cashier.processor.mvend.services.shared.SharedService;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.util.ExceptionMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@Slf4j
public class BalanceRequestService {

    @Autowired
    CashierInternalClientService cashierService;

    @Autowired
    UserApiInternalClientService userService;

    @Autowired
    AccountingClientService accountingService;

    @Autowired
    CachingDomainClientService cachingDomainClientService;

    @Autowired
    SharedService sharedService;

    @Autowired
    LimitInternalSystemService limits;

    @TimeThisMethod
    public void balance(BalanceRequestContext context) throws
            Status101UserNotFoundException, Status999GeneralFailureException, Status107InvalidHashException,
            Status106InvalidUserIDOrKeyException, Status491PermanentSelfExclusionException,
            Status490SoftSelfExclusionException, Status496PlayerCoolingOffException {

        try {

            sharedService.getPropertiesDMPFromServiceCashier(context, true, "mvend");
            sharedService.validateUsernameAndPassword(context);
            sharedService.validateHash(context);
            sharedService.getUserFromServiceUser(context);

            //TODO retrieve locale from user
            limits.checkPlayerRestrictions(context.getUserGuid(), "en_US");

            getCurrencyCodeFromServiceDomain(context);
            getBalanceFromServiceAccounting(context);

        } catch (Status900InvalidHashException e) {
            throw new Status107InvalidHashException();
        } catch (Status901InvalidOrMissingParameters status901InvalidOrMissingParameters) {
            throw new Status106InvalidUserIDOrKeyException();
        } catch (UserNotFoundException e) {
            throw new Status101UserNotFoundException();
        } catch (UserClientServiceFactoryException e) {
            log.error(e.getMessage(), e);
            throw new Status999GeneralFailureException("Service user error: " + ExceptionMessageUtil.allMessages(e));
        } catch (Status500LimitInternalSystemClientException e) {
            log.error(e.getMessage(), e);
            throw new Status999GeneralFailureException("Service limit error: " + ExceptionMessageUtil.allMessages(e));
        }
    }

    private void getCurrencyCodeFromServiceDomain(BalanceRequestContext context) throws Status999GeneralFailureException {
        try {
            Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(context.getGroupRef());
            context.setCurrencyCode(domain.getCurrency());
        } catch (Exception e) {
            throw new Status999GeneralFailureException("Domain service error: " + ExceptionMessageUtil.allMessages(e));
        }
    }

    private void getBalanceFromServiceAccounting(BalanceRequestContext context) throws Status999GeneralFailureException {
        try {
            Response<Long> response = accountingService.getBalance(context.getCurrencyCode(), context.getGroupRef(), context.getUserGuid());
            if (!response.isSuccessful()) {
                throw new Exception("Accounting service error " + response.getStatus() + " " + response.getMessage());
            }
            if (response.getData() == null) {
                context.setBalanceInCents(0L);
            }
            context.setBalanceInCents(response.getData());
        } catch (Exception e) {
            throw new Status999GeneralFailureException("Accounting service error: " + ExceptionMessageUtil.allMessages(e));
        }
    }


}
