package lithium.service.cashier.processor.premierpay.interac;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.premierpay.DoProcessorPremierPayAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessor extends DoProcessorPremierPayAdapter {
	@Autowired
	private LithiumConfigurationProperties configurationProperties;

	@Override
	protected DoProcessorResponseStatus depositStage1(
		DoProcessorRequest request,
		DoProcessorResponse response,
		DoProcessorContext context,
		RestTemplate rest
	) throws Exception {
		BigDecimal amount = request.inputAmount();

		String payby = "interac";
		String gatewayUrl = configurationProperties.getGatewayPublicUrl();
//		gatewayUrl = "http://196.22.242.140:29001";  //Riaan external GW IP.

		String notificationUrl = gatewayUrl + request.getProperty("callbackUrl") + "?callback=notify";
		String returnUrl = gatewayUrl + request.getProperty("callbackUrl") + "?callback=return";
		log.debug("notificationUrl :: " + notificationUrl);

		Map<String, String> iframePostData = new HashMap<>();
		iframePostData.put("sid", request.getProperty("sid"));
		iframePostData.put("rcode", request.getProperty("rcode"));
		iframePostData.put("return_url", returnUrl);
		iframePostData.put("notification_url", notificationUrl);
		iframePostData.put("item_amount_unit[0]", amount.toPlainString());
		iframePostData.put("item_quantity[0]", "1");
		iframePostData.put("item_name[0]", "Deposit");
		iframePostData.put("payby", payby);
		iframePostData.put("direction", "in"); // undocumented 'feauture'
		iframePostData.put("udf1", request.getTransactionId() + "");
		iframePostData.put("udf2", request.getUser().getGuid());
		iframePostData.put("udf3", payby);
		if (request.getProcessorUserId()!=null) iframePostData.put("customer_id", request.getProcessorUserId());
		iframePostData.put("firstname", request.getUser().getFirstName());
		iframePostData.put("lastname", request.getUser().getLastName());
		iframePostData.put("email", request.getUser().getEmail());
		iframePostData.put("phone", request.getUser().getCellphoneNumber());
		iframePostData.put("address", request.getUser().getResidentialAddress().getAddressLine1());
		iframePostData.put("city", request.getUser().getResidentialAddress().getCity());
		String[] states = request.getUser().getResidentialAddress().getAdminLevel1Code().split("\\.");
		iframePostData.put("state", states[1]);
		iframePostData.put("country", request.getUser().getResidentialAddress().getCountryCode());
		iframePostData.put("zip_code", request.getUser().getResidentialAddress().getPostalCode());

		//TODO: Needs to be stored/retrieved from somewhere.
		// iframePostData.put("customer_id", "");

		String test = "";

		try {
			test = request.getProperty("test");
		} catch (Exception e) {
			// This is a test property, and we don't care if it's not there.
		}
		if ("test".equalsIgnoreCase(test)) response.setIframeUrl(request.getProperty("baseUrl") + "app_dev.php/");
		if (!"test".equalsIgnoreCase(test)) response.setIframeUrl(request.getProperty("baseUrl"));
		response.setIframePostData(iframePostData);
		response.setIframeMethod("POST");
		response.setIframeWindowTarget("_blank");
		response.setAmount(amount);

		buildRawRequestLog(request, response, null);
		buildRawResponseLog(response, null);
		/* FIXME: 2019/12/02 We can't increment the stage since we don't preserve the response state and can not retrieve it in future incomplete requests.
			We will need to build a better state engine if we want to keep the iframe url and params etc. However this poses a risk since it means out trans can not resume
			stateless in case of a config modification and there will be no way to clear out these transactions.
			We should consider adding a notifier service of incomplete transactions and have a CS rep handle them by either ending or contacting customer
		 */
		// return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
		return DoProcessorResponseStatus.IFRAMEPOST;
	}

	@Override
	protected DoProcessorResponseStatus depositStage2(
		DoProcessorRequest request,
		DoProcessorResponse response,
		DoProcessorContext context,
		RestTemplate rest
	) throws Exception {
		return DoProcessorResponseStatus.NOOP;
	}
}
