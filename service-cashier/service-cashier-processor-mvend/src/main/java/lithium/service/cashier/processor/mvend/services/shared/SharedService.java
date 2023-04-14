package lithium.service.cashier.processor.mvend.services.shared;

import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.mvend.api.exceptions.Status900InvalidHashException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status901InvalidOrMissingParameters;
import lithium.service.cashier.processor.mvend.api.exceptions.Status999GeneralFailureException;
import lithium.service.cashier.processor.mvend.context.RequestContext;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.util.ExceptionMessageUtil;
import lithium.util.Hash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SharedService {

    @Autowired
    CashierInternalClientService cashierService;

    @Autowired
    UserApiInternalClientService userService;

    public void getPropertiesDMPFromServiceCashier(RequestContext context, boolean deposit,
            String methodCode) throws Status999GeneralFailureException {
        try {
            DomainMethodProcessor dmp = cashierService.processorByMethodCodeAndProcessorDescription(
                    context.getGroupRef(), deposit,
                    methodCode, "default");

            log.debug("Received properties processor config: " + dmp);
            if (dmp.getProperties().size() == 0) {
                throw new Status999GeneralFailureException("Invalid processor configuration");
            }
            context.setPropertiesDmp(dmp);
        } catch (Exception e) {
            log.error("Error trying to call cashier client: " + ExceptionMessageUtil.allMessages(e), e);
            throw new Status999GeneralFailureException(ExceptionMessageUtil.allMessages(e));
        }
    }

    public void getProcessingDMPFromServiceCashier(RequestContext context, boolean deposit,
                                                String methodCode, String processorDescription) throws Status999GeneralFailureException {
        try {
            DomainMethodProcessor dmp = cashierService.processorByMethodCodeAndProcessorDescription(
                    context.getGroupRef(), deposit,
                    methodCode, processorDescription);

            log.debug("Received processing processor config: " + dmp);
            context.setProcessingDmp(dmp);
        } catch (Exception e) {
            log.error("Error trying to call cashier client: " + ExceptionMessageUtil.allMessages(e), e);
            throw new Status999GeneralFailureException(ExceptionMessageUtil.allMessages(e));
        }
    }

    public void validateUsernameAndPassword(RequestContext context) throws Status901InvalidOrMissingParameters {
        if (!context.getUsername().equals(context.getPropertiesDmp().getProperties().get("username"))) {
            log.warn("Invalid username, expected " + context.getPropertiesDmp().getProperties().get("username") + " " + context);
            throw new Status901InvalidOrMissingParameters("Invalid username");
        }
        if (!context.getPassword().equals(context.getPropertiesDmp().getProperties().get("password"))) {
            log.warn("Invalid password, expected " + context.getPropertiesDmp().getProperties().get("password") + " " + context);
            throw new Status901InvalidOrMissingParameters("Invalid password");
        }
    }

    public void validateHash(RequestContext context) throws Status900InvalidHashException, Status999GeneralFailureException {

        String expectedHash = null;
        String payload = context.getMsisdn() + "|" +
                context.getPropertiesDmp().getProperties().get("secret_key") + "|" +
                context.getTimestamp();

        try {
            expectedHash = Hash.builderMd5(payload).md5();
        } catch (Exception e) {
            throw new Status999GeneralFailureException("Could not calculate hash" + ExceptionMessageUtil.allMessages(e));
        }

        if (!expectedHash.equals(context.getHash())) {
            log.warn("Expected hash " + expectedHash + " but received " + context.getHash() + " using payload " +
                    payload);
            throw new Status900InvalidHashException();
        }
    }

    public void getUserFromServiceUser(RequestContext context) throws UserNotFoundException, UserClientServiceFactoryException {
        User user = null;
        user = userService.getUserByCellphoneNumber(context.getGroupRef(), context.getMsisdn());
        Long sessionId = (user.getSession() != null) ? user.getSession().getId() : user.getLastLogin().getId();
        context.setSessionId(sessionId);
        context.setUserGuid(user.guid());
        context.setFirstName(user.getFirstName());
        log.debug("Found user " + user);
    }

}
