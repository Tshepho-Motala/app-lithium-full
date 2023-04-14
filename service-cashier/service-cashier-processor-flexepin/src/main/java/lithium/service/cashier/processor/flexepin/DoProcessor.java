package lithium.service.cashier.processor.flexepin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.service.cashier.client.external.DoProcessorLimitsClients;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorRequestUser;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.Limits;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.flexepin.data.TransactionResponse;
import lithium.service.cashier.processor.flexepin.exception.HashCalculationException;
import lithium.service.cashier.processor.flexepin.util.HashMacCalculator;
import lithium.service.cashier.processor.flexepin.util.NonceGenerator;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessor extends DoProcessorAdapter {

	protected DoProcessorRequestUser user;

	@Autowired
	private LithiumServiceClientFactory services;

	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response,
			DoProcessorContext context, RestTemplate rest) throws Exception {

		log.debug(request.toString());
		final String flexepin = request.stageInputData(1, "pin").trim();
		final String baseUrl = request.getProperty("baseUrl");
		final String ApiSecret = request.getProperty("APISecret");
		final String ApiKey = request.getProperty("APIKey");
		final String MechantId = request.getProperty("scpMechantId");
		final Long trid = request.getTransactionId();
		this.user = request.getUser();
		 
		TransactionResponse validatePin = this.doRequest(Constants.GET_METHOD,
				Constants.VALIDATE_URI + flexepin + Constants.FORWARD_SLASH + MechantId, null, ApiSecret, ApiKey,
				baseUrl, request, response, trid);
		proccessResponse(response, request, validatePin);
		if (!validatePin.getResult().equals(Constants.REQUEST_SUCCESS)) {
			return DoProcessorResponseStatus.DECLINED;
		}
		BigDecimal cost = new BigDecimal(validatePin.getCost().doubleValue());
		log.info("Flexepin cost for : " + flexepin + " is " + cost.toPlainString());
 		response.setOutputData(1, "amount", cost.toPlainString());
		response.setOutputData(1, "amountCents", cost.movePointRight(2).toPlainString());
		response.setAmount(cost);
		response.setAmountCentsReceived(cost.movePointRight(2).intValue());
		if(limitsValid(context.getRequest().getUser().getGuid() ,cost.toPlainString()) != true) {
			proccessResponse(response, request, validatePin);
			response.setMessage("The provided amount exceeds the maximum limits or is below the minimum allowed amount.");
			return DoProcessorResponseStatus.DECLINED;
		}
		 context.getRequest().setStage(2);
		 return DoProcessorResponseStatus.NEXTSTAGE;
		
	}
	/**
	 * Method accepts a decimal string value and determined if the value is within the allowed transaction cost bounds
	 * @param guid
	 * @param redeemableAmount
	 * @return true if limits are satisfied 
	 */
	private boolean limitsValid(String guid , String redeemableAmount) {

		try {
			Limits myLimits = getUserProcessorLimits().get().getUserDomainLimits(guid).getData();
			Long maxAmount = Long.valueOf(myLimits.getMaxAmount());
			Long minAmount = Long.valueOf(myLimits.getMinAmount());
			try {
				Long amountCents = new BigDecimal(redeemableAmount).movePointRight(2).longValue();
				if ( (minAmount != null && amountCents < minAmount) || (maxAmount != null && amountCents > maxAmount) ) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}catch(Exception limitsNotFound) {
			log.debug("An error accured trying to retreive limits for user" +guid+" exception:" +limitsNotFound.getMessage());
		}
		return true;
	}

	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response,
			DoProcessorContext context, RestTemplate rest) throws Exception {

		final String flexepin = request.stageInputData(1, "pin");
		final String baseUrl = request.getProperty("baseUrl");
		final String ApiSecret = request.getProperty("APISecret");
		final String ApiKey = request.getProperty("APIKey");
		final String MechantId = request.getProperty("scpMechantId");
		final Long trid = request.getTransactionId();
		this.user = request.getUser();

		TransactionResponse redeemPin = this.doRequest(Constants.PUT_METHOD,
				Constants.REEDEM_URI + flexepin + Constants.FORWARD_SLASH + MechantId, null, ApiSecret, ApiKey, baseUrl,
				request, response, trid);

		proccessResponse(response, request, redeemPin);	
		if (!redeemPin.getResult().equals(Constants.REQUEST_SUCCESS)) {
			return DoProcessorResponseStatus.DECLINED;
		}

		return DoProcessorResponseStatus.SUCCESS; 
	}

	public TransactionResponse doRequest(String requestMethod, String requestUri, String body, String secret,
			String siteKey, String apiUrl, DoProcessorRequest request, DoProcessorResponse response, Long trid) {

		String siteUrl;
		String nonce = NonceGenerator.generateRandomString(4);
		TransactionResponse transactionResponse = TransactionResponse.builder().build();

		try {
			String baseUrl = request.getProperty("baseUrl");

			if (!requestUri.contentEquals("status")) {
				siteUrl = baseUrl + Constants.FORWARD_SLASH + requestUri + Constants.FORWARD_SLASH + trid;
			} else {
				siteUrl = baseUrl + Constants.FORWARD_SLASH + requestUri;
			}
			StringBuilder uri = new StringBuilder();
			String json = body == null ? "" : body;
			uri.append(requestMethod + Constants.NEW_LINE);
			uri.append(Constants.FORWARD_SLASH + requestUri + Constants.FORWARD_SLASH + trid + Constants.NEW_LINE);
			uri.append(nonce + Constants.NEW_LINE);
			uri.append(json);

			String signature = "";
			try {
				signature = HashMacCalculator.hex64sha256(uri.toString(), secret);
			} catch (Exception e) {
				throw new HashCalculationException("Hash could not be calculated ");
			}

			HttpURLConnection conn = (HttpURLConnection) (new URL(siteUrl)).openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod(requestMethod);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Authentication", "HMAC " + siteKey + ":" + signature + ":" + nonce);
			conn.setRequestProperty("charset", "ascii");
			conn.setRequestProperty("Content-Length", String.valueOf(json.length()));
			conn.setUseCaches(true);

			if (conn.getResponseCode() != 200) {

				throw new RuntimeException(
						"Failed : HTTP error code : " + conn.getResponseCode() + "\n" + conn.getResponseMessage());
			} else {
				log.debug("results" + conn.getResponseCode() + " " + conn.getResponseMessage());

				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				String serverResponse;
				log.debug("Output from Server .... ");
				while ((serverResponse = br.readLine()) != null) {
					log.debug(serverResponse.length() + "");
					ObjectMapper objectMapper = new ObjectMapper();
					if (serverResponse != null) {

						try {
							transactionResponse = objectMapper.readValue(serverResponse, TransactionResponse.class);
							log.debug(serverResponse);
							log.debug(transactionResponse.toString());

						} catch (com.fasterxml.jackson.databind.JsonMappingException errorResponse) {
 						}
					}
				}
			}
			conn.disconnect();
		} catch (Exception e) {
 		}
		return transactionResponse;
	}

	private void proccessResponse(DoProcessorResponse doProccessorResponse, DoProcessorRequest request,
			TransactionResponse transactionResponse) {

		doProccessorResponse.setTransactionId(Long.valueOf(transactionResponse.getTransaction_id()));
		doProccessorResponse.setProcessorReference(transactionResponse.getTrans_no());
		doProccessorResponse.setOutputData(1, "TransatctionResponse", transactionResponse.toString());
		doProccessorResponse.setOutputData(1, "TransactionId", transactionResponse.getTransaction_id());
		doProccessorResponse.setOutputData(1, "Result", transactionResponse.getResult());
		doProccessorResponse.setOutputData(1, "Result message", transactionResponse.getResult_description());
		doProccessorResponse.setMessage(transactionResponse.getResult_description());
		doProccessorResponse.setStatus(
				transactionResponse.getResult().equals(Constants.REQUEST_SUCCESS) ? DoProcessorResponseStatus.SUCCESS
						: DoProcessorResponseStatus.FATALERROR);

		if (transactionResponse.getResult().equals(Constants.REQUEST_SUCCESS)) {
 			doProccessorResponse.setOutputData(1, "Cost", String.valueOf(transactionResponse.getCost()));
 			doProccessorResponse.setOutputData(1, "Currency", transactionResponse.getCurrency());
			doProccessorResponse.setOutputData(1, "Serial", transactionResponse.getSerial());
			doProccessorResponse.setOutputData(1, "Description", transactionResponse.getDescription());
			doProccessorResponse.setOutputData(1, "Ean", transactionResponse.getEan());
			doProccessorResponse.setOutputData(1, "Status", transactionResponse.getStatus());
		    doProccessorResponse.setOutputData(1, "Residual value", transactionResponse.getResidual_value());
			//doProccessorResponse.setAmountCentsReceived(transactionResponse.getValue());
			doProccessorResponse.setUserGuid(this.user.getGuid());
			doProccessorResponse.setProcessorUserId(this.user.getGuid());

		}

		buildRawResponseLog(doProccessorResponse, request);
	}
	
	private Optional<DoProcessorLimitsClients> getUserProcessorLimits() {
		return getClient(DoProcessorLimitsClients.class, "service-cashier");
	}
	
	private <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;
		
		try {
			clientInstance = services.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return Optional.ofNullable(clientInstance);
		
	}

}
