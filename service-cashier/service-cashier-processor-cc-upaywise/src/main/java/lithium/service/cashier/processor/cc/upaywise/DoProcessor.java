package lithium.service.cashier.processor.cc.upaywise;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.cc.DoProcessorCCAdapter;
import lithium.service.cashier.method.cc.FieldValidatorCC;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.cc.upaywise.data.UPayWiseRequest;
import lithium.service.cashier.processor.cc.upaywise.data.UPayWiseResponse;
import lithium.service.cashier.processor.cc.upaywise.data.enums.Action;
import lithium.service.cashier.processor.cc.upaywise.data.enums.ResponseCode;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.exchange.client.ExchangeClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessor extends DoProcessorCCAdapter {
	@Autowired LithiumServiceClientFactory services;
	@Autowired HttpServletRequest httpServletRequest;
	
	private double getExchangeRate(String fromCurrencyCode, String toCurrencyCode) throws LithiumServiceClientFactoryException {
		ExchangeClient client = services.target(ExchangeClient.class, "service-exchange", true);
		return client.rate(fromCurrencyCode, toCurrencyCode).getData();
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		Map<String, String> properties = request.getProperties();
		
		try {
			Integer.parseInt(properties.get("deductPercentage"));
		} catch (NumberFormatException nfe) {
			throw new Exception("Could not convert deductPercentage to Integer", nfe);
		}
		BigDecimal deductiblePercentage = new BigDecimal(properties.get("deductPercentage")).movePointLeft(2);
		
		String currencyCode = properties.get("currencyCode");
		double exchangeRate = getExchangeRate(request.getUser().getCurrency(), currencyCode);
		
		BigDecimal amount = new BigDecimal(request.stageInputData(1, "amount"));
		
		BigDecimal convertedAmount = amount.multiply(new BigDecimal(exchangeRate)).setScale(2, RoundingMode.CEILING);
		BigDecimal convertedAmountAfterDeduction = convertedAmount.subtract(convertedAmount.multiply(deductiblePercentage)).setScale(2, RoundingMode.CEILING);
		
		UPayWiseRequest uPayWiseRequest = UPayWiseRequest.builder()
			.terminalId(properties.get("terminalId"))
			.password(properties.get("password"))
			.action(String.valueOf(Action.PURCHASE.code()))
			.card(request.stageInputData(1, "ccnumber"))
			.cvv2(request.stageInputData(1, "cvv"))
			.expYear(request.stageInputData(1, "expyear"))
			.expMonth(request.stageInputData(1, "expmonth"))
			.member(request.getUser().getFirstName() + " " + request.getUser().getLastName())
			.currencyCode(currencyCode)
			.address(request.getUser().getResidentialAddress().toOneLinerStreet())
			.city(request.getUser().getResidentialAddress().getCity())
			.stateCode("")
			.zip(request.getUser().getResidentialAddress().getPostalCode())
			.countryCode(request.getUser().getResidentialAddress().getCountryCode())
			.email(request.getUser().getEmail())
			.amount(convertedAmountAfterDeduction.toString())
			.merchantTrackId(request.getTransactionId().toString())
			.merchantIp(httpServletRequest.getLocalAddr())
			.customerIp(request.getUser().getLastKnownIP())
			.build();
		
		response.setOutputData(1, "account_info", request.stageInputData(1, "ccnumber"));
		
		UPayWiseResponse uPayWiseResponse = postForObject(request, response, context, rest,
			properties.get("url"),
			uPayWiseRequest,
			UPayWiseResponse.class
		);
		log.debug(uPayWiseResponse.toString());
		
		if (uPayWiseResponse.getTargetUrl() != null && uPayWiseResponse.getPayId() != null) {
			response.setIframeUrl(uPayWiseResponse.getTargetUrl()+uPayWiseResponse.getPayId());
			buildRawResponseLog(response, uPayWiseResponse);
			return DoProcessorResponseStatus.IFRAMEPOST;
		}
		
		response.setProcessorReference(uPayWiseResponse.getTranId());
		response.setMessage(uPayWiseResponse.getUdf5());
		
		buildRawResponseLog(response, uPayWiseResponse);
		
		ResponseCode rc = ResponseCode.find(uPayWiseResponse.getResponseCode());
		
		if (populateFieldErrorIfPresent(response, rc, STAGE_1)) {
			return DoProcessorResponseStatus.INPUTERROR;
		}
		
		if (uPayWiseResponse.getResult().equalsIgnoreCase("Successful")) {
			switch (rc) {
				case RC000: return DoProcessorResponseStatus.SUCCESS;
				case RC001:
				case RC509: return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
				default: throw new Exception("Invalid response from UPayWise for Successful request");
			}
		} else {
			return DoProcessorResponseStatus.DECLINED;
		}
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		return DoProcessorResponseStatus.NOOP;
	}
	
	private boolean populateFieldErrorIfPresent(DoProcessorResponse response, ResponseCode rc, int stage) {
		switch (rc) {
			case RC204:
			case RC205:
			case RC219:
			case RC227:
			case RC514:
			case RC533:
			case RC5T3:
			case RC604:
				response.stageOutputData(stage).put(FieldValidatorCC.CC_NUMBER_FIELD, rc.getDescription());
				return true;
			case RC220:
			case RC5N7:
			case RC605:
			case RC626:
				response.stageOutputData(stage).put(FieldValidatorCC.CVV_FIELD, rc.getDescription());
				return true;
			case RC223:
			case RC5Q1:
				response.stageOutputData(stage).put(FieldValidatorCC.EXP_MONTH_FIELD, rc.getDescription());
				response.stageOutputData(stage).put(FieldValidatorCC.EXP_YEAR_FIELD, rc.getDescription());
				return true;
			case RC513:
			case RC5Q7:
				response.stageOutputData(stage).put(FieldValidatorCC.AMOUNT_FIELD, rc.getDescription());
				return true;
			default:
				return false;
		}
	}
}