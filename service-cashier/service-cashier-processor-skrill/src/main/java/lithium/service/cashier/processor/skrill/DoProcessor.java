package lithium.service.cashier.processor.skrill;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.skrill.DoProcessorSkrillAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.skrill.data.CustomerVerificationRequest;
import lithium.service.cashier.processor.skrill.data.CustomerVerificationResponse;
import lithium.service.cashier.processor.skrill.data.QuickCheckoutRequest;
import lithium.service.cashier.processor.skrill.data.TransferPrepareRequest;
import lithium.service.cashier.processor.skrill.data.TransferPrepareResponse;
import lithium.service.cashier.processor.skrill.data.TransferRequest;
import lithium.service.cashier.processor.skrill.data.TransferResponse;
import lithium.service.cashier.processor.skrill.util.HashCalculator;
import lithium.util.ObjectToHttpEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class DoProcessor extends DoProcessorSkrillAdapter {
	@Autowired RestTemplate restTemplate;

	private static final int TRANSFER_STATUS_SCHEDULED = 1;
	private static final int TRANSFER_STATUS_PROCESSED = 2;

	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		try {
			QuickCheckoutRequest quickCheckoutRequest = QuickCheckoutRequest.builder()
			.payToEmail(request.getProperty("skrillMerchantEmail"))
			.recipientDescription(request.getUser().getDomain())
			.transactionId(String.valueOf(request.getTransactionId()))
			.returnUrl(request.getProperty("returnUrl"))
			.returnUrlText(request.getProperty("returnUrlText"))
			.returnUrlTarget(request.getProperty("returnUrlTarget"))
			.cancelUrl(request.getProperty("cancelUrl"))
			.cancelUrlTarget(request.getProperty("cancelUrlTarget"))
			.statusUrl(request.getProperty("callbackUrl"))
			.logoUrl(request.getProperty("logoUrl"))
			.prepareOnly(1)
			.firstName(request.getUser().getFirstName())
			.lastName(request.getUser().getLastName())
			.address(request.getUser().getResidentialAddress().getAddressLine1())
			.address2(request.getUser().getResidentialAddress().getAddressLine2())
			.phoneNumber(request.getUser().getCellphoneNumber())
			.postalCode(request.getUser().getResidentialAddress().getPostalCode())
			.city(request.getUser().getResidentialAddress().getCity())
			.state(request.getUser().getResidentialAddress().getAdminLevel1())
			.country(request.getUser().getResidentialAddress().getCountryCode())
			.amount(request.processorCommunicationAmount().stripTrailingZeros().toPlainString())
			.currency(request.getUser().getCurrency())
			.paymentMethods("WLT")
			.build();
			log.debug("quickCheckoutRequest:: " + quickCheckoutRequest);
			String sid = postForObject(
				request,
				response,
				context,
				rest,
				request.getProperty("baseUrl"),
				ObjectToHttpEntity.forPostFormFormParam(quickCheckoutRequest),
				String.class
			);
			response.setOutputData(1, "sid", sid);
			log.debug("sid:: " + sid);
			return DoProcessorResponseStatus.NEXTSTAGE;
		} catch (Exception e) {
			return DoProcessorResponseStatus.FATALERROR;
		}
	}

	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		String redirectTo = request.getProperty("baseUrl");
		Map<String, String> queryParams = new LinkedHashMap<>();
		queryParams.put("sid", request.stageOutputData(1, "sid"));
		response.setIframeUrl(redirectTo);
		response.setIframeMethod("GET");
		response.setIframePostData(queryParams);
		return DoProcessorResponseStatus.IFRAMEPOST;
	}

	@Override
	protected DoProcessorResponseStatus depositStage3(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		return DoProcessorResponseStatus.NOOP;
	}

	@Override
	protected DoProcessorResponseStatus validateWithdrawalStage1(DoProcessorRequest request, DoProcessorResponse response) {
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		try {
			HashCalculator hashCalculator = new HashCalculator();
			hashCalculator.addItem("secretWord", request.getProperty("secretWord"));
			String secretWordHashed = hashCalculator.calculateHash();
			CustomerVerificationRequest customerVerificationRequest = CustomerVerificationRequest.builder()
			.merchantId(request.getProperty("skrillMerchantId"))
			.password(secretWordHashed)
			.email(request.stageInputData(1, "skrillEmail"))
			.build();
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			final HttpEntity<CustomerVerificationRequest> entity = new HttpEntity<>(customerVerificationRequest, headers);
			CustomerVerificationResponse customerVerificationResponse = restTemplate.exchange(
					request.getProperty("customerVerificationUrl"), HttpMethod.POST, entity, CustomerVerificationResponse.class).getBody();
			log.debug("customerVerificationResponse:: " + customerVerificationResponse);
			buildRawResponseLog(response, customerVerificationResponse);
			if (customerVerificationResponse.getCode() != null &&
				customerVerificationResponse.getCode().contentEquals("ACTIVE_CUSTOMER_ACCOUNT_NOT_FOUND")) {
					response.setMessage("Skrill account verification failed.");
					response.stageOutputData(1).put("skrillEmail", "Skrill account verification failed.");
					return DoProcessorResponseStatus.DECLINED;
			} else if (customerVerificationResponse.getVerificationLevel() != null) {
				return DoProcessorResponseStatus.SUCCESS;
			}
		} catch (Exception e) {
			log.error("Could not verify customer account with Skrill. " + e.getMessage(), e);
			return DoProcessorResponseStatus.DECLINED;
		}
		return DoProcessorResponseStatus.DECLINED;
	}

	@Override
	protected DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		TransferPrepareRequest transferPrepareRequest = TransferPrepareRequest.builder()
		.action("prepare")
		.email(request.getProperty("skrillMerchantEmail"))
		.password(request.getProperty("mqiPasswordMd5"))
		.amount(request.processorCommunicationAmount().toPlainString())
		.currency(request.getUser().getCurrency())
		.beneficiaryEmail(request.stageInputData(1, "skrillEmail"))
		.subject(request.getProperty("withdrawalNotificationSubject"))
		.note(request.getProperty("withdrawalNotificationNote"))
		.referenceId(String.valueOf(request.getTransactionId()))
		.build();
		TransferPrepareResponse transferPrepareResponse = postForObject(
			request,
			response,
			context,
			rest,
			request.getProperty("baseUrl") + "/app/pay.pl",
			ObjectToHttpEntity.forPostFormFormParam(transferPrepareRequest),
			TransferPrepareResponse.class
		);;
		log.info("transferPrepareResponse:: " + transferPrepareResponse);
		response.setOutputData(1, "sid", transferPrepareResponse.getSid());
		buildRawResponseLog(response, transferPrepareResponse);
		if (transferPrepareResponse.getSid() != null) {
			TransferRequest transferRequest = TransferRequest.builder()
			.action("transfer")
			.sid(transferPrepareResponse.getSid())
			.build();
			TransferResponse transferResponse = postForObject(
				request,
				response,
				context,
				rest,
				request.getProperty("baseUrl") + "/app/pay.pl",
				ObjectToHttpEntity.forPostFormFormParam(transferRequest),
				TransferResponse.class
			);
			buildRawResponseLog(response, transferResponse);
			if (transferResponse.getTransaction() != null &&
				transferResponse.getTransaction().getStatus().intValue() == TRANSFER_STATUS_PROCESSED) {
					return DoProcessorResponseStatus.SUCCESS;
			} else {
				// Since we check for a valid skrill account in validateWithdrawalStage1, this should never be the case
				return DoProcessorResponseStatus.DECLINED;
			}
		} else {
			return DoProcessorResponseStatus.DECLINED;
		}
	}
}
