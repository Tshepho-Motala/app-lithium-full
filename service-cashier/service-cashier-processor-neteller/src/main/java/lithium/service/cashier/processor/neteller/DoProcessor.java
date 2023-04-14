package lithium.service.cashier.processor.neteller;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.neteller.DoProcessorNetellerAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.neteller.data.BillingDetails;
import lithium.service.cashier.processor.neteller.data.Link;
import lithium.service.cashier.processor.neteller.data.Neteller;
import lithium.service.cashier.processor.neteller.data.Payment;
import lithium.service.cashier.processor.neteller.data.PaymentHandle;
import lithium.service.cashier.processor.neteller.data.StandaloneCredits;
import lithium.service.cashier.processor.neteller.data.enums.PaymentType;
import lithium.service.cashier.processor.neteller.data.enums.Rel;
import lithium.service.cashier.processor.neteller.data.enums.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DoProcessor extends DoProcessorNetellerAdapter {
	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		try {
			PaymentHandle paymentHandleResponse = createPaymentHandle(request, rest);
			buildRawResponseLog(response, paymentHandleResponse);
			switch (paymentHandleResponse.getStatus()) {
				case INITIATED:
					String redirectTo = "";
					String redirectMethod = "GET";
					if (paymentHandleResponse.getLinks().size() == 1) {
						Link redirectLink = paymentHandleResponse.getLinks().get(0);
						redirectTo = redirectLink.getHref();
						if (redirectLink.getMethod() != null) redirectMethod = redirectLink.getMethod();
					} else {
						Link link = paymentHandleResponse.getLinks()
							.stream()
							.filter(l -> l.getRel().equals(Rel.PAYMENT_REDIRECT))
							.findFirst()
							.get();
						redirectTo = link.getHref();
						if (link.getMethod() != null) redirectMethod = link.getMethod();
					}
					response.setOutputData(1, "paymentHandleToken", paymentHandleResponse.getPaymentHandleToken());
					response.setOutputData(1, "paymentRedirectUrl", redirectTo);
					response.setOutputData(1, "paymentRedirectMethod", redirectMethod);
					return DoProcessorResponseStatus.NEXTSTAGE;
				case FAILED:
					return DoProcessorResponseStatus.DECLINED;
				default:
					return DoProcessorResponseStatus.FATALERROR; // Unexpected status for stage
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return DoProcessorResponseStatus.FATALERROR;
		}
	}

	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		String redirectTo = request.stageOutputData(1, "paymentRedirectUrl");
		String redirectMethod = request.stageOutputData(1, "paymentRedirectMethod");
		Map<String, String> queryParams = getQueryParamsFromUrl(redirectTo);
		response.setIframeUrl(redirectTo);
		response.setIframePostData(queryParams);
		response.setIframeMethod(redirectMethod);
		response.setIframeWindowTarget(request.getProperty("iframelocation"));
		return DoProcessorResponseStatus.IFRAMEPOST;
	}

	@Override
	protected DoProcessorResponseStatus depositStage3(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		try {
			Payment paymentRequest = Payment.builder()
			.merchantRefNum(String.valueOf(request.getTransactionId()))
			.amount(request.processorCommunicationAmount().movePointRight(2).longValue())
			.currencyCode(request.getUser().getCurrency())
			.dupCheck(true)
			.settleWithAuth(true)
			.paymentHandleToken(request.stageOutputData(1, "paymentHandleToken"))
			.customerIp(request.getUser().getLastKnownIP())
			.build();
			final HttpEntity<Payment> entity = new HttpEntity<>(paymentRequest, buildHeaders(request));
			Payment paymentResponse = rest.exchange(request.getProperty("baseUrl") + "/v1/payments", HttpMethod.POST, entity, Payment.class).getBody();
			buildRawResponseLog(response, paymentResponse);
			log.debug("paymentResponse:: " + paymentResponse);
			switch (paymentResponse.getStatus()) {
				case COMPLETED:
				case RECEIVED:
				case HELD:
				case PENDING:
					return DoProcessorResponseStatus.NEXTSTAGE;
				case FAILED:
				case CANCELLED:
					return DoProcessorResponseStatus.DECLINED;
				default:
					return DoProcessorResponseStatus.FATALERROR; // Unexpected status for stage
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return DoProcessorResponseStatus.FATALERROR;
		}
	}

	@Override
	protected DoProcessorResponseStatus depositStage4(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		return DoProcessorResponseStatus.NOOP;
	}

	@Override
	protected DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		try {
			PaymentHandle paymentHandle = createPaymentHandle(request, rest);
			response.setOutputData(1, "paymentHandleToken", paymentHandle.getPaymentHandleToken());
			StandaloneCredits standaloneCredits = StandaloneCredits.builder()
			.amount(request.processorCommunicationAmount().movePointRight(2).longValue())
			.merchantRefNum(String.valueOf(request.getTransactionId()))
			.currencyCode(request.getUser().getCurrency())
			.customerIp(request.getUser().getLastKnownIP())
			.paymentHandleToken(paymentHandle.getPaymentHandleToken())
			.build();
			final HttpEntity<StandaloneCredits> entity = new HttpEntity<>(standaloneCredits, buildHeaders(request));
			StandaloneCredits standaloneCreditsResponse = rest.exchange(request.getProperty("baseUrl") + "/v1/standalonecredits", HttpMethod.POST, entity, StandaloneCredits.class).getBody();
			buildRawResponseLog(response, standaloneCreditsResponse);
			log.debug("standaloneCreditsResponse:: " + standaloneCreditsResponse);
			switch (standaloneCreditsResponse.getStatus()) {
				case INITIATED:
				case PENDING:
				case RECEIVED:
				case COMPLETED:
					return DoProcessorResponseStatus.NEXTSTAGE;
				case FAILED:
				case CANCELLED:
					return DoProcessorResponseStatus.DECLINED;
				default:
					return DoProcessorResponseStatus.FATALERROR; // Unexpected status for stage
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return DoProcessorResponseStatus.FATALERROR;
		}
	}

	@Override
	protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		return DoProcessorResponseStatus.NOOP;
	}

	private PaymentHandle createPaymentHandle(
		DoProcessorRequest request,
		RestTemplate rest
	) throws Exception {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		List<Link> returnLinks = new ArrayList<>();
		returnLinks.add(
			Link.builder()
			.rel(Rel.DEFAULT.rel().toLowerCase())
			.href(request.getProperty("returnurl"))
			.method("GET").build()
		);
		TransactionType transactionType = TransactionType.PAYMENT;
		if (!request.getTransactionType().description().equalsIgnoreCase("DEPOSIT")) {
			transactionType = TransactionType.STANDALONE_CREDIT;
		}
		PaymentHandle paymentHandleRequest = PaymentHandle.builder()
		.merchantRefNum(String.valueOf(request.getTransactionId()))
		.transactionType(transactionType.type())
		.paymentType(PaymentType.NETELLER.type())
		.amount(request.processorCommunicationAmount().movePointRight(2).longValue())
		.currencyCode(request.getUser().getCurrency())
		.customerIp(request.getUser().getLastKnownIP())
		.billingDetails(
			BillingDetails.builder()
			.street(request.getUser().getResidentialAddress().getAddressLine1())
			.street2(request.getUser().getResidentialAddress().getAddressLine2())
			.city(request.getUser().getResidentialAddress().getCity())
			.zip(request.getUser().getResidentialAddress().getPostalCode())
			.country(request.getUser().getResidentialAddress().getCountryCode())
			.build()
		)
		.neteller(
			Neteller.builder()
			.consumerId(request.stageInputData(1, "netellerEmail"))
			.build()
		)
		.returnLinks(returnLinks)
		.build();
		log.debug("paymentHandleRequest:: " + paymentHandleRequest);
		final HttpEntity<PaymentHandle> entity = new HttpEntity<>(paymentHandleRequest, buildHeaders(request));
		PaymentHandle paymentHandleResponse = rest.exchange(request.getProperty("baseUrl") + "/v1/paymenthandles", HttpMethod.POST, entity, PaymentHandle.class).getBody();
		log.debug("paymentHandleResponse:: " + paymentHandleResponse);
		return paymentHandleResponse;
	}

	private HttpHeaders buildHeaders(DoProcessorRequest request) throws Exception {
		String pkUsername = request.getProperty("private_key_username");
		String pkPassword = request.getProperty("private_key_password");
		String simulator = request.getProperty("simulator");
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((pkUsername + ":" + pkPassword).getBytes()));
		if (!simulator.isEmpty()) headers.set("Simulator", simulator);
		return headers;
	}

	private Map<String, String> getQueryParamsFromUrl(String url) {
		String[] queryParams = url.substring(url.indexOf("?") + 1, url.length()).split("&");
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (String q: queryParams) {
			String key = q.substring(0, q.indexOf("="));
			String value = q.substring(q.indexOf("=") + 1, q.length());
			map.put(key, value);
		}
		return map;
	}
}
