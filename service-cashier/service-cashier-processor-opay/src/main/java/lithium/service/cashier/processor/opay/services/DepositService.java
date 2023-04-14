package lithium.service.cashier.processor.opay.services;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.metrics.CountThisMethod;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.cashier.client.objects.DepositStatus;
import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.opay.api.v2.schema.DepositRequest;
import lithium.service.cashier.processor.opay.context.BalanceRequestContext;
import lithium.service.cashier.processor.opay.context.DepositRequestContext;
import lithium.service.cashier.processor.opay.context.DepositStatusRequestContext;
import lithium.service.cashier.processor.opay.context.RequestContext;
import lithium.service.cashier.processor.opay.exceptions.Status900InvalidSignatureException;
import lithium.service.cashier.processor.opay.exceptions.Status901InvalidOrMissingParameters;
import lithium.service.cashier.processor.opay.exceptions.Status902UserNotFoundException;
import lithium.service.cashier.processor.opay.exceptions.Status903ReferenceExistsException;
import lithium.service.cashier.processor.opay.exceptions.Status906DepositNotAllowed;
import lithium.service.cashier.processor.opay.exceptions.Status907UserSelfExcluded;
import lithium.service.cashier.processor.opay.exceptions.Status999GeneralFailureException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.DepositLimitClient;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.Access;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.translate.client.objects.RestrictionError;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.Status;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.math.CurrencyAmount;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@Slf4j
public class DepositService {

    @Autowired
    @Setter
    CashierInternalClientService cashierService;

    @Autowired
    @Setter
    UserApiInternalClientService userService;

    @Autowired
    @Setter
    AccountingClientService accountingService;

    @Autowired
    @Setter
    CachingDomainClientService cachingDomainClientService;

    @Autowired
    @Setter
    LithiumServiceClientFactory serviceFactory;

    @Autowired
    @Setter
    LimitInternalSystemService limits;

    @Autowired
    @Setter
    MessageSource messageSource;

    @TimeThisMethod
    public void balance(BalanceRequestContext context) throws Status999GeneralFailureException, Status900InvalidSignatureException, Status906DepositNotAllowed, Status907UserSelfExcluded, Status902UserNotFoundException {
        log.info("Start validating user: " + context);

        getPropertiesDMPFromServiceCashier(context, true, "opay");

        String payload = context.getTimestamp() + context.getMsisdn();
        validateSignature(context, payload);
        getUserFromServiceUser(context);
        //TODO retrieve locale from user
        try {
            Access access = limits.checkAccess(context.getUserGuid());
            if (!access.isDepositAllowed()) {
                log.warn("Deposit not allowed for user=" + context.getUserGuid());
                throw new Status906DepositNotAllowed(RestrictionError.DEPOSIT.getResponseMessageLocal(messageSource, context.getUserGuid().split("/")[0], access.getDepositErrorMessage()));
            }
            limits.checkPlayerRestrictions(context.getUserGuid(), "en_US");
        } catch (Status496PlayerCoolingOffException e) {
            log.warn("User " + context.getUserGuid() + " cooling off: " + ExceptionMessageUtil.allMessages(e));
            throw new Status907UserSelfExcluded("User cooling off");
        } catch (Status491PermanentSelfExclusionException e) {
            log.warn("User " + context.getUserGuid() + " permanent self excluded: " + ExceptionMessageUtil.allMessages(e));
            throw new Status907UserSelfExcluded("User permanent self excluded");
        } catch (Status490SoftSelfExclusionException e) {
            log.warn("User " + context.getUserGuid() + " soft self excluded: " + ExceptionMessageUtil.allMessages(e));
            throw new Status907UserSelfExcluded("User soft self excluded");
        } catch (Status500LimitInternalSystemClientException e) {
            log.warn("Limit internal system client service error: " + ExceptionMessageUtil.allMessages(e));
            throw new Status999GeneralFailureException("Limit internal system client service error: " + ExceptionMessageUtil.allMessages(e));
        }

        getCurrencyCodeFromServiceDomain(context);
        getBalanceFromServiceAccounting(context);
        log.info("validating user complete: " + context);
    }

    @TimeThisMethod
    @Retryable(exclude = NotRetryableErrorCodeException.class)
    public void deposit(DepositRequestContext context) throws Status999GeneralFailureException, Status900InvalidSignatureException, Status901InvalidOrMissingParameters, Status902UserNotFoundException, Status903ReferenceExistsException, Status906DepositNotAllowed {
        log.info("Start deposit : " + context);
        getPropertiesDMPFromServiceCashier(context, true, "opay");
        validateAmount(context);
        DepositRequest request = context.getRequest();
        String payload = request.getDateTime() + request.getAmount() + request.getNarrative()
                + request.getNetworkRef() + request.getExternalRef() + request.getMsisdn();
        validateSignature(context, payload);
        getUserFromServiceUser(context);
        validateDepositRestrictions(context);
        registerDepositWithCashier(context);
        log.info("Deposit registered: " + context);
    }

    private void validateDepositRestrictions(DepositRequestContext context) throws Status902UserNotFoundException, Status999GeneralFailureException, Status906DepositNotAllowed {

        String guid = context.getUserGuid();
        User user = checkUserRestrictions(context);

        boolean allowedDeposit = false;
        Access access;
        try {
            access = limits.checkAccess(guid);
            if(access.isDepositAllowed()) {
                DepositLimitClient depositLimitClient = serviceFactory.target(DepositLimitClient.class);
                String defaultLocale = cachingDomainClientService.getDomainClient().findByName(user.getDomain().getName()).getData().getDefaultLocale();
                allowedDeposit = depositLimitClient.allowedToDeposit(guid, context.getAmountInCents(), defaultLocale);
            }
        } catch (Exception e) {
            log.error("Cant check deposit limits for user=[" + user.guid() + "]");
            throw new Status999GeneralFailureException("Cant check deposit limits", e);
        }

        if (!allowedDeposit) {
            log.info("No allowed deposit="+context.getAmountInCents()+" for user=[" + user.guid() + "]");
            throw new Status906DepositNotAllowed(RestrictionError.DEPOSIT.getResponseMessageLocal(messageSource, user.getDomain().getName(), access.getDepositErrorMessage()));
        }
    }

    private User checkUserRestrictions(RequestContext context) throws Status999GeneralFailureException, Status902UserNotFoundException, Status906DepositNotAllowed {
	    String guid = context.getUserGuid();
	    User user;
	    try {
		    user = userService.getUserByGuid(guid);
	    } catch (UserClientServiceFactoryException e) {
		    log.warn("User client service error: " + ExceptionMessageUtil.allMessages(e));
		    throw new Status999GeneralFailureException("User client service error: " + ExceptionMessageUtil.allMessages(e));
	    } catch (UserNotFoundException e) {
		    log.warn("User " + context.getMsisdn() + " not found: " + ExceptionMessageUtil.allMessages(e));
		    throw new Status902UserNotFoundException();
	    }

	    Status status = user.getStatus();
	    if (!status.getUserEnabled()) {
		    log.info("User is disabed. Username = [" + user.getName() + "]");
		    throw new Status906DepositNotAllowed("User is disabed.");
	    }

	    String blockParameter = context.getPropertiesDmp().getProperties().get("block_unverified_users");
	    if (blockParameter == null || Boolean.valueOf(blockParameter)) {
		    Long verificationStatusId = user.getVerificationStatus();
		    if (verificationStatusId == null || !VerificationStatus.isVerified(verificationStatusId)) {
			    log.info("User not verified. Username = [" + user.getName() + "]");
			    throw new Status906DepositNotAllowed("User not verified.");
		    }
	    } else {
		    log.debug("Skipped verification for username = [" + user.getName() + "]. Parameter 'block_unverified_users' = " + blockParameter);
	    }
	    return user;
    }

    @TimeThisMethod
    public void depositStatus(DepositStatusRequestContext context) throws Status901InvalidOrMissingParameters, Status999GeneralFailureException, Status900InvalidSignatureException {
        log.info("Getting status of deposit transaction: " + context);

        getPropertiesDMPFromServiceCashier(context, true, "opay");
        String payload = context.getTimestamp() + context.getNetworkRef();
        validateSignature(context, payload);
        if ((context.getNetworkRef() == null) ||
                (context.getNetworkRef().length() < 1)) {
            throw new Status901InvalidOrMissingParameters("networkRef should not be empty");
        }

        try {
            DepositStatus depositStatus =
                    cashierService.getDepositStatus(context.getNetworkRef(), "opay");
            context.setDepositStatus(depositStatus);
            log.info("Retrieved status of deposit transaction: " + context);
        } catch (Exception e) {
            String message = "Could not find opay transaction in cashier with this reference: "
                    + context.getNetworkRef() + " " + ExceptionMessageUtil.allMessages(e);
            log.error(message, e);
            throw new Status901InvalidOrMissingParameters(message);
        }
    }

    @CountThisMethod
    private void registerDepositWithCashier(DepositRequestContext context) throws Status999GeneralFailureException, Status903ReferenceExistsException {
        try {
            context.setCashierReferenceNumber(cashierService.registerDeposit(
                    context.getPropertiesDmp().getId(),
                    context.getUserGuid(),
                    "NGN",
                    context.getAmountInCents(),
                    context.getRequest().getNetworkRef(),
                    context.getRequest().getExternalRef(),
                    context.getSessionId(),
                    true,
                    context.getPaymentType()
            ));
        } catch (Exception e) {
            log.error("Register deposit failed (" + context + "):" , e);
            if (e.getMessage().contains("A transaction with this reference already exists."))
                throw new Status903ReferenceExistsException();
            else
                throw new Status999GeneralFailureException("Register deposit failed " + e.getMessage());
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

    private void getCurrencyCodeFromServiceDomain(BalanceRequestContext context) throws
            Status999GeneralFailureException {
        try {
            Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(context.getGroupRef());
            context.setCurrencyCode(domain.getCurrency());
        } catch (Exception e) {
            log.warn("Domain service error: " + ExceptionMessageUtil.allMessages(e));
            throw new Status999GeneralFailureException("Domain service error: " + ExceptionMessageUtil.allMessages(e));
        }
    }

    private void getBalanceFromServiceAccounting(BalanceRequestContext context) throws
            Status999GeneralFailureException {
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
            log.warn("Accounting service error: " + ExceptionMessageUtil.allMessages(e));
            throw new Status999GeneralFailureException("Accounting service error: " + ExceptionMessageUtil.allMessages(e));
        }
    }

	public void getUserFromServiceUser(RequestContext context) throws Status906DepositNotAllowed, Status999GeneralFailureException, Status902UserNotFoundException {
		try {
			User user = userService.getUserByCellphoneNumber(context.getGroupRef(), context.getFormattedPhoneNumber());
			Long sessionId = (user.getSession() != null) ? user.getSession().getId() : user.getLastLogin().getId();
			context.setSessionId(sessionId);
			context.setUserGuid(user.guid());
			context.setFirstName(user.getFirstName());
			log.debug("Found user " + user);
			checkUserRestrictions(context);
		} catch (UserClientServiceFactoryException e) {
			log.warn("User client service error: " + ExceptionMessageUtil.allMessages(e));
			throw new Status999GeneralFailureException("User client service error: " + ExceptionMessageUtil.allMessages(e));
		} catch (UserNotFoundException e) {
			log.warn("User " + context.getMsisdn() + " not found: " + ExceptionMessageUtil.allMessages(e));
			throw new Status902UserNotFoundException();
		}
	}

	private void validateSignature(RequestContext context, String payload) throws Status900InvalidSignatureException {
		String rsa_pub = context.getPropertiesDmp().getProperties().get("rsa_pub");
		boolean validSignature = verifySHA1withRSASignature(rsa_pub, context.getSignature(), payload);
		if (!validSignature) {
			log.warn("Can't validate signature " + context.getSignature() + " using payload " + payload);
			throw new Status900InvalidSignatureException("Signature Verification Failed");
		}
	}

    private boolean verifySHA1withRSASignature(String pub, String sign, String src) throws
            Status900InvalidSignatureException {
        try {
            Signature sigEng = Signature.getInstance("SHA1withRSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(pub));
            KeyFactory fac = KeyFactory.getInstance("RSA");
            RSAPublicKey rsaPubKey = (RSAPublicKey) fac.generatePublic(keySpec);
            sigEng.initVerify(rsaPubKey);
            sigEng.update(src.getBytes());
            return sigEng.verify(Base64.getDecoder().decode(sign));
        } catch (Exception e) {
            log.warn("Could not calculate signature due " + ExceptionMessageUtil.allMessages(e));
            throw new Status900InvalidSignatureException("Could not calculate signature");
        }
    }

    public void getPropertiesDMPFromServiceCashier(RequestContext context, boolean deposit,
                                                   String methodCode) throws Status999GeneralFailureException {
        try {
            DomainMethodProcessor dmp = cashierService.processorByMethodCodeAndProcessorDescription(
                    context.getGroupRef(), deposit,
                    methodCode, "opay");

            log.debug("Received properties processor config: " + dmp);
            if (dmp.getProperties().size() == 0) {
                log.warn("Invalid processor configuration");
                throw new Status999GeneralFailureException("Invalid processor configuration");
            }
            context.setPropertiesDmp(dmp);
        } catch (Exception e) {
            log.error("Error trying to call cashier client: " + ExceptionMessageUtil.allMessages(e), e);
            throw new Status999GeneralFailureException(ExceptionMessageUtil.allMessages(e));
        }
    }
}
