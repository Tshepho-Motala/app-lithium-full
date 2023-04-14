package lithium.service.cashier.processor.cc.qwipi;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.cc.DoProcessorCCAdapter;
import lithium.service.cashier.method.cc.FieldValidatorCC;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.cc.qwipi.data.PaymentRequest3DS;
import lithium.service.cashier.processor.cc.qwipi.data.PaymentRequestS2S;
import lithium.service.cashier.processor.cc.qwipi.data.PaymentResponseS2S;
import lithium.service.cashier.processor.cc.qwipi.data.enums.ErrorCode;
import lithium.service.cashier.processor.cc.qwipi.data.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DoProcessor extends DoProcessorCCAdapter {
	@Autowired LithiumConfigurationProperties config;
	
	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		PaymentRequestS2S qwipiRequest = new PaymentRequestS2S();
		PaymentRequest3DS qwipi3DSRequest = null;
		
		Map<String, String> properties = request.getProperties();
		
		if (properties.get("3dsecure").equals("yes")) {
			qwipi3DSRequest = new PaymentRequest3DS(); 
			qwipiRequest = qwipi3DSRequest; 
		}

		qwipiRequest.setMerNo(properties.get("merno"));
		qwipiRequest.setAmount(request.stageInputData(1, "amount"));
		qwipiRequest.setCvv2(request.stageInputData(1, "cvv"));
		qwipiRequest.setMonth(request.stageInputData(1, "expmonth"));
		qwipiRequest.setCardNum(request.stageInputData(1, "ccnumber"));
		qwipiRequest.setYear(request.stageInputData(1, "expyear"));
		qwipiRequest.setBillNo(request.getTransactionId().toString());
		qwipiRequest.setAddress(request.getUser().getResidentialAddress().toOneLinerStreet());
		qwipiRequest.setCity(request.getUser().getResidentialAddress().getCity());
		qwipiRequest.setCountry(request.getUser().getResidentialAddress().getCountry());
		qwipiRequest.setZipCode(request.getUser().getResidentialAddress().getPostalCode());
		
		qwipiRequest.setCardHolderIp(request.getUser().getLastKnownIP());
		qwipiRequest.setUserAgent(request.getUser().getLastKnownUserAgent());

		qwipiRequest.setCurrency(request.getUser().getCurrency());
		//TODO hardcoded language EN
		qwipiRequest.setLanguage("EN");
		
		DateTimeFormatter dateTimeDTF = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		qwipiRequest.setDateTime(LocalDateTime.now().format(dateTimeDTF));
		org.joda.time.format.DateTimeFormatter dateTimeYMD = DateTimeFormat.forPattern("yyyyMMdd");
		
		if (request.getUser().getDateOfBirth() != null) {
			qwipiRequest.setDob(dateTimeYMD.print(request.getUser().getDateOfBirth()));
		}
		qwipiRequest.setEmail(request.getUser().getEmail());
		qwipiRequest.setFirstName(request.getUser().getFirstName());
		qwipiRequest.setLastName(request.getUser().getLastName());
		qwipiRequest.setMiddleName("");
		qwipiRequest.setPhone(request.getUser().getTelephoneNumber());
		qwipiRequest.setSsn(request.getUser().getSocialSecurityNumber());
		qwipiRequest.setState(request.getUser().getResidentialAddress().getAdminLevel1());
		
		qwipiRequest.saveMd5Info(properties.get("md5Key"));
		
		response.setOutputData(1, "account_info", request.stageInputData(1, "ccnumber"));
		
		if (qwipi3DSRequest != null) {
			qwipi3DSRequest.setReturnUrl(config.getGatewayPublicUrl()+"/service-cashier/frontend/loadingrefresh");
			qwipi3DSRequest.setBgReturnUrl(
				config.getGatewayPublicUrl()+
				properties.get("callback.url")
			);
			response.setIframeUrl(properties.get("paymenturl3ds"));
			response.addObjectFieldsAsIframePostData(qwipi3DSRequest);
			buildRawRequestLog(request, response, qwipi3DSRequest);
			buildRawResponseLog(response, qwipi3DSRequest);
			return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
		} else {
			String url = properties.get("paymenturls2s");
			PaymentResponseS2S qwipiResponse = postForObject(request, response, context, rest, url,
				qwipiRequest,
				PaymentResponseS2S.class
			);
			response.setAmount(BigDecimal.valueOf(Double.parseDouble(qwipiResponse.getAmount())));
			response.setProcessorReference(qwipiResponse.getOrderId());
			response.setMessage(qwipiResponse.getRemark());
			
			buildRawResponseLog(response, qwipiResponse);
			if (populateFieldErrorIfPresent(response, qwipiResponse.getErrorCode(), STAGE_1)) {
				return DoProcessorResponseStatus.INPUTERROR;
			}
			
			switch (ResultCode.fromCode(Integer.parseInt(qwipiResponse.getResultCode()))) {
				case SUCCESS: return DoProcessorResponseStatus.SUCCESS;
				case PROCESSING: return DoProcessorResponseStatus.NEXTSTAGE;
				case FAILED: return DoProcessorResponseStatus.DECLINED;
				default: throw new Exception("Invalid result from QWIPI " + qwipiResponse.toString());
			}
		}
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		return DoProcessorResponseStatus.NOOP;
	}

	private boolean populateFieldErrorIfPresent(DoProcessorResponse response, String errorCodeString, int stage) {
		int errorCode = 0;
		
		try {
			errorCode = Integer.parseInt(errorCodeString);
		} catch (NumberFormatException nfe) {
			log.error("Unable to parse error code from qwipi processor: " + errorCodeString, nfe);
			return false;
		}
		
		ErrorCode ec = ErrorCode.find(errorCode);
		if (ec == null) return false;
		
		switch (ec) {
			case E1001800:
			case E1000400:
			case E1001970:
				response.stageOutputData(stage).put(FieldValidatorCC.CC_NUMBER_FIELD, ec.description());
				return true;
			case E1000410:
			case E1000420:
				response.stageOutputData(stage).put(FieldValidatorCC.CVV_FIELD, ec.description());
				return true;
			case E1000430:
			case E1000440:
				response.stageOutputData(stage).put(FieldValidatorCC.EXP_MONTH_FIELD, ec.description());
				return true;
			case E1000450:
			case E1000460:
				response.stageOutputData(stage).put(FieldValidatorCC.EXP_YEAR_FIELD, ec.description());
				return true;
			case E1000320:
				response.stageOutputData(stage).put(FieldValidatorCC.AMOUNT_FIELD, ec.description());
				return true;
			default:
				return false;
		}
	}
}
