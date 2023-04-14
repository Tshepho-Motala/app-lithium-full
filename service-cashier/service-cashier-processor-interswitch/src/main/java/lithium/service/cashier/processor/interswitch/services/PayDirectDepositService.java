package lithium.service.cashier.processor.interswitch.services;

import lithium.exceptions.Status400BadRequestException;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.interswitch.data.DepositRequestContext;
import lithium.service.cashier.processor.interswitch.data.RequestContext;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.DepositLimitClient;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status479DepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.objects.Status;
import lithium.service.user.client.objects.User;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PayDirectDepositService {

	@Autowired
	LithiumServiceClientFactory serviceFactory;
	@Autowired
	LimitInternalSystemService limits;
	@Autowired
	@Setter
	CashierInternalClientService cashierService;
	@Autowired
	CachingDomainClientService cachingDomainClientService;

	private static final boolean IS_DEPOSIT = true;
	private static final String NG_PHONE_CODE = "234";
	private static final int NG_PHONE_NUMBER_LENGTH = 10;

	public BigDecimal getAllowedDepositValue(DepositRequestContext context, User user, String amount)
			throws
			Status405UserDisabledException,
			Status500InternalServerErrorException,
			Status550ServiceDomainClientException,
			Status500LimitInternalSystemClientException,
            Status478TimeSlotLimitException,
			Status479DepositLimitReachedException,
			Status400BadRequestException {

		Status status = user.getStatus();
		if (!status.getUserEnabled()) {
			log.warn("Cant get deposit value for blocked user=" + user.guid());
			throw new Status405UserDisabledException("User is blocked");
		}

		String guid = user.guid();
		try {
			limits.checkPlayerRestrictions(guid, "en_US");
        } catch (Status491PermanentSelfExclusionException | Status490SoftSelfExclusionException | Status496PlayerCoolingOffException e) {
            log.warn("Cant get deposit value for blocked user=" + user.guid() + " " + e.getMessage());
            throw new Status405UserDisabledException("User is blocked");
        } catch (Status500LimitInternalSystemClientException e) {
            throw new Status500InternalServerErrorException(e.getMessage());
        }
        if (amount == null) amount = "0";

        String defaultLocale = cachingDomainClientService.getDomainClient().findByName(user.getDomain().getName()).getData().getDefaultLocale();
        log.debug("checkDepositLimits: locale:" + defaultLocale);
        DepositLimitClient depositLimitClient;
        try {
            depositLimitClient = serviceFactory.target(DepositLimitClient.class);
        } catch (Exception e) {
            log.error("Cant find DepositLimitClient", e);
            throw new Status500InternalServerErrorException("Cant find DepositLimitClient");
        }

        BigDecimal maxAmount;
        DomainMethodProcessor dmp = context.getPropertiesDmp();
        try {
            maxAmount = CurrencyAmount.fromCentsAllowNull(dmp.getProcessor().getLimits().getMaxAmount()).toAmount();
        } catch (Exception ex) {
            log.error("Cant find limits for processor " + dmp.getProcessor().getName());
            throw new Status500InternalServerErrorException("Cant find limits");
        }

        BigDecimal depositAmount;
        try {
            depositAmount = CurrencyAmount.fromAmountString(amount).toAmount();
        } catch (NumberFormatException ex) {
            log.error("Incorrect input value for amount = " + amount + " . Validation terminated .");
            throw new Status400BadRequestException("Incorrect input value for amount = " + amount);
        }

        if (depositAmount.compareTo(maxAmount) >= 0 || depositAmount.compareTo(BigDecimal.ZERO) == 0) {
            depositAmount = maxAmount;
        }

        log.info("Get allowed deposit value for user=" + guid + ", amount=" + depositAmount);
        BigDecimal allowedDeposit = depositLimitClient.getAllowedDepositValue(guid, defaultLocale);
        allowedDeposit = allowedDeposit == null ? depositAmount : allowedDeposit;
        if (allowedDeposit.compareTo(depositAmount) > 0) {
            return depositAmount;
        } else {
            return allowedDeposit;
        }
    }

    public void getPropertiesDMPFromServiceCashier(RequestContext context, String methodName)
            throws Status500InternalServerErrorException {
        try {
            DomainMethodProcessor dmp = cashierService.processorByMethodCodeAndProcessorDescription(
                    context.getDomainName(), IS_DEPOSIT,
                    methodName, methodName);

            log.debug("Received properties processor config: " + dmp);
            if (dmp.getProperties().size() == 0) {
                log.warn("Invalid processor configuration");
                throw new Status500InternalServerErrorException("Invalid processor configuration");
            }
            context.setPropertiesDmp(dmp);
        } catch (Exception e) {
            log.error("Error trying to call cashier client: " + ExceptionMessageUtil.allMessages(e), e);
            throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e));
        }
    }

    private List<String> getIpAddress(HttpServletRequest webRequest) {
		String requestIpAddress = webRequest.getRemoteAddr();
		if (webRequest.getHeader("X-Forwarded-For") != null) {
			requestIpAddress = webRequest.getHeader("X-Forwarded-For");
		}
		String[] splittedAddresses = requestIpAddress.split(",");
		return Arrays.asList(splittedAddresses);
    }

	public String checkAllowedProcessorCode(HttpServletRequest webRequest, String domainName, User user, List<String> processorCodes ) throws Status401UnAuthorisedException {
		List<String> ipAddresses = getIpAddress(webRequest);
		String userAgent = webRequest.getHeader("User-Agent") == null ? "Undefined" : webRequest.getHeader("User-Agent");
		String userGuid = user.guid();
		try {
			for (String ipAddress : ipAddresses) {
				for (String processorCode : processorCodes) {
					if (cashierService.isAccessAllowed(domainName, processorCode, ipAddress, userAgent, userGuid, IS_DEPOSIT)) {
						return processorCode;
					}
				}
			}
		} catch (Exception ex) {
			log.warn("Cant check access for domainName=" + domainName + ",  ipAddresses=" + ipAddresses.stream().map(Object::toString)
					.collect(Collectors.joining(", ")), ex);
		}
		throw new Status401UnAuthorisedException("Access denied for Paydirect/Quickreller services");
	}

	public String buildCellPhoneNumber(String inputPhoneNumber) {
		String outputPhoneNumber = null;
		if (inputPhoneNumber == null || inputPhoneNumber.trim().length() < NG_PHONE_NUMBER_LENGTH) {
			log.error("Incorrect input phone number =[" + inputPhoneNumber + "]");
			return outputPhoneNumber;
		}
		inputPhoneNumber = inputPhoneNumber.trim();
		StringBuilder sb = new StringBuilder();
		sb.append(NG_PHONE_CODE);
		sb.append(inputPhoneNumber.substring(inputPhoneNumber.length() - NG_PHONE_NUMBER_LENGTH));
		return sb.toString();
	}
}
