package lithium.service.cashier.processor.emerchant;

import com.emerchantpay.gateway.GenesisClient;
import com.emerchantpay.gateway.api.constants.ConsumerEndpoints;
import com.emerchantpay.gateway.api.constants.Endpoints;
import com.emerchantpay.gateway.api.constants.Environments;
import com.emerchantpay.gateway.api.exceptions.GenesisException;
import com.emerchantpay.gateway.api.exceptions.ResponseException;
import com.emerchantpay.gateway.api.requests.nonfinancial.consumer.RetrieveConsumerRequest;
import com.emerchantpay.gateway.api.requests.wpf.WPFCreateRequest;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

@Slf4j
@Service
public class DoProcessor extends DoProcessorAdapter {
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
//		gatewayUrl = "http://53d100b9.ngrok.io";  //Riaan external GW IP.

		String notificationUrl = gatewayUrl + request.getProperty("callbackUrl");
		String returnUrl = gatewayUrl + "/service-cashier/frontend/loadingrefresh";
		if("_blank".contentEquals(request.getProperty("iframelocation"))) {
			returnUrl = gatewayUrl + "/service-cashier/frontend/closepage";
		}

		Environments env = Environments.STAGING;
		String envProperty = request.getProperty("environment");
		if ((env != null) && ("PRODUCTION".equalsIgnoreCase(envProperty))) env = Environments.PRODUCTION;

		Configuration configuration = new Configuration(env, Endpoints.EMERCHANTPAY);
		configuration.setUsername(request.getProperty("username"));
		configuration.setPassword(request.getProperty("password"));
		configuration.setToken(request.getProperty("token"));
		configuration.setDebugMode(((env != null) && ("PRODUCTION".equalsIgnoreCase(envProperty)))?false:true);
		configuration.setWpfEnabled(true);

		try {
			configuration.setLanguage(Locale.forLanguageTag(context.getRequest().getUser().getLocale().substring(0,2)));
		} catch (Exception e) {
			log.debug("Could not set locale for user in emerchantpay, no worries.");
		}

		GenesisClient client = null;
		WPFCreateRequest wpfCreateRequest = new WPFCreateRequest();
		if (request.getProcessorUserId() != null) {
			wpfCreateRequest.setConsumerId(request.getProcessorUserId());
		} else {
			wpfCreateRequest.setConsumerId(consumerLookup(request, request.getUser().getEmail()));
		}
		wpfCreateRequest.setRememberCard(true);
		wpfCreateRequest.setDescription(request.getUser().getDomain() + " Deposit");
		wpfCreateRequest.setTransactionId(request.getTransactionId() + "");
		wpfCreateRequest.setUsage(request.getUser().getDomain() + " Deposit");
		wpfCreateRequest.setRemoteIp(request.getUser().getLastKnownIP());
		wpfCreateRequest.setCurrency(request.getUser().getCurrency());
		wpfCreateRequest.setAmount(amount);
		wpfCreateRequest.setCustomerEmail(request.getUser().getEmail());
		wpfCreateRequest.setBillingFirstname(request.getUser().getFirstName());
		wpfCreateRequest.setBillingLastname(request.getUser().getLastName());
		wpfCreateRequest.setBillingPrimaryAddress(request.getUser().getResidentialAddress().getAddressLine1());
		wpfCreateRequest.setBillingZipCode(request.getUser().getResidentialAddress().getPostalCode());
		wpfCreateRequest.setBillingCity(request.getUser().getResidentialAddress().getCity());
		wpfCreateRequest.setBillingState(stateISOResolver(request.getUser().getResidentialAddress().getAdminLevel1Code()));
		wpfCreateRequest.setBillingCountry(request.getUser().getResidentialAddress().getCountry());
		wpfCreateRequest.setNotificationUrl(new URL(notificationUrl));
		wpfCreateRequest.setReturnFailureUrl(new URL(returnUrl));
		wpfCreateRequest.setReturnSuccessUrl(new URL(returnUrl));
		wpfCreateRequest.setReturnCancelUrl(new URL(returnUrl));
		String transactionTypesString = request.getProperty("transactiontypes");
		ArrayList<String> transactionTypeList = new ArrayList<>();
		Collections.addAll( transactionTypeList, transactionTypesString.split(","));
		wpfCreateRequest.addTransactionTypes(transactionTypeList);
		try {
			int ttl = Integer.parseInt(request.getProperty("ttl"));
			if (ttl > 0) {
				wpfCreateRequest.setLifetime((ttl / 60) / 1000);
			}
		} catch (Exception e) {
			log.debug("Could not set ttl on emerchantpay processor, this is not an issue");
		}
		wpfCreateRequest.setPayLater(false);
		try {
			client = new GenesisClient(configuration, wpfCreateRequest);
			String xml = wpfCreateRequest.toXML();
			log.debug("XML REQUEST : " + xml);
			String xmlSanitize = XML.toJSONObject(xml).toString(2);
			log.debug("xmlSanitize : " + xmlSanitize);
			buildRawRequestLog(request, response, xmlSanitize);
			client.debugMode(((env != null) && ("PRODUCTION".equalsIgnoreCase(envProperty)))?false:true);
			client.execute();
		} catch (GenesisException ge) {
			log.error("Could not parse response. "+ge.getMessage(), ge);
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
				case NEW:
				case PENDING_ASYNC:
					response.setStatus(DoProcessorResponseStatus.IFRAMEPOST_NEXTSTAGE); //Can not put it on next stage, if customer cancels on 3d secure we are stuck in pending.
					response.setIframeMethod("GET");
					response.setIframeUrl(nodeWrapper.findString("redirect_url")); //result.getTransaction().getRedirectUrl());
					response.setProcessorUserId(nodeWrapper.findString("consumer_id"));
					response.setProcessorReference(nodeWrapper.findString("unique_id"));
					response.setIframeWindowTarget(request.getProperty("iframelocation"));
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
			log.debug("processorResponse: "+processorResponse);
			//This needs to always be written to avoid missing consumer error
			response.setProcessorUserId(nodeWrapper.findString("consumer_id"));
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

	/**
	 * Check if a consumer is on emerchant side, if it finds one, we use it if not we return null
	 * @param request
	 * @param email
	 * @return
	 */
	private  String consumerLookup(DoProcessorRequest request, String email) {
		log.debug("Looking up emerchant consumer: " + email);
		try {
			Environments env = Environments.STAGING;
			String envProperty = request.getProperty("environment");
			if ((env != null) && ("PRODUCTION".equalsIgnoreCase(envProperty))) env = Environments.PRODUCTION;

			Configuration configuration = new Configuration(env, Endpoints.EMERCHANTPAY, ConsumerEndpoints.RETRIEVE_CONSUMER, ConsumerEndpoints.CONSUMER_API_VERSION);
			configuration.setUsername(request.getProperty("username"));
			configuration.setPassword(request.getProperty("password"));
			configuration.setToken(request.getProperty("token"));
			configuration.setDebugMode(((env != null) && ("PRODUCTION".equalsIgnoreCase(envProperty))) ? false : true);

			RetrieveConsumerRequest retrieveConsumerRequest = new RetrieveConsumerRequest();
			retrieveConsumerRequest.setEmail(email);
			log.debug("retrieve consumer: " + retrieveConsumerRequest.toXML());

			GenesisClient client = new GenesisClient(configuration, retrieveConsumerRequest);
			client.execute();
			return client.getConsumer().getRequest().getConsumer().getConsumerId();
		} catch (Exception e) {
			log.debug("Problem getting emerchant consumer, this is most likely not a problem. " + email, e);
		}
		return null;
	}

	/**
	 * Resolves geoip codes to ISO codes for states/provinces for Canada
	 * @param internalStateCode
	 * @return
	 */
	private String stateISOResolver(final String internalStateCode) {
		switch (internalStateCode) {
			case "CA.01": return "AB"; //Alberta
			case "CA.02": return "BC"; //British Columbia
			case "CA.03" : return "MB"; //Manitoba
			case "CA.04" : return "NB"; //New Brunswick
			case "CA.05" : return "NL"; //Newfoundland
//			case "CA.05" : return "NL"; //Newfoundland and Labrador
			case "CA.13" : return "NT"; //Northwest Territories
			case "CA.07" : return "NS"; //Nova Scotia
			case "CA.14" : return "NU"; //Nunavut
			case "CA.08" : return "ON"; //Ontario
			case "CA.09" : return "PE"; //Prince Edward Island
			case "CA.10" : return "QC"; //Quebec
			case "CA.11" : return "SK"; //Saskatchewan
			case "CA.12" : return "YT"; //Yukon
			default : return internalStateCode;
		}
	}
}
