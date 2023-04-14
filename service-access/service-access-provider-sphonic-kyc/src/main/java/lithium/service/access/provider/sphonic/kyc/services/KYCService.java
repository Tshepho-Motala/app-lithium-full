package lithium.service.access.provider.sphonic.kyc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.access.client.objects.EAuthorizationOutcome;
import lithium.service.access.client.objects.ExternalAuthorizationRequest;
import lithium.service.access.client.objects.ProviderAuthorizationResult;
import lithium.service.access.client.objects.RawAuthorizationData;
import lithium.service.access.provider.sphonic.kyc.config.Configuration;
import lithium.service.access.provider.sphonic.kyc.storage.repositories.AuthenticationRepository;
import lithium.service.access.provider.sphonic.schema.kyc.request.AdditionalDetails;
import lithium.service.access.provider.sphonic.schema.kyc.request.Address;
import lithium.service.access.provider.sphonic.schema.kyc.request.DateOfBirth;
import lithium.service.access.provider.sphonic.schema.kyc.request.Name;
import lithium.service.access.provider.sphonic.schema.kyc.request.RequestData;
import lithium.service.access.provider.sphonic.schema.kyc.request.RequestDetails;
import lithium.service.access.provider.sphonic.schema.kyc.request.SphonicKYCRequest;
import lithium.service.access.provider.sphonic.schema.kyc.response.SphonicKYCResponse;
import lithium.service.access.provider.sphonic.services.RestService;
import lithium.service.access.provider.sphonic.services.SphonicAuthenticationService;
import lithium.service.access.provider.sphonic.services.SphonicHTTPService;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserVerificationStatusUpdate;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

@Service
@Slf4j
public class KYCService {
	@Autowired private AuthenticationRepository authenticationRepository;
	@Autowired private ConfigurationService configurationService;
	@Autowired private SphonicAuthenticationService sphonicAuthenticationService;
	@Autowired private SphonicHTTPService sphonicHTTPService;
	@Autowired private UserApiInternalClientService userApiInternalClientService;
	@Autowired private VerificationResultService verificationResultService;
	@Autowired private ChangeLogService changeLogService;
	@Autowired private ObjectMapper mapper;
	@Autowired private RestService restService;

	private static final String KYC_RESULT_PASS = "pass";
	private static final String KYC_RESULT_FAIL = "fail";
	private static final String KYC_RESULT_REVIEW = "review";
	private static final String KYC_RESULT_ERROR = "error";

	public ProviderAuthorizationResult checkAuthorization(ExternalAuthorizationRequest request) {
		log.trace("Request " + request);

		ProviderAuthorizationResult result = ProviderAuthorizationResult.builder().build();

		try {
			Configuration configuration = configurationService.getDomainConfiguration(request.getDomainName());

			String accessToken = sphonicAuthenticationService.getAccessToken(authenticationRepository,
					request.getDomainName(), configuration.getAuthenticationUrl(), configuration.getUsername(),
					configuration.getPassword(), configuration.getConnectTimeout(), configuration.getConnectionRequestTimeout(), configuration.getSocketTimeout());

			User user = userApiInternalClientService.getUserByGuid(request.getUserGuid());
			log.trace("User " + user);

			if (user == null || user.getResidentialAddress() == null) {
				String errorMsg = "Cannot do KYC verification. KYC could not be retrieved from user service.";
				log.error(errorMsg + " [user.guid="+user.guid()+"]");
				result.setErrorMessage(errorMsg);
				result.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
				return result;
			}
			result = BooleanUtils.isTrue(configuration.getSkipOnAddressVerified()) && BooleanUtils.isTrue(user.getAddressVerified())
				? skipSphonicCall(user, result)
				: verify(user, configuration, accessToken, result);
		} catch (Exception | UserClientServiceFactoryException e) {
			log.error("checkAuthorization failed [request="+request+"] | " + e.getMessage(), e);
			//result.setAuthorisationOutcome(EAuthorizationOutcome.TIMEOUT);
			result.setAuthorisationOutcome(EAuthorizationOutcome.NOT_FILLED);
			result.setErrorMessage(e.getMessage());
		}
		return result;
	}
	private ProviderAuthorizationResult skipSphonicCall(User user, ProviderAuthorizationResult result) {
		log.info("User(" + user.getGuid() + ") address is already verified. Skip call to Sphonic KYC.");
		result.setAuthorisationOutcome(EAuthorizationOutcome.ACCEPT);
		result.setRawDataList(new ArrayList<>() {{ add(RawAuthorizationData.builder().build()); }});
		result.setData(new LinkedHashMap<>());
		try {
			changeLogService.registerChangesSystem("user", "edit", user.getId(),
				"User address is already verified. Skip call to Sphonic KYC.",
				null, null, Category.ACCOUNT, SubCategory.KYC, 80, user.getDomain().getName());
		} catch (Throwable e) {
			log.warn("Failed to add skip call to Sphonic KYC note for user : " + user.getGuid() + " due " + e.getMessage(), e);
		}
		return result;
	}
	private ProviderAuthorizationResult verify(User user, Configuration configuration, String accessToken, ProviderAuthorizationResult result) {
		Map<String, String> data = new LinkedHashMap<>();
		SphonicKYCRequest kycRequest = null;
		SphonicKYCResponse kycResponse = null;

		try {
			kycRequest = buildKYCRequest(user);

			log.info("KYC verification [request=" + mapper.writeValueAsString(kycRequest) + "; access_token=" + accessToken + "]");
			kycResponse = callExternalProvider(configuration, accessToken, kycRequest);
			log.info("KYC verification [response=" + mapper.writeValueAsString(kycResponse) + "]");

			switch (kycResponse.getFinalResult()) {
				case KYC_RESULT_PASS:
					result.setAuthorisationOutcome(EAuthorizationOutcome.ACCEPT);
					updateUserVerificationStatus(user, kycResponse);
					break;
				case KYC_RESULT_FAIL:
					result.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
					if (BooleanUtils.isTrue(configuration.getPartialVerification())) {
						updateUserVerificationStatus(user, kycResponse);
					}
					break;
				case KYC_RESULT_REVIEW:
					result.setAuthorisationOutcome(EAuthorizationOutcome.REVIEW);
					break;
				case KYC_RESULT_ERROR:
					result.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);//TODO: check TIMEOUT here
					break;
				default: {
					log.error("Unhandled result received from kyc workflow [result=" + kycResponse.getSphonicResponse().getData().getResult().getFinalResult() + "]");
					result.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
				}
			}
			verificationResultService.sendVerificationResult(kycResponse, user, kycResponse.getFinalResult().equalsIgnoreCase(KYC_RESULT_PASS));
		} catch (Exception e) {
			log.error("Failed to verify Sphonic KYC [kycRequest=" + kycRequest + "] | " + e.getMessage(), e);
			//we need
			addFailedVerificationAttemptNote(user, e);
			result.setAuthorisationOutcome(EAuthorizationOutcome.NOT_FILLED);
			result.setErrorMessage(e.getMessage());
		} finally {
			ArrayList<RawAuthorizationData> rawAuthorizationDataList = new ArrayList<>();
			RawAuthorizationData rawAuthorizationData = RawAuthorizationData.builder()
				.rawRequestToProvider((kycRequest != null) ? kycRequest.toString() : null)
				.rawResponseFromProvider((kycResponse != null) ? kycResponse.toString() : null)
				.build();
			rawAuthorizationDataList.add(rawAuthorizationData);
			result.setRawDataList(rawAuthorizationDataList);
			result.setData(data);
			return result;
		}
	}

	private void updateUserVerificationStatus(User user, SphonicKYCResponse kycResponse) throws UserClientServiceFactoryException, UserNotFoundException {
		VerificationStatus status = VerificationStatus.UNVERIFIED;
		if (BooleanUtils.isTrue(kycResponse.getAddressVerified()) && BooleanUtils.isTrue(kycResponse.getAgeVerified())) {
			status = VerificationStatus.EXTERNALLY_VERIFIED;
		} else if (BooleanUtils.isTrue(kycResponse.getAgeVerified())) {
			status = VerificationStatus.AGE_ONLY_VERIFIED;
		}

		UserVerificationStatusUpdate statusUpdate = UserVerificationStatusUpdate.builder()
			.addressVerified(kycResponse.getAddressVerified())
			.ageVerified(kycResponse.getAgeVerified())
			.statusId(status.getId())
			.comment("Sphonic KYC verification")
			.userGuid(user.guid())
			.build();

		userApiInternalClientService.editUserVerificationStatus(statusUpdate);
	}

	private SphonicKYCRequest buildKYCRequest(User user) {
		lithium.service.user.client.objects.Address residentalAddress = user.getResidentialAddress();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		SimpleDateFormat dobFormatter = new SimpleDateFormat("yyyy-MM-dd");

		return SphonicKYCRequest.builder()
				.requestDetails(RequestDetails.builder()
					.requestId(UUID.randomUUID().toString())
					.requestDateTime(dateFormat.format(new Date()))
					.build())
				.requestData(RequestData.builder()
					.dateOfBirth(DateOfBirth.builder()
						.dataOfBirth(dobFormatter.format(user.getDateOfBirth().toDate()))
						.build())
					.name(Name.builder()
						.firstNames(user.getFirstName())
						.surname(user.getLastName())
						.build())
					.address(Address.builder()
						.address1(residentalAddress.getAddressLine1())
						.address2(residentalAddress.getAddressLine2())
						.city(residentalAddress.getCity())
						.county(residentalAddress.getCountry())
						.postalCode(residentalAddress.getPostalCode())
						.build())
					.additionalDetails(AdditionalDetails.builder()
						.telephoneNumber(user.getCellphoneNumber())
						.gender(user.getGender())
						.testRequest(String.valueOf(BooleanUtils.isTrue(user.getTestAccount())).toUpperCase())
						.build())
					.build())
			.build();
	}

	private SphonicKYCResponse callExternalProvider(Configuration configuration, String accessToken, SphonicKYCRequest request)
			throws Status500InternalServerErrorException {
		String url = configuration.getKycUrl() + "/" + configuration.getMerchantId() + "/"
				+ configuration.getKycWorkflowName();
		sphonicHTTPService.setRest(restService.
						restTemplate(
								configuration.getConnectTimeout(),
								configuration.getConnectionRequestTimeout(),
								configuration.getSocketTimeout()));
		return sphonicHTTPService.postForEntity(accessToken, url, request, SphonicKYCResponse.class);
	}

	private void addFailedVerificationAttemptNote(User user, Exception exception) {
		try {
			String comment = "Sphonic KYC verification failed. Exception: " + exception.getMessage();
			changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(),
				User.SYSTEM_GUID, null, comment, null, null,
				Category.ACCESS, SubCategory.KYC, 80, user.getDomain().getName());
		} catch (Exception ex) {
			log.error("Failed to add Sphonic kyc verification note" + ex.getMessage() + "For user:" + user.getGuid());
		}
	}
}
