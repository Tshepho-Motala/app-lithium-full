package lithium.service.cashier.processor.quickbit;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.quickbit.DoProcessorQuickbitAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.quickbit.data.TransactionRequest;
import lithium.service.cashier.processor.quickbit.data.TransactionResponse;
import lithium.service.cashier.processor.quickbit.data.enums.StatusCode;
import lithium.util.ObjectToHttpEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessor extends DoProcessorQuickbitAdapter {
	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		BigDecimal amount = new BigDecimal(request.stageInputData(1, "amount"));
		TransactionRequest tranRequest = TransactionRequest.builder()
		.firstName(request.getUser().getFirstName())
		.lastName(request.getUser().getLastName())
		.email(request.getUser().getEmail())
		.dateOfBirth(new SimpleDateFormat("yyyy-MM-dd").format(request.getUser().getDateOfBirth().toDate()))
		.phoneNumber(request.getUser().getCellphoneNumber().replaceAll("[^0-9]+", ""))
		.address(request.getUser().getResidentialAddress().toOneLinerFull())
		.stateCode(request.getUser().getResidentialAddress().getAdminLevel1Code()
			.substring(request.getUser().getResidentialAddress().getAdminLevel1Code()
			.indexOf(".") + 1, request.getUser().getResidentialAddress().getAdminLevel1Code().length()))
		.postalCode(request.getUser().getResidentialAddress().getPostalCode())
		.city(request.getUser().getResidentialAddress().getCity())
		.countryCode(request.getUser().getResidentialAddress().getCountryCode())
		.fiatAmount(amount)
		.fiatCurrency(request.getUser().getCurrency())
		.cryptoCurrency("BCH")
		.affiliateReferralCode(request.getProperty("referralcode"))
		.callbackUrl(request.getProperty("callbackurl"))
		.requestReference(String.valueOf(request.getTransactionId()))
		.merchantProfile(1)
		.affiliateRedirectUrl(request.getProperty("redirecturl"))
		.build();
		
		tranRequest.setChecksum(tranRequest.calculateHash(request.getProperty("secretkey")));
		
		TransactionResponse tranResponse = null;
		try {
			tranResponse = postForObject(request, response, context, rest,
				request.getProperty("baseUrl") + "/direct_api/create_transaction",
				ObjectToHttpEntity.forPostFormFormParam(tranRequest),
				TransactionResponse.class
			);
			log.debug(tranResponse.toString());
			StatusCode sc = StatusCode.find(Integer.parseInt(tranResponse.getStatusCode()));
			if (sc.equals(StatusCode.I201)) {
				response.setIframeUrl(tranResponse.getRedirectUrl());
				return DoProcessorResponseStatus.REDIRECT_NEXTSTAGE;
			} else {
				log.warn("Declining transaction (" + request.getTransactionId() + ") due to (" + sc.getDescription() + ")");
				buildRawResponseLog(response, tranResponse);
				return DoProcessorResponseStatus.FATALERROR;
				
			}
		} catch (Exception e) {
			buildRawResponseLog(response, tranResponse);
			return DoProcessorResponseStatus.FATALERROR;
		}
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		return DoProcessorResponseStatus.NOOP;
	}
}