package lithium.service.cashier.processor.interswitch.services;

import com.netflix.servo.util.VisibleForTesting;
import lithium.exceptions.Status400BadRequestException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.metrics.CountThisMethod;
import lithium.metrics.TimeThisMethod;
import lithium.service.cashier.client.objects.DepositStatus;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.interswitch.api.schema.NotificationPayment;
import lithium.service.cashier.processor.interswitch.api.schema.Payment;
import lithium.service.cashier.processor.interswitch.api.schema.PaymentNotificationRequest;
import lithium.service.cashier.processor.interswitch.api.schema.PaymentNotificationResponse;
import lithium.service.cashier.processor.interswitch.client.ICommandExecutor;
import lithium.service.cashier.processor.interswitch.data.DepositRequest;
import lithium.service.cashier.processor.interswitch.data.DepositRequestContext;
import lithium.service.cashier.processor.interswitch.data.PayDirectChannel;
import lithium.service.cashier.processor.interswitch.exceptions.Status404NotFoundException;
import lithium.service.cashier.processor.interswitch.exceptions.Status417LimistsException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.limit.client.DepositLimitClient;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.Status;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.util.ExceptionMessageUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Slf4j
public class PaymentNotificationExecutor implements ICommandExecutor {

    private PaymentNotificationRequest paymentNotificationRequest;
    private HttpServletRequest webRequest;
    private String processorCode;
    private User user;

    private PayDirectDepositService payDirectDepositService;
    private UserApiInternalClientService userApiInternalClientService;
    private CashierInternalClientService cashierService;
    private LithiumServiceClientFactory serviceFactory;
    private CachingDomainClientService cachingDomainClientService;

    public PaymentNotificationExecutor(String request, HttpServletRequest webRequest) throws Exception {
        this.webRequest = webRequest;
        this.paymentNotificationRequest =  unmarshallNotificationRequest(request);
    }

    @Override
    public User getAllowedUser(String domainName) {
	    User user = null;
	    Payment payment = paymentNotificationRequest.getPaymentList().get(0);
	    String playerCellPhoneNumber = payDirectDepositService.buildCellPhoneNumber(payment.getCustReference());
	    if (playerCellPhoneNumber == null) {
		    return user;
	    }
	    try {
		    user = userApiInternalClientService.getUserByCellphoneNumber(domainName, playerCellPhoneNumber);
		    this.user = user;
	    } catch (NumberFormatException | UserClientServiceFactoryException | UserNotFoundException ex) {
		    log.warn("Cant find user by phone number=" + payment.getCustReference() + ". Internal error: " + ex.getMessage());
	    }
	    return user;
    }

    @Override
    public PaymentNotificationResponse executeCommand(String processorCode) {
        log.info("Payment notification input data:" + paymentNotificationRequest.toString());
        setProcessorCode(processorCode);
        List<NotificationPayment> notifications = new ArrayList<>();
        for (Payment payment : paymentNotificationRequest.getPaymentList()) {
            NotificationPayment notificationPayment = handlePayment(payment);
            notifications.add(notificationPayment);
        }
        PaymentNotificationResponse response = new PaymentNotificationResponse();
        response.setNotificationPaymentsList(notifications);
        return response;

    }


    public NotificationPayment handlePayment(Payment payment) {
        log.info("Start hahdle payment =(" + payment.toString() + ")");
        BigDecimal amount = payment.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0 || payment.getIsReversal()) {
            // Reversal Payment not supported
            log.info("Unsupported operation for amount= " + amount + "");
            return buildErrorNotification(payment.getPaymentLogId(), "Unsupported operation");
        }

        NotificationPayment notificationPayment = new NotificationPayment();

        try {
            log.debug("Pament metod "+getProcessorCode()+" detected");
            DepositRequestContext context = buildDepositRequestContext(payment, getProcessorCode());

            verifyUser(context);
            deposit(context);
            if (context.getNotificationMessage() != null) {
                notificationPayment.setStatusMessage(context.getNotificationMessage());
            }
            notificationPayment.setStatus(0);
            notificationPayment.setPaymentLogId(payment.getPaymentLogId());
            log.info("Susccessfully finished "+getProcessorCode()+" payment for notification=(" + notificationPayment.getStatus() + ")");
        } catch (Exception e) {
            notificationPayment = buildErrorNotification(payment.getPaymentLogId(), e.getMessage());
            log.warn("Terminated payment notification=(" + notificationPayment.getStatus() + ")");
        }
        return notificationPayment;
    }

    private DepositRequestContext buildDepositRequestContext(Payment payment, String methodCode) {
        DepositRequestContext context = new DepositRequestContext();
        DepositRequest depositRequest = new DepositRequest();
        context.setMethodCode(methodCode);
        context.setChannelName(payment.getChannelName());
        depositRequest.setDateTime(payment.getPaymentDate());
        depositRequest.setUserId(payment.getCustReference());
        depositRequest.setGroupRef(payment.getProductGroupCode());
        context.setTimestamp(depositRequest.getDateTime());
        context.setRequest(depositRequest);
        context.setGroupRef(depositRequest.getGroupRef());
        depositRequest.setAmount(payment.getAmount().toString());
        depositRequest.setExternalRef(payment.getPaymentLogId().toString());
        context.setAdditionalReferenceNumber(payment.getPaymentReference());
        return context;
    }

    private NotificationPayment buildErrorNotification(Long externalReferense, String errorMessage) {
        NotificationPayment notificationPayment = new NotificationPayment();
        notificationPayment.setPaymentLogId(externalReferense);
        notificationPayment.setStatus(1); // Rejected by System
        notificationPayment.setStatusMessage(errorMessage);
        return notificationPayment;
    }

    @TimeThisMethod
    private void deposit(DepositRequestContext context)
            throws
            Status500InternalServerErrorException,
            Status400BadRequestException,
            Status404NotFoundException,
            Status417LimistsException {

        validateAmount(context);
        checkExistingTransaction(context);
        validateDepositLimits(context);
        if (context.getDepositStatus() == null) {
            registerDepositWithCashier(context);
        }
    }

    public void verifyUser(DepositRequestContext context)
		    throws Status405UserDisabledException, Status404NotFoundException, Status500InternalServerErrorException {

        if (this.user == null) {
            throw new Status404NotFoundException("User not found");
        }

        Status status = user.getStatus();
	    if (!status.getUserEnabled()) {
		    log.info("User is disabed giud=[" + user.guid() + "]");
		    throw new Status405UserDisabledException("User is disabed CustReference = [" + user.getId() + "]");
	    }

	    context.setUserGuid(user.guid());
	    context.setFirstName(user.getFirstName());
	    context.setDomainName(user.getDomain().getName());

	    payDirectDepositService.getPropertiesDMPFromServiceCashier(context, this.processorCode);

	    if (Boolean.valueOf(context.getPropertiesDmp().getProperties().get("block_unverified_users"))) {
		    Long verificationStatusId = user.getVerificationStatus();
		    if (verificationStatusId == null || !VerificationStatus.isVerified(verificationStatusId)) {
			    log.info("User not verified! Username = [" + user.getName() + "]");
			    throw new Status405UserDisabledException("User not verified! Username = [" + user.getName() + "]");
		    }
	    }

	    Long sessionId;
	    try {
		    sessionId = (user.getSession() != null) ? user.getSession().getId() : user.getLastLogin().getId();
	    } catch (Exception ex) {
		    log.error("The user=[" + user.guid() + "] has never logged in. Operation terminated");
		    throw new Status405UserDisabledException("User has never logged in! Username = [" + user.getName() + "]");
	    }

	    context.setSessionId(sessionId);
	    log.debug("Found user " + user);
    }

    private void validateDepositLimits(DepositRequestContext context) throws Status404NotFoundException, Status417LimistsException {
        BigDecimal allowedDeposit;
        BigDecimal expectedAmount;
        try {
            DepositLimitClient depositLimitClient = serviceFactory.target(DepositLimitClient.class);
            String defaultLocale = cachingDomainClientService.getDomainClient().findByName(user.getDomain().getName()).getData().getDefaultLocale();
            expectedAmount = CurrencyAmount.fromCentsAllowNull(context.getAmountInCents()).toAmount();
            allowedDeposit = depositLimitClient.getAllowedDepositValue(user.guid(), defaultLocale);
            allowedDeposit = allowedDeposit == null ? expectedAmount : allowedDeposit;
        } catch (Exception ex) {
            log.error("Get limits exception", ex);
            throw new Status404NotFoundException(ex.getMessage());
        }

        if (allowedDeposit.compareTo(expectedAmount) < 0) {
            throw new Status417LimistsException("Exceeding the limit of deposits");
        }
    }

    private void checkExistingTransaction(DepositRequestContext context) {
        DepositStatus depositStatus =
                null;
        String notificationMessage = null;
        try {
            depositStatus = cashierService.getDepositStatus(context.getRequest().getExternalRef(), this.processorCode);
            notificationMessage = "Transaction ref=[" + context.getRequest().getExternalRef() + "] already exists";
        } catch (Exception e) {
            log.debug("Not found Interswitch transaction by External reference: [" + context.getRequest().getExternalRef() + "]");
        }
        context.setDepositStatus(depositStatus);
        context.setNotificationMessage(notificationMessage);
    }

    private void validateAmount(DepositRequestContext context) throws Status400BadRequestException {
        try {
            CurrencyAmount amount = CurrencyAmount.fromAmountString(context.getRequest().getAmount());
            context.setAmountInCents(amount.toCents());
        } catch (Exception e) {
            log.warn("Unable to parse amount " + e.getMessage() + " " + context);
            throw new Status400BadRequestException("Invalid amount");
        }
    }

    @CountThisMethod
    private void registerDepositWithCashier(DepositRequestContext context) throws Status500InternalServerErrorException {
        try {
            context.setCashierReferenceNumber(cashierService.registerDeposit(
                    context.getPropertiesDmp().getId(),
                    context.getUserGuid(),
                    context.getPropertiesDmp().getProperties().get("currency_code_str"),
                    context.getAmountInCents(),
                    context.getRequest().getExternalRef(),
                    context.getAdditionalReferenceNumber(),
                    context.getSessionId(),
                    true,
                    context.getChannelName()
            ));
            context.setNotificationMessage("Transaction ref=[" + context.getRequest().getExternalRef() + "] successfully added");
        } catch (Exception e) {
            log.error("Register deposit failed (" + context + ")" + ExceptionMessageUtil.allMessages(e));
            throw new Status500InternalServerErrorException("Register deposit failed " + e.getMessage());
        }
    }

    @Override
    public Object buildErrorMessage(String message) {
        List<NotificationPayment> notifications = new ArrayList<>();
        NotificationPayment notificationPayment = buildErrorNotification(paymentNotificationRequest.getPaymentList().get(0).getPaymentLogId(), message);
        notifications.add(notificationPayment);
        PaymentNotificationResponse response = new PaymentNotificationResponse();
        response.setNotificationPaymentsList(notifications);
        return response;
    }

	@Override
	public List<String> resolveProcessorCodes(String request) {
		List<String> processors = new ArrayList<>();
		for (Payment payment : paymentNotificationRequest.getPaymentList()) {
			String processor = PayDirectChannel.findByValue(payment.getChannelName());
			if (processor != null) {
				processors.add(processor);
			}
		}
		return processors;
	}

    @VisibleForTesting
    public static PaymentNotificationRequest unmarshallNotificationRequest(String request) throws Exception {
        StringReader reader = new StringReader(request);
        JAXBContext jaxbContext = JAXBContext.newInstance(PaymentNotificationRequest.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (PaymentNotificationRequest) jaxbUnmarshaller.unmarshal(reader);
    }
}
