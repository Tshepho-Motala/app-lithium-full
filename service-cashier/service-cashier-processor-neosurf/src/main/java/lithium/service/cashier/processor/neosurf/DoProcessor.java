package lithium.service.cashier.processor.neosurf;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.neosurf.data.RegisterRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessor extends DoProcessorAdapter {
	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response,
			DoProcessorContext context, RestTemplate rest) throws Exception {
		BigDecimal amount = new BigDecimal(request.stageInputData(1, "amount"));
		amount = amount.movePointRight(2);
 		Date dob = new Date();
		String tell = "";
		if (request.getUser() != null) { 
			if (null != request.getUser().getDateOfBirth()) {
				dob = request.getUser().getDateOfBirth().toDate();
			}
			if (null != request.getUser().getCellphoneNumber()) {
				tell = request.getUser().getCellphoneNumber().replaceAll("[^0-9]+", "");
			}
		}

		RegisterRequest tranRequest = RegisterRequest.builder()
				.userFirstName(request.getUser().getFirstName())
 				.userLastName(request.getUser().getLastName())
 				.userEmail(request.getUser().getEmail())
				.userDateOfBirth(new SimpleDateFormat("yyyy-MM-dd").format(dob))
				.userTelephoneNumber(tell)
				.amount(amount)
				.urlCallback(request.getProperty("callbackurl"))
				.merchantTransactionId(request.getTransactionId().toString())
 				.prohibitedForMinors(request.getProperty("prohibitedForMinors"))
				.currency(request.getUser().getCurrency().toLowerCase())
				.language(request.getUser().getLocale().split("-")[0])
				.urlOk(request.getProperty("urlOk"))
				.urlKo(request.getProperty("urlKo"))
				.urlPending(request.getProperty("urlPending"))
				.merchantId(request.getProperties().get("merchantId"))
				.test(request.getProperties().get("test"))
				.subMerchantId(request.getProperties().get("subMerchantId"))
				.userGender("male")
			 	.userKyc("0")
				.checksum(request.getProperties().get("secretkey")).build();

		response.setIframePostData(this.postData(tranRequest));
		
		response.setIframeUrl(request.getProperty("baseUrl"));
		
		buildRawRequestLog(request, response, tranRequest);

		return DoProcessorResponseStatus.IFRAMEPOST;

	}

	private Map<String, String> postData(RegisterRequest request) {
		HashMap<String, String> urlParams = new HashMap<String, String>();
		if (request.getAmount() != null && !request.getAmount().toString().isEmpty()) {
			urlParams.put("amount", request.getAmount().toString());
		}
		if (request.getCurrency() != null && !request.getCurrency().toString().isEmpty()) {
			urlParams.put("currency", request.getCurrency());
		}
		urlParams.put("hash", request.generateHash(request.getChecksum()));
		
		if (request.getLanguage() != null && !request.getLanguage().toString().isEmpty()) {
			urlParams.put("language", request.getLanguage());
		}
		if (request.getMerchantId() != null && !request.getMerchantId().toString().isEmpty()) {
			urlParams.put("merchantId", request.getMerchantId());
		}
		if (request.getMerchantTransactionId() != null && !request.getMerchantTransactionId().toString().isEmpty()) {
			urlParams.put("merchantTransactionId", request.getMerchantTransactionId());
		}
		if (request.getProhibitedForMinors() != null && !request.getProhibitedForMinors().toString().isEmpty()) {
			urlParams.put("prohibitedForMinors", request.getProhibitedForMinors());
		}
		if (request.getSubMerchantId() != null && !request.getSubMerchantId().toString().isEmpty()) {
			urlParams.put("subMerchantId", request.getSubMerchantId());
		}
		if (request.getTest() != null && !request.getTest().toString().isEmpty()) {
			urlParams.put("test", request.getTest());
		}
		if (request.getUrlCallback() != null && !request.getUrlCallback().toString().isEmpty()) {
			urlParams.put("urlCallback", request.getUrlCallback());
		}
		if (request.getUrlKo() != null && !request.getUrlKo().toString().isEmpty()) {
			urlParams.put("urlKo", request.getUrlKo());
		}
		if (request.getUrlOk() != null && !request.getUrlOk().toString().isEmpty()) {
			urlParams.put("urlOk", request.getUrlOk());
		}
		if (request.getUrlPending() != null && !request.getUrlPending().toString().isEmpty()) {
			urlParams.put("urlPending", request.getUrlPending());
		}
		if (request.getUserEmail() != null && !request.getUserEmail().toString().isEmpty()) {
			urlParams.put("userEmail", request.getUserEmail());
		}	
		if (request.getUserFirstName() != null && !request.getUserFirstName().isEmpty()) {
			urlParams.put("userFirstName", request.getUserFirstName());
		}
		if (request.getUserGender()!= null && !request.getUserGender().toString().isEmpty()) {
			urlParams.put("userGender", request.getUserGender());
		}
		if (request.getUserKyc() != null && !request.getUserFirstName().isEmpty()) {
			urlParams.put("userKyc", request.getUserKyc());
		}
		
		if (request.getUserLastName() != null && !request.getUserLastName().toString().isEmpty()) {
			urlParams.put("userLastName", request.getUserLastName());
		}
		if (request.getUserTelephoneNumber() != null && !request.getUserTelephoneNumber().toString().isEmpty()) {
			urlParams.put("userTelephoneNumber", request.getUserTelephoneNumber());
		}

		if (request.getVersion() != null && !request.getVersion().toString().isEmpty()) {
			urlParams.put("version", request.getVersion());
		}
		return urlParams;
	}

	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response,
			DoProcessorContext context, RestTemplate rest) throws Exception {	
		    buildRawResponseLog(response, request);
	 		return DoProcessorResponseStatus.SUCCESS;
	}
}
