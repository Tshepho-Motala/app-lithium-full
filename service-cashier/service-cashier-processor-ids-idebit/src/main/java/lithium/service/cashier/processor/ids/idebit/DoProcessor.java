package lithium.service.cashier.processor.ids.idebit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.ids.DoProcessorIdsAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.ids.idebit.data.PaymentNotificationResponse;
import lithium.service.cashier.processor.ids.idebit.data.PaymentRedirectRequest;
import lithium.service.cashier.processor.ids.idebit.data.PayoutRequest;
import lithium.service.cashier.processor.ids.idebit.data.PayoutResponse;
import lithium.service.cashier.processor.ids.idebit.data.ReturnNotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class DoProcessor extends DoProcessorIdsAdapter {
	private static final String PAYMENT_REDIRECT_URL_PATH = "/merGateway.do";
	@Autowired
	private LithiumConfigurationProperties config;
	
	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});

		PaymentRedirectRequest paymentRedirectRequest = PaymentRedirectRequest.builder()
				.merchantId(request.getProperty(PROPERTY_MERCHANT_ID))
				.merchantSubId(request.getProperty(PROPERTY_MERCHANT_SUB_ID))
				.userGuid(request.getUser().getShortGuid())
				.transactionNumber(request.getTransactionId().toString())
				.amountDecimalString(request.stageInputData(1, "amount"))
				.currencyCode(request.getUser().getCurrency())
				.firstName(request.getUser().getFirstName())
				.middleName("")
				.lastName(request.getUser().getLastName())
				.addressLineOne(request.getUser().getResidentialAddress() != null ? request.getUser().getResidentialAddress().getAddressLine1() : null)
				.addressLineTwo(request.getUser().getResidentialAddress() != null ? request.getUser().getResidentialAddress().getAddressLine2() : null)
				.city(request.getUser().getResidentialAddress() != null ? request.getUser().getResidentialAddress().getCity() : null)
				.state(request.getUser().getResidentialAddress() != null ? request.getUser().getResidentialAddress().getAdminLevel1() : null)
				.zip(request.getUser().getResidentialAddress() != null ? request.getUser().getResidentialAddress().getPostalCode() : null)
				.country(request.getUser().getResidentialAddress() != null ? request.getUser().getResidentialAddress().getCountryCode() : null)
				.homePhoneAreaCode("")
				.homePhoneLocalNumber("")
				.dateOfBirthDay(""+request.getUser().getDobDay())
				.dateOfBirthMonth(""+request.getUser().getDobMonth())
				.dateOfBirthYear(""+request.getUser().getDobYear())
				.additionalData("")
				.returnUrl(request.stageInputData(1, "iframeDepositCallbackUrl"))
				.languageCode(Locale.forLanguageTag(request.getUser().getLocale()).getLanguage())
				//.redirectUrl(config.getGatewayPublicUrl() + request.getProperty(PROPERTY_CALLBACK_URL))
				.build();


		//TODO: Look into a clean way to do this. I'm sure there must be some way.
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(paymentRedirectRequest);
		JsonNode root = mapper.readTree(json);
		final Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
		Map<String, String> kvMap = new HashMap<>();
		while (fields.hasNext()) {
			Map.Entry<String, JsonNode> entry = fields.next();
			kvMap.put(entry.getKey(), entry.getValue().asText());
		}
		String urlInstanceName = request.getProperty(PROPERTY_PROCESSOR_IMPL_NAME).equalsIgnoreCase("IDEBIT") ? "/consumer" : "/instadebit";
		response.setIframeUrl(request.getProperty(PROPERTY_BASE_URL) + urlInstanceName +PAYMENT_REDIRECT_URL_PATH);
		response.setIframePostData(kvMap);
		response.setRawResponseLog(json);
		return DoProcessorResponseStatus.IFRAMEPOST;
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});

		//We received the notification from IDS and will now echo back the response
		if (request.getPreviousProcessorRequest() != null && request.getPreviousProcessorRequest() instanceof Map) {
			response.setRawRequestLog(request.getPreviousProcessorRequest().toString());
			PaymentNotificationResponse paymentNotificationResponse = new PaymentNotificationResponse();
			paymentNotificationResponse.mapParametersToObject((Map)request.getPreviousProcessorRequest());

			if (paymentNotificationResponse.getStatus().equalsIgnoreCase("S")) {
				response.setTransactionId(Long.parseLong(paymentNotificationResponse.getTransactionNumber()));
				response.setProcessorReference(paymentNotificationResponse.getIdsTransactionNumber());

				response.setStatus(DoProcessorResponseStatus.SUCCESS);
				response.setRawResponseLog(postPaymentNotification(request.getProperty(PROPERTY_BASE_URL), paymentNotificationResponse.getOriginalParameterMap()));
				return DoProcessorResponseStatus.SUCCESS;
			} else {
				response.setStatus(DoProcessorResponseStatus.DECLINED);
				response.setRawResponseLog(postPaymentNotification(request.getProperty(PROPERTY_BASE_URL), paymentNotificationResponse.getOriginalParameterMap()));
				return DoProcessorResponseStatus.DECLINED;
			}
		} else {
			// We did not receive a valid callback so we just keep waiting. This is very unlikely to happen
			response.setStatus(DoProcessorResponseStatus.NOOP);
			return DoProcessorResponseStatus.NOOP;
		}
	}

	/**
	 * This withdrawal stage will be called once an admin approves the withdrawal request.
	 * @param request
	 * @param response
	 * @param context
	 * @param rest
	 * @return
	 * @throws Exception
	 */
	protected DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		Long amount = request.inputAmountCents();
		PayoutRequest payoutRequest = PayoutRequest.builder()
				.merchantId(request.getProperty(PROPERTY_MERCHANT_ID))
				.perchantPassword(request.getProperty(PROPERTY_MERCHANT_PASSWORD))
				.idsUserId(request.getProcessorUserId())
				.userGuid(request.getUser().getGuid())
				.transactionType("F")
				.transactionNumber(request.getTransactionId().toString())
				.amountDecimalString(request.inputAmount().toPlainString())
				.currencyCode(request.getUser().getCurrency())
				.build();

		try {
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(payoutRequest);
			JsonNode root = mapper.readTree(json);
			List<NameValuePair> params = new ArrayList<>();
			final Iterator<Map.Entry<String, JsonNode>> fields = root.fields();

			while (fields.hasNext()) {
				Map.Entry<String, JsonNode> entry = fields.next();
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().asText()));
			}

			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(request.getProperty(PROPERTY_BASE_URL) + "/service/servlet/MerchantPayout");

			httpPost.setEntity(new UrlEncodedFormEntity(params));
			response.setRawRequestLog(httpPost.toString());
			CloseableHttpResponse httpResponse = client.execute(httpPost);
			InputStream content = httpResponse.getEntity().getContent();
			String data = IOUtils.toString(httpResponse.getEntity().getContent(), "UTF-8");
			response.setRawResponseLog(data);
			PayoutResponse payoutResonse = populatePayoutResponseFromHtmlDataString(data);
			if (payoutResonse.getStatus().equalsIgnoreCase("S")) {
				response.setTransactionId(request.getTransactionId());
				response.setProcessorReference(payoutResonse.getIdsTransactionNumber());
				response.setStatus(DoProcessorResponseStatus.SUCCESS);
				response.setProcessorUserId(request.getProcessorUserId());
			} else {
				response.setTransactionId(request.getTransactionId());
				response.setProcessorReference(payoutResonse.getIdsTransactionNumber());
				response.setStatus(DoProcessorResponseStatus.DECLINED);
				response.setProcessorUserId(request.getProcessorUserId());
				response.setMessage(payoutResonse.getErrorString());
			}
			// TODO: 2019/07/11 Should possibly log this response to substantiate disputes with IDS
			client.close();
		} catch (Exception ex) {
			log.error("Problem sending payment data: " + payoutRequest);
		}

		return DoProcessorResponseStatus.SUCCESS;
	}

	@Override
	protected DoProcessorResponseStatus reverseStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		log.info("request reversal: " + request);

		//We received the reversal request from IDS and will now echo back the response
		if (request.getPreviousProcessorRequest() != null && request.getPreviousProcessorRequest() instanceof Map) {
			ReturnNotificationResponse returnNotificationResponse = new ReturnNotificationResponse();
			returnNotificationResponse.mapParametersToObject((Map)request.getPreviousProcessorRequest());

			response.setTransactionId(Long.parseLong(returnNotificationResponse.getOriginalTransactionNumber()));
			response.setProcessorReference(returnNotificationResponse.getIdsTransactionNumber());
			response.addRawResponseLog(returnNotificationResponse.toString());
			response.setStatus(DoProcessorResponseStatus.SUCCESS);
			response.addRawResponseLog(postPaymentNotification(request.getProperty(PROPERTY_BASE_URL), returnNotificationResponse.getOriginalParameterMap()));
			return DoProcessorResponseStatus.SUCCESS;
		} else {
			// We did not receive a valid callback so we just keep waiting. This is very unlikely to happen
			response.setStatus(DoProcessorResponseStatus.NOOP);
			return DoProcessorResponseStatus.NOOP;
		}
	}

	private String postPaymentNotification(String baseUrl, Map<String, String> paramMap) {
		try {
			List<NameValuePair> params = new ArrayList<>();
			paramMap.forEach((k,v) -> {
				params.add(new BasicNameValuePair(k, v));
			});
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(baseUrl + "/service/servlet/ConfirmTrans"); //hash is processor module name

			httpPost.setEntity(new UrlEncodedFormEntity(params));

			CloseableHttpResponse response = client.execute(httpPost);
			String data = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
			client.close();
			return data;
		} catch (Exception ex) {
			log.error("Problem sending verification data: " + paramMap);
			return "The payment notification message sending had a problem. "+ ex;
		}
	}

	private PayoutResponse populatePayoutResponseFromHtmlDataString(final String htmlString) {
		Document doc = Jsoup.parse(htmlString);
		PayoutResponse payoutResonse = new PayoutResponse();
		Map<String, String> paramMap = new HashMap<>();
		for (Element e : doc.body().getElementsByTag("input")) {
			paramMap.put(e.attr("name"), e.val());
		}
		payoutResonse.mapParametersToObject(paramMap);
		return payoutResonse;
	}
}
