package lithium.service.cashier.processor.trustly;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.trustly.api.SignedAPI;
import lithium.service.cashier.processor.trustly.api.data.TrustlyDepositErrors;
import lithium.service.cashier.processor.trustly.api.data.request.Request;
import lithium.service.cashier.processor.trustly.api.data.requestbuilders.AccountPayout;
import lithium.service.cashier.processor.trustly.api.data.requestbuilders.Deposit;
import lithium.service.cashier.processor.trustly.api.data.response.TrustlyResponse;
import lithium.service.cashier.processor.trustly.api.security.SignatureHandler;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;


@Slf4j
@Service
public class ServiceCashierTrustlyDoProcessor extends DoProcessorAdapter {

	private final static String TRANSACTION_ID_PLACEHOLDER = "{{trn_id}}";
	public  final static String URL_PROPERTY_UNSPECIFIED = "";
	@Autowired
	LithiumConfigurationProperties lithiumProperties;
	@Autowired
	MessageSource messageSource;
	@Autowired
	TrustlyService trustlyService;

	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		return trustlyService.initiateDeposit(request, response, context, rest);
	}

	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		return DoProcessorResponseStatus.NOOP;
	}

	//AccountPayout flow
	@Override
	public DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		return trustlyService.initiateWithdraw(request, response, context, rest);
	}

	@Override
	protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		return DoProcessorResponseStatus.NOOP;
	}

	//withdraw flow currently not used
	/*@Override
	public DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		try {
			Request depositRequest = new Withdraw.Build
					("https://f0e4999e468a.ngrok.io/service-cashier-processor-trustly/public/notify"lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-trustly/public/notify",
							request.getUser().getFullName(), request.getTransactionId().toString(), request.getUser().getCurrency(),
							request.getUser().getFirstName(), request.getUser().getLastName(), request.getUser().getEmail(),
							request.getUser().getDateOfBirth() != null ? new SimpleDateFormat("yyyy-MM-dd").format(request.getUser().getDateOfBirth()) : null)
					.locale("en_GB")
					.suggestedAmount(request.stageInputData(1, "amount"))
					.mobilePhone(request.getUser().getCellphoneNumber())
					.successURL(request.stageInputData(1,"success_url"))
					.failURL(request.stageInputData(1, "fail_url"))
					.getRequest();

			depositRequest.getParams().getData().setUsername(request.getProperty("username"));
			depositRequest.getParams().getData().setPassword(request.getProperty("api_password"));


			PrivateKey privateKey = SignatureHandler.getPrivateKey(Base64.getDecoder().decode(request.getProperty("rsa_private_key")), request.getProperty("rsa_private_key_password"));
			PublicKey publicKey = SignatureHandler.getPublicKey(Base64.getDecoder().decode(request.getProperty("rsa_public_key")));
			SignatureHandler.signRequest(depositRequest, privateKey);

			TrustlyResponse depositResponse = SignedAPI.sendRequest(depositRequest, request.getProperty("payments_api_url"), publicKey);

			response.setRawRequestLog(depositRequest.toString());
			response.setRawResponseLog(depositResponse.toString());
			if (depositResponse.successfulResult()) {
				Map data = (Map) depositResponse.getResult().getData();
				response.setProcessorReference(data.get("orderid").toString());
				response.setIframeUrl(data.get("url").toString());
				response.setIframeMethod("GET");
				response.setPaymentType("account");
				return DoProcessorResponseStatus.IFRAMEPOST;
			} else {
				response.setMessage("Failed to initiate withdraw. Provider responded with error" + depositResponse.getError().getCode());
				log.error("Failed to initiate withdraw. Trustly responded with error: " + depositResponse.getError().getCode() + " : " + depositResponse.getError().getMessage());
				return DoProcessorResponseStatus.DECLINED;
			}
		} catch (Exception e) {
			log.error("Failed to initiate payment for the transaction with id: " + request.getTransactionId() + ". " + e.getMessage(), e);
			buildRawResponseLog(response, e);
			return DoProcessorResponseStatus.FATALERROR;
		}
	}*/

}
