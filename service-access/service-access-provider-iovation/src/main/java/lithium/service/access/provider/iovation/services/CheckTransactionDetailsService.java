package lithium.service.access.provider.iovation.services;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lithium.service.access.provider.iovation.config.Config;
import lithium.service.access.provider.iovation.data.CheckTransactionDetails;
import lithium.service.access.provider.iovation.data.CheckTransactionDetailsResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CheckTransactionDetailsService {
	@Autowired AccessProviderIovationService service;
	@Autowired RestService restService;
	
	public CheckTransactionDetailsResponse checkTransactionDetails(String domainName, CheckTransactionDetails request) throws Exception {
		log.info("checkTransactionDetails (" + request.toString() + ")");
		Map<String, String> properties = service.getProviderPropertiesMap("service-access-provider-iovation", domainName);
		String baseUrl = properties.get(Config.BASE_URL.property());
		String subscriberId = properties.get(Config.SUBSCRIBER_ID.property());
		if (baseUrl == null || baseUrl.isEmpty()) throw new Exception("Base url not set.");
		if (subscriberId == null || subscriberId.isEmpty()) throw new Exception("Subscriber id not set");
		final HttpEntity<CheckTransactionDetails> entity = new HttpEntity<>(request, service.buildHeaders(properties));
		String url = baseUrl + "/fraud/v1/subs/" + subscriberId + "/checks";
		ResponseEntity<CheckTransactionDetailsResponse> response = restService.restTemplate(domainName).exchange(
			url,
			HttpMethod.POST,
			entity,
			CheckTransactionDetailsResponse.class
		);
		switch (response.getStatusCode().value()) {
			case HttpStatus.SC_CREATED: log.info("Check successfully processed."); break;
			case HttpStatus.SC_BAD_REQUEST: log.error("There was a validation error with the request."); break;
			case HttpStatus.SC_INTERNAL_SERVER_ERROR: log.error("There was a problem processing the request"); break;
		}
		return response.getBody();
	}
	
//	public CheckTransactionDetailsResponse checkTransactionDetails(
//		String domainName,
//		String accountCode,
//		String blackbox,
//		Map<String, Object> transactionInsight,
//		String type
//	) throws Exception {
//		return checkTransactionDetails(domainName,
//			CheckTransactionDetails.builder()
//			.accountCode(accountCode)
//			.blackbox(blackbox)
//			.transactionInsight(transactionInsight)
//			.type(type)
//			.build()
//		);
//	}
}
