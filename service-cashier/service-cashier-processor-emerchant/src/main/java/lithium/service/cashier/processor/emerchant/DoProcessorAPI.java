package lithium.service.cashier.processor.emerchant;

import com.emerchantpay.gateway.GenesisClient;
import com.emerchantpay.gateway.NotificationGateway;
import com.emerchantpay.gateway.api.constants.Endpoints;
import com.emerchantpay.gateway.api.constants.Environments;
import com.emerchantpay.gateway.api.exceptions.GenesisException;
import com.emerchantpay.gateway.api.exceptions.ResponseException;
import com.emerchantpay.gateway.api.requests.financial.card.Sale3DRequest;
import com.emerchantpay.gateway.api.requests.financial.card.SaleRequest;
import com.emerchantpay.gateway.util.Configuration;
import com.emerchantpay.gateway.util.NodeWrapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.emerchant.TransactionStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lombok.extern.slf4j.Slf4j;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Map;

/**
 * This class is not currently used but could be used in future when WPF is not used but rather the API
 */
@Slf4j
//@Service
public class DoProcessorAPI extends DoProcessorAdapter {
	@Autowired
	private LithiumConfigurationProperties configurationProperties;
	
	@Override
	protected DoProcessorResponseStatus depositStage1(
		DoProcessorRequest request,
		DoProcessorResponse response,
		DoProcessorContext context,
		RestTemplate rest
	) throws Exception {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});

		BigDecimal amount = new BigDecimal(request.stageInputData(1, "amount"));

		String gatewayUrl = configurationProperties.getGatewayPublicUrl();
//		gatewayUrl = "http://004fb30c.ngrok.io";  //Riaan external GW IP.

		String notificationUrl = gatewayUrl + request.getProperty("callbackUrl");
		String returnUrl = gatewayUrl + "/service-cashier/frontend/loadingrefresh";

		Environments env = Environments.STAGING;
		String envProperty = request.getProperty("environment");
		if ((env != null) && ("PRODUCTION".equalsIgnoreCase(envProperty))) env = Environments.PRODUCTION;

		Configuration configuration = new Configuration(env, Endpoints.EMERCHANTPAY);
		configuration.setUsername(request.getProperty("username"));
		configuration.setPassword(request.getProperty("password"));
		configuration.setToken(request.getProperty("token"));
		configuration.setDebugMode(((env != null) && ("PRODUCTION".equalsIgnoreCase(envProperty)))?false:true);

		String nameoncard = request.stageInputData(1, "nameoncard");
		String ccnumber = request.stageInputData(1, "ccnumber");
		String expmonth = request.stageInputData(1, "expmonth");
		String expyear = request.stageInputData(1, "expyear");
		String cvv = request.stageInputData(1, "cvv");
		String is3dSecure = request.getProperty("3dsecure");
		GenesisClient client = null;
		log.debug("input params extracted: " + nameoncard + " " + ccnumber + " " + expmonth + " " + expyear + " " + cvv + " " + is3dSecure);
		try {
			if ("yes".equalsIgnoreCase(is3dSecure)) {
				Sale3DRequest sale3DRequest = new Sale3DRequest();

				sale3DRequest
					.setTransactionId(request.getTransactionId() + "")
					.setUsage(request.getUser().getDomain() + " Deposit")
					.setRemoteIp(request.getUser().getLastKnownIP());
				sale3DRequest.setGaming(true);
				sale3DRequest.setCurrency(request.getUser().getCurrency());
//				sale3DRequest.setCurrency("USD");
				sale3DRequest.setAmount(amount);
				sale3DRequest.setCustomerEmail(request.getUser().getEmail());

				sale3DRequest.setCardHolder(nameoncard)
					.setCardNumber(ccnumber)
					.setExpirationMonth(expmonth)
					.setExpirationYear(expyear)
					.setCvv(cvv);
				sale3DRequest.setBillingFirstname(request.getUser().getFirstName())
					.setBillingLastname(request.getUser().getLastName())
					.setBillingPrimaryAddress(request.getUser().getResidentialAddress().getAddressLine1())
					.setBillingZipCode(request.getUser().getResidentialAddress().getPostalCode())
					.setBillingCity(request.getUser().getResidentialAddress().getCity())
					.setBillingState(request.getUser().getResidentialAddress().getAdminLevel1())
					.setBillingCountry(request.getUser().getResidentialAddress().getCountry());

				sale3DRequest.setNotificationUrl(new URL(notificationUrl));
				sale3DRequest.setReturnFailureUrl(new URL(returnUrl));
				sale3DRequest.setReturnSuccessUrl(new URL(returnUrl));
				
				client = new GenesisClient(configuration, sale3DRequest);
				String xml = sale3DRequest.toXML();
				log.debug("XML REQUEST : " + xml);
				String xmlSanitize = XML.toJSONObject(xml).toString(2);
				log.debug("xmlSanitize : " + xmlSanitize);
				buildRawRequestLog(request, response, xmlSanitize);
			} else {
				SaleRequest saleRequest = new SaleRequest();

				saleRequest
					.setTransactionId(request.getTransactionId() + "")
					.setUsage(request.getUser().getDomain() + " Deposit")
					.setRemoteIp(request.getUser().getLastKnownIP());
				saleRequest.setGaming(true);
				saleRequest.setCurrency(request.getUser().getCurrency());
//				saleRequest.setCurrency("USD");
				saleRequest.setAmount(amount);
				saleRequest.setCustomerEmail(request.getUser().getEmail());

				saleRequest.setCardHolder(nameoncard)
					.setCardNumber(ccnumber)
					.setExpirationMonth(expmonth)
					.setExpirationYear(expyear)
					.setCvv(cvv);
				saleRequest.setBillingFirstname(request.getUser().getFirstName()) // FIXME: 2019/12/04 we might need to use name on card here and in surname
					.setBillingLastname(request.getUser().getLastName())
					.setBillingPrimaryAddress(request.getUser().getResidentialAddress().getAddressLine1())
					.setBillingZipCode(request.getUser().getResidentialAddress().getPostalCode())
					.setBillingCity(request.getUser().getResidentialAddress().getCity())
					.setBillingState(request.getUser().getResidentialAddress().getAdminLevel1())
					.setBillingCountry(request.getUser().getResidentialAddress().getCountry());

				client = new GenesisClient(configuration, saleRequest);
				String xml = saleRequest.toXML();
				log.debug("XML REQUEST : " + xml);
				String xmlSanitize = XML.toJSONObject(xml).toString(2);
				log.debug("xmlSanitize : " + xmlSanitize);
				buildRawRequestLog(request, response, xmlSanitize);
			}
			client.debugMode(((env != null) && ("PRODUCTION".equalsIgnoreCase(envProperty)))?false:true);
			client.execute();
		} catch (GenesisException ge) {
			log.error("Could not parse response. "+ge.getMessage());
			response.setMessage(ge.getMessage());
			response.setStatus(DoProcessorResponseStatus.INPUTERROR);
			return response.getStatus();
		}


		NodeWrapper nodeWrapper = client.getResponse();
		log.debug("nodeWrapper: "+nodeWrapper+" success: "+nodeWrapper.isSuccess());

		String processorResponse = nodeWrapper.toString();
		log.debug("payment_response: "+nodeWrapper.findString("payment_response"));
		log.debug("processorResponse: "+processorResponse);
		try {
			// Parse Payment result
//			TransactionResult<? extends Transaction> result = client.getTransaction().getRequest();

			response.setBillingDescriptor(nodeWrapper.findString("descriptor")); //result.getTransaction().getDescriptor());
			response.setProcessorReference(nodeWrapper.findString("unique_id")); //result.getTransaction().getUniqueId());
			response.setMessage(nodeWrapper.findString("message")); //result.getTransaction().getMessage());
			response.setOutputData(1, "technical_message", nodeWrapper.findString("technical_message")); //result.getTransaction().getTechnicalMessage());

			String status = nodeWrapper.findString("status"); // result.getTransaction().getStatus();
			TransactionStatus transactionStatus = TransactionStatus.fromStatus(status);
			log.debug("status: "+status+" transactionStatus: "+transactionStatus);
			switch (transactionStatus) {
				case APPROVED:
					response.setStatus(DoProcessorResponseStatus.SUCCESS);
					break;
				case DECLINED:
					response.setStatus(DoProcessorResponseStatus.DECLINED);
					break;
				case PENDING_ASYNC:
					response.setStatus(DoProcessorResponseStatus.IFRAMEPOST_NEXTSTAGE); //Can not put it on next stage, if customer cancels on 3d secure we are stuck in pending.
					response.setIframeMethod("GET");
					response.setIframeUrl(nodeWrapper.findString("redirect_url")); //result.getTransaction().getRedirectUrl());
					break;
				case ERROR:
				case VOIDED:
				case PENDING:
				case REFUNDED:
				case REPRESENTED:
				case CHARGEBACKED:
				case PENDING_REVIEW:
				case CHARGEBACK_REVERSED:
				case SECOND_CHARGEBACKED:
					// TODO: 2020/01/24 We should probably look at implementing this in a more robust way
					response.setStatus(DoProcessorResponseStatus.DECLINED);
					break;
			}
//			processorResponse = result.getTransaction().getDocument();
			try {
				recon(request, client.getResponse().getFormParameters());
			} catch (GenesisException ge) {
				// It is possible the recon will fail due to missing params, we should not care about that and just carry on.
				log.debug("Error doing recon on transaction" + request.getTransactionId(), ge);
			}
			log.debug("processorResponse: "+processorResponse);
		} catch (ResponseException re) {
			log.error("Could not parse response. "+nodeWrapper.findString("technical_message")+" :: "+re.getMessage());
			response.setMessage(nodeWrapper.findString("technical_message"));
			response.setStatus(DoProcessorResponseStatus.DECLINED);
		} catch (Exception e) {
			log.error("Error: " + e.getMessage(), e);
			response.setMessage(e.getMessage());
			response.setStatus(DoProcessorResponseStatus.FATALERROR);
		} finally {
			buildRawResponseLog(response, processorResponse);
		}
		return response.getStatus();
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

//	public static void main(String[] args) {
//		SaleRequest sr = new SaleRequest();
//		sr.setCvv("123");
//		//sr.setCardHolder("jerry kjkjkj jjjjjjjjjjjjjjjjjjj jjjjjjjjjjjjjjjjjjj");
//		GenesisValidator gv = new GenesisValidator();
//		//log.warn("" +gv.isValidCardHolder("jerry"));
//	}

	private  String recon(DoProcessorRequest request, Map<String, String> params) throws Exception {
		Environments env = Environments.STAGING;
		String envProperty = request.getProperty("environment");
		if ((env != null) && ("PRODUCTION".equalsIgnoreCase(envProperty))) env = Environments.PRODUCTION;

		Configuration configuration = new Configuration(env, Endpoints.EMERCHANTPAY);
		configuration.setUsername(request.getProperty("username"));
		configuration.setPassword(request.getProperty("password"));
		configuration.setToken(request.getProperty("token"));
		configuration.setDebugMode(true);

		NotificationGateway gw = new NotificationGateway(configuration, params);
		gw.initReconciliation();
		gw.generateResponse();
		return gw.getResponse().toXML();
	}
}
