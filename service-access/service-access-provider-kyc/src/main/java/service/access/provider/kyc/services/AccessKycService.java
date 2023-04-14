package service.access.provider.kyc.services;

import lithium.service.access.client.objects.EAuthorizationOutcome;
import lithium.service.access.client.objects.ExternalAuthorizationRequest;
import lithium.service.access.client.objects.ProviderAuthorizationResult;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.kyc.client.KycResultsClient;
import lithium.service.kyc.provider.objects.VerificationMethodType;
import lithium.service.kyc.provider.objects.VerifyParam;
import lithium.service.kyc.provider.objects.VerifyRequest;
import lithium.service.kyc.schema.VerificationStatusResponse;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lithium.service.kyc.provider.config.VerifyIdParameters.GUID_PARAM;
import static lithium.service.kyc.provider.config.VerifyIdParameters.ID_NUMBER_PARAM;
import static lithium.service.kyc.provider.config.VerifyIdParameters.LAST_NAME_PARAM;

@Service
@Slf4j
public class AccessKycService {

	@Autowired
	LithiumServiceClientFactory services;
	@Autowired
	UserApiInternalClientService userService;

	public ProviderAuthorizationResult checkAuthorization(ExternalAuthorizationRequest request) {
		ProviderAuthorizationResult providerAuthorizationResult = new ProviderAuthorizationResult();
		providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.ACCEPT); // Default init just to be safe
		Map<String, String> data = new HashMap<>();
		try {
			User user = userService.getUserByGuid(request.getUserGuid());
			KycResultsClient kycResultsClient = services.target(KycResultsClient.class, true);
			ResponseEntity<VerificationStatusResponse> result = kycResultsClient.verify(buildVerifyRequest(user));
			data.put("Kyc verification result", result.getBody().getVerificationStatus());
		} catch (Exception | UserClientServiceFactoryException e) {
			data.put("Kyc verification result", "false");
			log.error("Error during verification user=[" + request.getUserGuid() + "], message: " + e.getMessage(), e);
		}
		providerAuthorizationResult.setData(data);
		log.debug("AuthorizationResult : " + providerAuthorizationResult);
		return providerAuthorizationResult;
	}

	private VerifyRequest buildVerifyRequest(User user) throws Exception {

		VerifyRequest verifyRequest = new VerifyRequest();
		verifyRequest.setVerificationMethodName(VerificationMethodType.METHOD_NIN_PHONE_NUMBER);

		VerifyParam identificationNumber = new VerifyParam();
		identificationNumber.setKey(ID_NUMBER_PARAM);
		identificationNumber.setValue(user.getCellphoneNumber());

		VerifyParam lastName = new VerifyParam();
		lastName.setKey(LAST_NAME_PARAM);
		lastName.setValue(user.getLastName());

		VerifyParam guid = new VerifyParam();
		guid.setKey(GUID_PARAM);
		guid.setValue(user.guid());

		List<VerifyParam> fields = new ArrayList<>();
		fields.add(identificationNumber);
		fields.add(lastName);
		fields.add(guid);

		verifyRequest.setFields(fields);

		return verifyRequest;
	}
}