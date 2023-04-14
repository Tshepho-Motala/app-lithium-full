package lithium.service.access.provider.iovation.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.Response;
import lithium.service.access.client.ExternalAuthorizationClient;
import lithium.service.access.client.exceptions.Status513InvalidDomainConfigurationException;
import lithium.service.access.client.objects.EAuthorizationOutcome;
import lithium.service.access.client.objects.ExternalAuthorizationRequest;
import lithium.service.access.client.objects.ProviderAuthorizationResult;
import lithium.service.access.client.objects.RawAuthorizationData;
import lithium.service.access.provider.iovation.data.CheckTransactionDetails;
import lithium.service.access.provider.iovation.data.CheckTransactionDetailsResponse;
import lithium.service.access.provider.iovation.data.DeviceRegistrationRequest;
import lithium.service.access.provider.iovation.data.RegistrationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/system")
@Slf4j
public class ExternalAuthorizationWrapperController implements ExternalAuthorizationClient {
	@Autowired IovationController iovationController;
	@Autowired
	IovationDeviceManagerController deviceManagerController;

	@Override
	@RequestMapping(path="/checkAuthorization")
	public Response<ProviderAuthorizationResult> checkAuthorization(
			@RequestBody ExternalAuthorizationRequest externalAuthorizationRequest) throws Status513InvalidDomainConfigurationException {

		CheckTransactionDetails checkTransactionDetails = CheckTransactionDetails.builder()
				.accountCode(externalAuthorizationRequest.getUserGuid())
				.blackbox(externalAuthorizationRequest.getDeviceId())
				.type(externalAuthorizationRequest.getRuleName())
				.statedIp(externalAuthorizationRequest.getIp())
				//.transactionInsight() (this can be added if things get passed from caller, currently nothing is being sent though
				.build();
		Response<CheckTransactionDetailsResponse> checkTransactionDetailsResponse = iovationController.checkTransactionDetails(
				externalAuthorizationRequest.getDomainName(),
				checkTransactionDetails);
		ArrayList<RawAuthorizationData> rawDataList = new ArrayList<>();
		try {
			rawDataList.add(
			RawAuthorizationData.builder()
					.rawRequestToProvider(new ObjectMapper().writeValueAsString(checkTransactionDetails))
					.rawResponseFromProvider(new ObjectMapper().writeValueAsString(checkTransactionDetailsResponse))
					.build());
		} catch (JsonProcessingException e) {
			log.warn("Unable to map raw transaction data for auth request: " + checkTransactionDetails, e);
			rawDataList.add(RawAuthorizationData.builder().build());
		}
		ProviderAuthorizationResult authorizationResult = null;
		if (checkTransactionDetailsResponse.isSuccessful()) {
			CheckTransactionDetailsResponse responseData = checkTransactionDetailsResponse.getData();
			if (responseData.getResult().contentEquals("A")) { //allow
				try {
					processDeviceRegistration(externalAuthorizationRequest.getDomainName(), checkTransactionDetails, responseData);
				} catch (Exception ex) {
					log.warn("Problem performing device registration. " + checkTransactionDetails + " " + responseData, ex);
				}
				authorizationResult = ProviderAuthorizationResult.builder()
						.authorisationOutcome(EAuthorizationOutcome.ACCEPT)
						.build();
			} else if (responseData.getResult().contentEquals("R")) { //review
					try {
						processDeviceRegistration(externalAuthorizationRequest.getDomainName(), checkTransactionDetails, responseData);
					} catch (Exception ex) {
						log.warn("Problem performing device registration. " + checkTransactionDetails + " " + responseData, ex);
					}
					authorizationResult = ProviderAuthorizationResult.builder()
							.authorisationOutcome(EAuthorizationOutcome.REVIEW)
							.build();
			} else { // deny
				authorizationResult = ProviderAuthorizationResult.builder()
						.authorisationOutcome(EAuthorizationOutcome.REJECT)
						.errorMessage(responseData.getReason())
						.build();
			}
		} else {
			log.error("Problem in remote rule response: " + checkTransactionDetailsResponse);
			authorizationResult = ProviderAuthorizationResult.builder()
					.authorisationOutcome(EAuthorizationOutcome.TIMEOUT)
					.errorMessage("Error from remote caller")
					.build();
		}
		authorizationResult.setRawDataList(rawDataList);
		return Response.<ProviderAuthorizationResult>builder()
				.data(authorizationResult)
				.status(Response.Status.OK)
				.build();
	}

	private void processDeviceRegistration(String domainName, CheckTransactionDetails checkTransactionDetails, CheckTransactionDetailsResponse responseData) throws Exception {
		RegistrationResult registrationResult = responseData.getDetails().getDevice().getRegistrationResult();
		DeviceRegistrationRequest request = DeviceRegistrationRequest.builder()
				.blackBox(checkTransactionDetails.getBlackbox())
				.userIPAddress(checkTransactionDetails.getStatedIp())
				.userAccountCode(checkTransactionDetails.getAccountCode())
				.build();
		log.debug("Details passed to device reg for evaluation: " + checkTransactionDetails);
		if (registrationResult != null) {
			String matchStatus = registrationResult.getMatchStatus();
			if (matchStatus.equals("NONE_REGISTERED") || matchStatus.equals("NO_MATCH")) {
				deviceManagerController.registerDevice(domainName, request);
			}
		} else {
			deviceManagerController.registerDevice(domainName, request);
		}
	}
}
