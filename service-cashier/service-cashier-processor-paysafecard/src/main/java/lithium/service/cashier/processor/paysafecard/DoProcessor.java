package lithium.service.cashier.processor.paysafecard;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.paysafecard.DoProcessorPaysafecardAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.skrill.data.QuickCheckoutRequest;
import lithium.util.ObjectToHttpEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class DoProcessor extends DoProcessorPaysafecardAdapter {
	@Autowired RestTemplate restTemplate;

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
			.paymentMethods("PSC")
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
}
