package lithium.service.access.provider.iovation.services;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lithium.service.access.provider.iovation.config.Config;
import lithium.service.access.provider.iovation.data.AddEvidence;
import lithium.service.access.provider.iovation.data.AddEvidenceResponse;
import lithium.service.access.provider.iovation.data.AppliedTo;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AddEvidenceService {
	@Autowired AccessProviderIovationService service;
	@Autowired RestService restService;
	
	public AddEvidenceResponse addEvidence(String domainName, AddEvidence request) throws Exception {
		log.info("addEvidence (" + request.toString() + ")");
		Map<String, String> properties = service.getProviderPropertiesMap("service-access-provider-iovation", domainName);
		String baseUrl = properties.get(Config.BASE_URL.property());
		String subscriberId = properties.get(Config.SUBSCRIBER_ID.property());
		if (baseUrl == null || baseUrl.isEmpty()) throw new Exception("Base url not set.");
		if (subscriberId == null || subscriberId.isEmpty()) throw new Exception("Subscriber id not set");
		final HttpEntity<AddEvidence> entity = new HttpEntity<>(request, service.buildHeaders(properties));
		String url = baseUrl + "/fraud/v1/subs/" + subscriberId + "/evidence/";
		ResponseEntity<AddEvidenceResponse> response = restService.restTemplate(domainName).exchange(url, HttpMethod.POST, entity, AddEvidenceResponse.class);
		switch (response.getStatusCode().value()) {
			case HttpStatus.SC_CREATED: log.info("The evidence was successfully created."); break;
			case HttpStatus.SC_BAD_REQUEST: log.error("Bad request."); break;
			case HttpStatus.SC_NOT_FOUND: log.warn("The account or device specified by the appliedTo entity was not found in the system."); break;
			case HttpStatus.SC_CONFLICT: log.warn("The evidence type already exists."); break;
		}
		return response.getBody();
	}
	
	public AddEvidenceResponse addEvidence(
		String domainName,
		String evidenceType,
		String comment,
		String type,
		String accountCode,
		String deviceAlias
	) throws Exception {
		return addEvidence(domainName,
			AddEvidence.builder()
			.evidenceType(evidenceType)
			.comment(comment)
			.appliedTo(
				AppliedTo.builder()
				.type(type)
				.accountCode(accountCode)
				.deviceAlias(deviceAlias)
				.build()
			)
			.build()
		);
	}
}
