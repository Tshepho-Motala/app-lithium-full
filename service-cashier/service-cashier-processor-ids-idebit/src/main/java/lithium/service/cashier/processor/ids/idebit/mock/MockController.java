package lithium.service.cashier.processor.ids.idebit.mock;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping
public class MockController {
	@Autowired
	private LithiumConfigurationProperties config;

	// TODO: 2019/07/10 Add failure scenario logic
	// TODO: 2019/07/10 Add return message scenario with some delay to allow completion of original request
	// TODO: 2019/07/10 Evaluate notification response for consistency with original post data
	// TODO: 2019/07/10 Add transaction verification response message


	/**
	 * Perform a call to cashier callback url to mock the processor sending back a payment notification
	 * @param request
	 * @return
	 */
	@RequestMapping("/consumer/paymentNotification")
	public View performPaymentProcessing(WebRequest request) {
		log.info("Request received into mock for payment request: " + request.toString());

		postPaymentNotification(buildNotificationCallback(request));

		return new RedirectView(request.getParameter("return_url"));
	}

	/**
	 * Perform a call to cashier callback url to mock the processor sending back a reversal/return notification
	 * @param request
	 * @return
	 */
	@RequestMapping("/consumer/returnNotification")
	public View performReturnProcessing(WebRequest request) {
		log.info("Request received into mock for return/reversal request: " + request.toString());

		postPaymentNotification(buildReturnCallback(request));

		return new RedirectView(request.getParameter("return_url"));
	}

	@RequestMapping(value = "/service/servlet/ConfirmTrans", produces = "text/html")
	public ResponseEntity<String> performVerificationProcessing(WebRequest request) throws Exception {
		log.info("Request received into mock for notification request: " + request.toString());

		//response.setStatus(HttpStatus.SC_OK);
		//response.getWriter().println("<input type=\"hidden\" name=\"verification_code\" value=\"0\"");
		return ResponseEntity.status(HttpStatus.OK).body("<input type=\"hidden\" name=\"verification_code\" value=\"0\"/>");
	}

	@RequestMapping(value = "/service/servlet/MerchantPayout", produces = "text/html")
	public ResponseEntity<String> performMerchantPayout(WebRequest request) throws Exception {
		log.info("Request received into mock for payout request: " + request.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("<input type=\"hidden\" name=\"txn_num\" value=\""+"ids_"+request.getParameter("merchant_txn_num")+"\" />");
		sb.append("<input type=\"hidden\" name=\"txn_fee\" value=\"0.00\" />");
		sb.append("<input type=\"hidden\" name=\"txn_status\" value=\"S\" />"); //S=success, X=error
		sb.append("<input type=\"hidden\" name=\"error_code\" value=\"\" />");

		return ResponseEntity.status(HttpStatus.OK).body(sb.toString());
	}

	/**
	 * Http client init, sending of payload and closing
	 *
	 * @param params
	 */
	private void postPaymentNotification(List<NameValuePair> params) {
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(config.getGatewayPublicUrl() + "/service-cashier/external/callback/do/idebit/87cde942c78863495267c1202291c58a47564f1b"); //hash is processor module name

			httpPost.setEntity(new UrlEncodedFormEntity(params));

			CloseableHttpResponse response = client.execute(httpPost);
			client.close();
		} catch (Exception ex) {
			log.error("Problem sending payment notification callback");
		}
	}

	/**
	 * Build up a post request for the notification callback payload
	 *
	 * @param request
	 * @return
	 */
	private List<NameValuePair> buildNotificationCallback(WebRequest request) {
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("user_id", "ids/"+request.getParameter("merchant_user_id")));
		params.add(new BasicNameValuePair("txn_num", "ids_" + request.getParameter("merchant_txn_num")));
		params.add(new BasicNameValuePair("txn_type", "T"));
		params.add(new BasicNameValuePair("merchant_id", request.getParameter("merchant_id")));
		params.add(new BasicNameValuePair("merchant_user_id", request.getParameter("merchant_user_id")));
		params.add(new BasicNameValuePair("merchant_txn_num", request.getParameter("merchant_txn_num")));
		params.add(new BasicNameValuePair("txn_amount", request.getParameter("txn_amount")));
		params.add(new BasicNameValuePair("txn_fee", "0.5"));
		params.add(new BasicNameValuePair("txn_currency", request.getParameter("txn_currency")));
		params.add(new BasicNameValuePair("txn_status", "S")); //All other is failure
		params.add(new BasicNameValuePair("error_code", ""));
		params.add(new BasicNameValuePair("extra_field_1", request.getParameter("extra_field_1"))); //I = instant EFT A = traritional EFT
		params.add(new BasicNameValuePair("channel", "A")); //I = instant EFT A = traritional EFT

		log.info("parameters for notification mock" + params.toString());
		return params;
	}

	/**
	 * Build up a post request for the notification callback payload
	 *
	 * @param request
	 * @return
	 */
	private List<NameValuePair> buildReturnCallback(WebRequest request) {
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("user_id", "ids/"+request.getParameter("merchant_user_id")));
		params.add(new BasicNameValuePair("txn_num", "ids_" + request.getParameter("merchant_txn_num") + "_reversal"));
		params.add(new BasicNameValuePair("txn_type", "R"));
		params.add(new BasicNameValuePair("merchant_id", request.getParameter("merchant_id")));
		params.add(new BasicNameValuePair("merchant_user_id", request.getParameter("merchant_user_id")));
		params.add(new BasicNameValuePair("merchant_txn_num", request.getParameter("merchant_txn_num")));
		params.add(new BasicNameValuePair("txn_amount", request.getParameter("txn_amount")));
		params.add(new BasicNameValuePair("txn_fee", "0.5"));
		params.add(new BasicNameValuePair("txn_currency", request.getParameter("txn_currency")));
		params.add(new BasicNameValuePair("original_txn_num", "ids_" + request.getParameter("merchant_txn_num")));
		params.add(new BasicNameValuePair("error_code", ""));
		params.add(new BasicNameValuePair("return_code", "01")); //01 is insufficient funds

		log.info("parameters for return/reversal mock" + params.toString());
		return params;
	}

	class VerificationResponse {
		@Getter
		private String verification_code = "0";
	}
}
