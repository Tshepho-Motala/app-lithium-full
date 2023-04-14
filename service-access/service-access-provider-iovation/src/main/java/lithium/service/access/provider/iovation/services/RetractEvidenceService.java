package lithium.service.access.provider.iovation.services;

import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lithium.service.access.provider.iovation.config.Config;
import lithium.service.access.provider.iovation.data.RetractEvidenceResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RetractEvidenceService {
	@Autowired AccessProviderIovationService service;
	@Autowired RestService restService;
	
	public RetractEvidenceResponse retractEvidence(String domainName, String accountCode, String deviceAlias, String evidenceType) throws Exception {
		log.info("retractEvidence (domainName: " + domainName + ", accountCode: " + accountCode + ", deviceAlias: "
			+ deviceAlias + ", evidenceType: " + evidenceType + ")");
		Map<String, String> properties = service.getProviderPropertiesMap("service-access-provider-iovation", domainName);
		String baseUrl = properties.get(Config.BASE_URL.property());
		String subscriberId = properties.get(Config.SUBSCRIBER_ID.property());
		if (baseUrl == null || baseUrl.isEmpty()) throw new Exception("Base url not set.");
		if (subscriberId == null || subscriberId.isEmpty()) throw new Exception("Subscriber id not set");
		final HttpEntity<?> entity = new HttpEntity<>(service.buildHeaders(properties));
		StringBuilder url = new StringBuilder();
		url.append(baseUrl);
		url.append("/fraud/v1/subs/");
		url.append(subscriberId);
		url.append("/evidence/retractions");
		url.append("?accountCode=" + URLEncoder.encode(accountCode, "UTF-8"));
		url.append("&deviceAlias=" + deviceAlias);
		url.append("&evidenceType=" + evidenceType);
		ResponseEntity<RetractEvidenceResponse> response = restService.restTemplate(domainName).exchange(url.toString(), HttpMethod.POST, entity, RetractEvidenceResponse.class);
		switch (response.getStatusCode().value()) {
			case HttpStatus.SC_NO_CONTENT: log.info("Evidence was successfully retracted."); break;
			case HttpStatus.SC_BAD_REQUEST: log.error("The request parameters were incorrect or insufficient."); break;
			case HttpStatus.SC_NOT_FOUND: log.warn("No evidence of the type was found for the account or device."); break;
		}
		return response.getBody();
	}
}
