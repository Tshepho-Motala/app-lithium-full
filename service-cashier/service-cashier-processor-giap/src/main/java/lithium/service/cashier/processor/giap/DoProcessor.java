package lithium.service.cashier.processor.giap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import com.google.api.services.androidpublisher.model.ProductPurchase;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessor extends DoProcessorAdapter {
	private final Set<String> scopes = Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER);
	
	@Autowired LithiumConfigurationProperties config;
	
	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		String purchaseToken = request.stageInputData(1).get("purchaseToken");
		if (purchaseToken != null) {
			if (purchaseToken.equals("CANCELLED")) {
				response.setStatus(DoProcessorResponseStatus.PLAYER_CANCEL);
				response.setMessage("User Cancelled Transaction.");
				return response.getStatus();
			} else if (request.stageInputData(1).get("productGuid") != null) {
				response.setStatus(DoProcessorResponseStatus.NEXTSTAGE);
				return response.getStatus();
			}
			response.setStatus(DoProcessorResponseStatus.INPUTERROR);
		}
		response.setStatus(DoProcessorResponseStatus.INPUTERROR);
		return response.getStatus();
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		Map<String, String> properties = request.getProperties();
		
		String appName = properties.get("appName"); //MegaVegas Social Demo
		String packageName = properties.get("packageName"); // com.playsafesa.megavegas.app.social
		
		String purchaseToken = request.stageInputData(1, "purchaseToken");
		String productId = request.stageInputData(1, "productGuid");
		
		log.info("Token :: "+purchaseToken);
		log.info("Productid :: "+productId);
		log.info("packageName :: "+packageName);
		
		String lastKnownIP = request.getUser().getLastKnownIP();
		int indexOf = lastKnownIP.indexOf(",");
		if (indexOf != -1) lastKnownIP = lastKnownIP.substring(0, indexOf);
		
		NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		
		String pk = properties.getOrDefault("private_key", "");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("type", properties.getOrDefault("type", ""));
		map.put("project_id", properties.getOrDefault("project_id", ""));
		map.put("private_key_id", properties.getOrDefault("private_key_id", ""));
		map.put("private_key", pk.replaceAll("\\\\n", "\n"));
		map.put("client_email", properties.getOrDefault("client_email", ""));
		map.put("client_id", properties.getOrDefault("client_id", ""));
		map.put("auth_uri", properties.getOrDefault("auth_uri", ""));
		map.put("token_uri", properties.getOrDefault("token_uri", ""));
		map.put("auth_provider_x509_cert_url", properties.getOrDefault("auth_provider_x509_cert_url", ""));
		map.put("client_x509_cert_url", properties.getOrDefault("client_x509_cert_url", ""));
		JSONObject jo = new JSONObject(map);
		String json = jo.toString(2);
		log.info(json);
		InputStream is = new ByteArrayInputStream(json.getBytes());
		
//		GoogleCredential credential = GoogleCredential.fromStream(is).createScoped(scopes);
		GoogleCredentials credentials = GoogleCredentials.fromStream(is).createScoped(scopes);
//		credentials.refreshIfExpired();

		HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
		
		AndroidPublisher publisher = new AndroidPublisher.Builder(httpTransport, jsonFactory, requestInitializer).setApplicationName(appName).build();
		ProductPurchase product = null;
		try {
			product = publisher.purchases().products().get(packageName, productId, purchaseToken).execute();
			buildRawResponseLog(response, product.toPrettyString());
		} catch (GoogleJsonResponseException e) {
			response.setMessage(e.getDetails().getMessage());
			buildRawResponseLog(response, e.getDetails());
			return DoProcessorResponseStatus.DECLINED;
		}
		Integer purchaseState = product.getPurchaseState(); // 0 - Purchased / 1 - Canceled
		Integer consumptionState = product.getConsumptionState(); // 0 - Yet to be consumed / 1 - Consumed
		
		response.setProcessorReference(product.getOrderId());
		response.setOutputData(1, "purchaseType", ""+product.getPurchaseType());
		response.setOutputData(1, "purchaseState", ""+purchaseState);
		response.setOutputData(1, "purchaseTimeMillis", ""+product.getPurchaseTimeMillis());
		response.setOutputData(1, "consumptionState", ""+consumptionState);
		response.setOutputData(1, "developerPayload", ""+product.getDeveloperPayload());
		
		if ((purchaseState == 0) && (consumptionState == 1)) {
			response.setStatus(DoProcessorResponseStatus.SUCCESS);
//			response.setStatus(DoProcessorResponseStatus.NOOP);
		} else {
			response.setStatus(DoProcessorResponseStatus.NOOP);
		}
		
		return response.getStatus();
	}
}