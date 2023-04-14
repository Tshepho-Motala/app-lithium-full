package lithium.service.cashier.processor.interswitch.services;

import com.netflix.servo.util.VisibleForTesting;
import lithium.exceptions.Status400BadRequestException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.access.client.AccessService;
import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.processor.interswitch.api.schema.Customer;
import lithium.service.cashier.processor.interswitch.api.schema.CustomerInformationRequest;
import lithium.service.cashier.processor.interswitch.api.schema.CustomerInformationResponse;
import lithium.service.cashier.processor.interswitch.api.schema.PaymentItem;
import lithium.service.cashier.processor.interswitch.client.ICommandExecutor;
import lithium.service.cashier.processor.interswitch.data.DepositRequestContext;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status479DepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Slf4j
public class CustomerInformationExecutor implements ICommandExecutor {

    private CustomerInformationRequest customerInformationRequest;
    private HttpServletRequest webRequest;
    private static final String PRODUCT_NAME= "PayAtBank";
    private PayDirectDepositService payDirectDepositService;
    private UserApiInternalClientService userApiInternalClientService;
    private AccessService accessService;
    private User user;
	private static final String PAYDIRECT_METHOD_NAME = "paydirect";
	private static final String QUICKTELLER_METHOD_NAME = "quickteller";

    public CustomerInformationExecutor(String request, HttpServletRequest webRequest) throws Exception {
        this.webRequest = webRequest;
        this.customerInformationRequest =  unmarshallCustomerInformationRequest(request);
    }

    @Override
    public CustomerInformationResponse executeCommand(String processorCode) throws Status500InternalServerErrorException, Status550ServiceDomainClientException, Status500LimitInternalSystemClientException {
	    log.info("CustomerInformation validate input data:" + customerInformationRequest.toString());
	    DepositRequestContext context = new DepositRequestContext();
	    if (this.user == null) {
		    return buildInvalidCustomerResponse(customerInformationRequest.getMerchantReference(), "Cant find user");
	    }
	    if (updateRequestContext(context, user) == null) {
		    return buildInvalidCustomerResponse(customerInformationRequest.getMerchantReference(), "User has never logged in. Contact to Customer Service");
	    }
	    payDirectDepositService.getPropertiesDMPFromServiceCashier(context, processorCode);
	    try {
		    validateMerchantReference(context);
	    } catch (Status400BadRequestException ex) {
		    return buildInvalidCustomerResponse(customerInformationRequest.getMerchantReference(), ex.getMessage());
	    }
	    BigDecimal allowedDeposit;
	    try {
		    allowedDeposit = payDirectDepositService.getAllowedDepositValue(context, user, customerInformationRequest.getAmount());
	    } catch (Status478TimeSlotLimitException | Status400BadRequestException | Status405UserDisabledException | Status479DepositLimitReachedException ex) {
		    return buildInvalidCustomerResponse(customerInformationRequest.getMerchantReference(), ex.getMessage());
	    }
	    return buildCustomerInformationResponse(customerInformationRequest, user, allowedDeposit);
    }

	@Override
	public User getAllowedUser(String domainName) {
		User user = null;
		log.info("Start PayDirect verify user for: userId=" + customerInformationRequest.getCustReference());
		String playerCellPhoneNumber = payDirectDepositService.buildCellPhoneNumber(customerInformationRequest.getCustReference());
		if (playerCellPhoneNumber == null) {
			return user;
		}
		try {
			user = userApiInternalClientService.getUserByCellphoneNumber(domainName, playerCellPhoneNumber);
			if (user == null) {
				log.warn("User not found CustReferense = [" + customerInformationRequest.getCustReference() + "]");
			}
			this.user = user;
		} catch (UserClientServiceFactoryException | UserNotFoundException ex) {
			log.error("User by phone number=[" + customerInformationRequest.getCustReference() + "] not found. Internal error: " + ex.getMessage());
		}
		return user;
	}

	@Override
	public Object buildErrorMessage(String message) {
		return buildInvalidCustomerResponse(customerInformationRequest.getMerchantReference(), message);
	}

	@Override
	public List<String> resolveProcessorCodes(String request) {
        return Arrays.asList(PAYDIRECT_METHOD_NAME,QUICKTELLER_METHOD_NAME);
	}

	private DepositRequestContext updateRequestContext(DepositRequestContext context, User user) {
		Long sessionId;
		try {
			sessionId = (user.getSession() != null) ? user.getSession().getId() : user.getLastLogin().getId();
		} catch (Exception ex) {
			log.error("The user=[" + user.guid() + "] has never logged in. Operation terminated");
			return null;
		}

		context.setMerchantReference(customerInformationRequest.getMerchantReference());
		context.setSessionId(sessionId);
		context.setUserGuid(user.guid());
		context.setFirstName(user.getFirstName());
		context.setDomainName(user.getDomain().getName());
		return context;
	}

    private void validateMerchantReference(DepositRequestContext context) throws Status400BadRequestException {
        DomainMethodProcessor dmp = context.getPropertiesDmp();
        String dmpMerchantReference = dmp.getProperties().get("merchant_reference");
        String requestMerchantReference = context.getMerchantReference();
        if (dmpMerchantReference == null || requestMerchantReference == null || !requestMerchantReference.equalsIgnoreCase(dmpMerchantReference)) {
            log.error("Unsupported MerchantReference value =" + requestMerchantReference);
            throw new Status400BadRequestException("Unsupported MerchantReference value");
        }
    }

    private CustomerInformationResponse buildCustomerInformationResponse(CustomerInformationRequest verifyRequest, User user, BigDecimal allowedDeposit) {
	    BigDecimal amount = allowedDeposit.setScale(2);
	    List<Customer> customers = new ArrayList<>();
	    Customer customer = new Customer();
	    customer.setCustReference(verifyRequest.getCustReference());
	    customer.setStatus(0);
	    customer.setFirstName(user.getFirstName());
	    customer.setLastName(user.getLastName());
	    customer.setAmount(amount);
	    customer.setEmail(user.getEmail());
	    customer.setPhone(user.getCellphoneNumber());

	    if (amount.compareTo(BigDecimal.ZERO) > 0) {
		    PaymentItem item = new PaymentItem();
		    item.setProductName(PRODUCT_NAME);
		    item.setProductCode(verifyRequest.getPaymentItemCode());
            item.setQuantity(1L);
            item.setPrice(amount);
            item.setSubtotal(amount);
            item.setTax(BigDecimal.ZERO);
            item.setTotal(amount);
            List<PaymentItem> paymentItems = new ArrayList<>();
            paymentItems.add(item);
            customer.setPaymentItems(paymentItems);
        }

        customers.add(customer);
        CustomerInformationResponse respose = new CustomerInformationResponse();
        respose.setMerchantReference(verifyRequest.getMerchantReference());
        respose.setCustomerList(customers);
        return respose;
    }

    private CustomerInformationResponse buildInvalidCustomerResponse(String merchantReference, String errorMessage) {
        List<Customer> customers = new ArrayList<>();
        Customer customer = new Customer();
        customer.setStatus(1);
        customer.setStatusMessage(errorMessage);
        customers.add(customer);
        CustomerInformationResponse respose = new CustomerInformationResponse();
        respose.setMerchantReference(merchantReference);
        respose.setCustomerList(customers);
        return respose;
    }
    @VisibleForTesting
    public static CustomerInformationRequest unmarshallCustomerInformationRequest(String request) throws Exception {
        StringReader reader = new StringReader(request);
        JAXBContext jaxbContext = JAXBContext.newInstance(CustomerInformationRequest.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (CustomerInformationRequest) jaxbUnmarshaller.unmarshal(reader);
    }
}
