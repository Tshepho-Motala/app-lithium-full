package lithium.service.cashier.processor.mvend.services;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.metrics.CountThisMethod;
import lithium.metrics.TimeThisMethod;
import lithium.service.cashier.client.objects.DepositStatus;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.mvend.api.exceptions.Status900InvalidHashException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status901InvalidOrMissingParameters;
import lithium.service.cashier.processor.mvend.api.exceptions.Status901UserNotFoundException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status999GeneralFailureException;
import lithium.service.cashier.processor.mvend.context.DepositRequestContext;
import lithium.service.cashier.processor.mvend.context.DepositStatusRequestContext;
import lithium.service.cashier.processor.mvend.context.RequestContext;
import lithium.service.cashier.processor.mvend.services.shared.SharedService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.math.CurrencyAmount;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@Slf4j
public class DepositRequestService {

    @Autowired @Setter
    CashierInternalClientService cashierService;

    @Autowired @Setter
    UserApiInternalClientService userService;

    @Autowired @Setter
    SharedService sharedService;

    @TimeThisMethod
    @Retryable(exclude = NotRetryableErrorCodeException.class)
    public void deposit(
        DepositRequestContext context,
        boolean success
    ) throws
        Status900InvalidHashException,
        Status901InvalidOrMissingParameters,
        Status901UserNotFoundException,
        Status999GeneralFailureException {
        try {
            sharedService.getPropertiesDMPFromServiceCashier(context, true, "mvend");
            sharedService.getProcessingDMPFromServiceCashier(context, true, "mvend", context.getRequest().getProcessor());
            sharedService.validateUsernameAndPassword(context);
            validateAmount(context);
            sharedService.validateHash(context);
            sharedService.getUserFromServiceUser(context);
            registerDepositWithCashier(context, success);
        } catch (UserClientServiceFactoryException e) {
            throw new Status999GeneralFailureException("Service user error: " + ExceptionMessageUtil.allMessages(e));
        } catch (UserNotFoundException e) {
            throw new Status901UserNotFoundException();
        }
    }

    private void validateAmount(DepositRequestContext context) throws Status901InvalidOrMissingParameters {
        try {
            CurrencyAmount amount = CurrencyAmount.fromAmountString(context.getRequest().getAmount());
            context.setAmountInCents(amount.toCents());
        } catch (Exception e) {
            log.warn("Unable to parse amount " + e.getMessage() + " " + context);
            throw new Status901InvalidOrMissingParameters("Invalid amount");
        }
    }

    @CountThisMethod
    private void registerDepositWithCashier(DepositRequestContext context, boolean success) throws Status999GeneralFailureException {
        try {
            context.setCashierReferenceNumber(
                cashierService.registerDeposit(
                    context.getProcessingDmp().getId(),
                    context.getUserGuid(),
                    context.getRequest().getCurrency(),
                    context.getAmountInCents(),
                    context.getRequest().getReference(),
                    null,
                    context.getSessionId(),
                    success,
                    null
                )
            );
        } catch (Exception e) {
            log.error("Register deposit failed " + context, e);
            throw new Status999GeneralFailureException("Register deposit failed " + e.getMessage());
        }
    }

    public void opayDepositStatus(DepositStatusRequestContext context) throws Status901InvalidOrMissingParameters, Status999GeneralFailureException, Status900InvalidHashException {
        sharedService.getProcessingDMPFromServiceCashier(context, true, "mvend", "opay");
        String payload = context.getTimestamp() + context.getNetwork_ref();
        validateSignature(context, payload);
        if ((context.getNetwork_ref() == null) ||
                (context.getNetwork_ref().length() < 1)) {
            throw new Status901InvalidOrMissingParameters("networkRef should not be empty");
        }

        try {
            DepositStatus depositStatus =
                    cashierService.getDepositStatus(context.getNetwork_ref(), "mvend");
                context.setDepositStatus(depositStatus);
        } catch (Exception e) {
            String message = "Could not find opay transaction in cashier with this reference: "
                    + context.getNetwork_ref() + " " + ExceptionMessageUtil.allMessages(e);
            log.error(message, e);
            throw new Status901InvalidOrMissingParameters(message);
        }
    }

    private void validateSignature(RequestContext context, String payload) throws Status999GeneralFailureException, Status900InvalidHashException {
        String rsa_pub = context.getProcessingDmp().getProperties().get("mvend_rsa_pub");
        boolean validSignature = verifySHA1withRSASignature(rsa_pub, context.getHash(), payload);
        if (!validSignature) {
            log.warn("Can't validate signature " + context.getHash() + " using payload " + payload);
            throw new Status900InvalidHashException();
        }
    }

    private boolean verifySHA1withRSASignature(String pub, String sign, String src) throws Status999GeneralFailureException {
        try {
            Signature sigEng = Signature.getInstance("SHA1withRSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(pub));
            KeyFactory fac = KeyFactory.getInstance("RSA");
            RSAPublicKey rsaPubKey = (RSAPublicKey) fac.generatePublic(keySpec);
            sigEng.initVerify(rsaPubKey);
            sigEng.update(src.getBytes());
            return sigEng.verify(Base64.getDecoder().decode(sign));
        } catch (Exception e) {
            throw new Status999GeneralFailureException("Could not calculate signature " + ExceptionMessageUtil.allMessages(e));
        }
    }

}
