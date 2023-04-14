package lithium.service.access.provider.sphonic.cruks.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.access.client.objects.EAuthorizationOutcome;
import lithium.service.access.client.objects.ExternalAuthorizationRequest;
import lithium.service.access.client.objects.ProviderAuthorizationResult;
import lithium.service.access.client.objects.RawAuthorizationData;
import lithium.service.access.provider.sphonic.cruks.config.Configuration;
import lithium.service.access.provider.sphonic.cruks.storage.entities.FailedAttempt;
import lithium.service.access.provider.sphonic.cruks.storage.repositories.AuthenticationRepository;
import lithium.service.access.provider.sphonic.schema.RequestDetails;
import lithium.service.access.provider.sphonic.schema.cruks.login.CRUKSLoginRequest;
import lithium.service.access.provider.sphonic.schema.cruks.login.CRUKSLoginResponse;
import lithium.service.access.provider.sphonic.schema.cruks.registration.AdditionalDetails;
import lithium.service.access.provider.sphonic.schema.cruks.registration.CRUKSRegistrationRequest;
import lithium.service.access.provider.sphonic.schema.cruks.registration.CRUKSRegistrationResponse;
import lithium.service.access.provider.sphonic.schema.cruks.registration.DateOfBirth;
import lithium.service.access.provider.sphonic.schema.cruks.registration.Name;
import lithium.service.access.provider.sphonic.schema.cruks.registration.RequestData;
import lithium.service.access.provider.sphonic.services.RestService;
import lithium.service.access.provider.sphonic.services.SphonicAuthenticationService;
import lithium.service.access.provider.sphonic.services.SphonicHTTPService;
import lithium.service.access.provider.sphonic.util.SphonicDataUtil;
import lithium.service.access.provider.sphonic.util.SphonicUserRevisionLabelValueUtil;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.objects.Label;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CRUKSService {
	@Autowired private AuthenticationRepository authenticationRepository;
	@Autowired private ChangeLogService changeLogService;
	@Autowired private ConfigurationService configurationService;
	@Autowired private CRUKSResultService cruksResultService;
	@Autowired private FailedAttemptService failedAttemptService;
	@Autowired private SphonicAuthenticationService sphonicAuthenticationService;
	@Autowired private SphonicHTTPService sphonicHTTPService;
	@Autowired private UserApiInternalClientService userApiInternalClientService;
	@Autowired private RestService restService;

	private static final String CRUKS_DOB_FORMAT = "dd/MM/yyyy";
	private static final String CRUKS_MODE_TEST_PASS = "test_pass";
	private static final String CRUKS_MODE_TEST_FAIL = "test_fail";

	public ProviderAuthorizationResult checkAuthorization(ExternalAuthorizationRequest request) {
		log.trace("Request " + request);

		boolean registration = false;

		// If request.getUserGuid() is null, then this is called on a registration.
		// We need to have either bsn in the additionalDetails map, or countryCodeOfBirth in the playerBasic object.
		if (request.getUserGuid() == null) {
			registration = true;
			validateRequiredFields(request, false, true);
		} else {
			// It is a login. We need to retrieve the cruksId from the user.
			validateRequiredFields(request, false, false);
		}

		ProviderAuthorizationResult result = ProviderAuthorizationResult.builder().build();
		Map<String, String> data = new LinkedHashMap<>();

		PlayerBasic player = request.getPlayerBasic();
		log.trace("Player " + player);
		User user = null;

		CRUKSRegistrationResponse cruksRegistrationResponse ;
		CRUKSLoginResponse cruksLoginResponse;

		ArrayList<RawAuthorizationData> rawAuthorizationDataList = new ArrayList<>();

		try {
			Configuration configuration = configurationService.getDomainConfiguration(request.getDomainName());

			String accessToken = sphonicAuthenticationService.getAccessToken(authenticationRepository,
					request.getDomainName(), configuration.getAuthenticationUrl(), configuration.getUsername(),
					configuration.getPassword(), configuration.getConnectTimeout(), configuration.getConnectionRequestTimeout(), configuration.getSocketTimeout());

			String uniqueReference = getUniqueReference(configuration);

			String birthPlace = null;
			if (player.getPlaceOfBirth() != null && !player.getPlaceOfBirth().trim().isEmpty()) {
				birthPlace = player.getPlaceOfBirth();
			}
			String dob = getDobFormatted(player.getDobDay(), player.getDobMonth(), player.getDobYear());

			String cruksId = null;
			String cruksResult;

			if (registration) {
				String bsn = (request.getAdditionalData() != null)
						? request.getAdditionalData().get("bsn")
						: null;

				cruksRegistrationResponse = cruksRegistration(configuration, accessToken, player.getFirstName(),
						player.getLastName(), player.getLastNamePrefix(), dob, bsn, birthPlace, uniqueReference,
						rawAuthorizationDataList);

				cruksResult = cruksRegistrationResponse.getSphonicResponse().getData().getResult().toUpperCase();
				cruksId = cruksRegistrationResponse.getSphonicResponse().getData().getVendorData()
						.getResponseCRUKSCode();

				populateResponseDataMap(cruksRegistrationResponse, cruksId, data);
			} else {
				user = userApiInternalClientService.getUserByGuid(request.getUserGuid());
				log.trace("User " + user);

				boolean failFast = false;

				if (user.getCurrent() == null ||
						user.getCurrent().getLabelValueList() == null ||
						user.getCurrent().getLabelValueList().isEmpty()) {
					failFast = true;
				} else {
					cruksId = SphonicUserRevisionLabelValueUtil.getValueFromUserRevisionLabelValues(
							user.getCurrent().getLabelValueList(), Label.CRUKS_ID);
					if (cruksId == null || cruksId.isEmpty()) {
						failFast = true;
					}
				}

				if (failFast) {
					String errorMsg = "Cannot do CRUKS login. CRUKS id could not be retrieved from user service.";
					log.error(errorMsg + " [user.guid="+user.guid()+"]");
					result.setErrorMessage(errorMsg);
					result.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
					return result;
				}

				cruksLoginResponse = cruksLogin(configuration, accessToken, cruksId, uniqueReference,
						rawAuthorizationDataList);

				cruksResult = cruksLoginResponse.getSphonicResponse().getData().getResult().toUpperCase();

				populateResponseDataMap(cruksLoginResponse, cruksId, data);
			}

			updateResult(result, cruksResult.replaceAll("\\s", "").toUpperCase(), request, user);

			if (!registration) cruksResultService.handle(user, cruksId, cruksResult, false);
		} catch (Exception e) {
			log.error("checkAuthorization failed [request="+request+"] | " + e.getMessage(), e);
			result.setAuthorisationOutcome(EAuthorizationOutcome.TIMEOUT);
			result.setErrorMessage(e.getMessage());
			if (registration) {
				String logDetail = " [Outcome=" + EAuthorizationOutcome.TIMEOUT + ", message=" + e.getMessage();
				log.warn("CRUKS service cannot be reached." + logDetail, e);
			}
			if (!registration) {
				retryRequest(request, user, "Failed to perform HTTP request to sphonic");
			}
		} finally {
			result.setRawDataList(rawAuthorizationDataList);
			result.setData(data);
			return result;
		}
	}

	private void retryRequest(ExternalAuthorizationRequest request, User user, String message) {
		FailedAttempt failedAttempt = failedAttemptService.createOrUpdate(request.getDomainName(),
				user.guid(), message);
		String logDetail = " [attempt.id=" + failedAttempt.getId() + ", user.guid=" + failedAttempt.getUser().getGuid()
				+ ", lastAttemptedAt=" + failedAttempt.getLastAttemptedAt()
				+ ", totalAttempts=" + failedAttempt.getTotalAttempts() + "]";

		log.trace("Persisted failed attempt " + failedAttempt);
		log.warn(message + logDetail);
		String comments = "CRUKS service cannot be reached. User has been logged in. Will continue to call CRUKS"
				+ " service.";
		changeLogService.registerChangesForNotesWithFullNameAndDomain("user.exclusion", "edit", user.getId(),
				User.SYSTEM_GUID, null, comments, null, null,
				Category.ACCOUNT, SubCategory.STATUS_CHANGE, 80, user.getDomain().getName());
	}

	private void updateResult(ProviderAuthorizationResult result, String cruksResult, ExternalAuthorizationRequest request, User user) {
		switch (cruksResult) {
			case CRUKSResultService.CRUKS_RESULT_PASS:
				result.setAuthorisationOutcome(EAuthorizationOutcome.ACCEPT);
				if (!ObjectUtils.isEmpty(user)) {
					failedAttemptService.removeUserFromAttempts(user.guid());
				}
				break;
			case CRUKSResultService.CRUKS_RESULT_FAIL:
			case CRUKSResultService.CRUKS_RESULT_INVALID:
				result.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
				if (!ObjectUtils.isEmpty(user)) {
					failedAttemptService.removeUserFromAttempts(user.guid());
				}
				break;
			case CRUKSResultService.CRUKS_RESULT_NONE:
				result.setAuthorisationOutcome(EAuthorizationOutcome.TIMEOUT);
				retryRequest(request, user, CRUKSResultService.CRUKS_RESULT_NONE_MESSAGE);
				break;
			case CRUKSResultService.CRUKS_RESULT_ERROR:
				result.setAuthorisationOutcome(EAuthorizationOutcome.TIMEOUT);
				retryRequest(request, user, CRUKSResultService.CRUKS_RESULT_ERROR_MESSAGE);
				break;
			case CRUKSResultService.CRUKS_RESULT_REVIEW:
				result.setAuthorisationOutcome(EAuthorizationOutcome.REVIEW);
				break;
			default: {
				log.error("Unhandled result received from cruks workflow [result=" + cruksResult + "]");
				result.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
			}
		}
	}

	private String getUniqueReference(Configuration configuration) {
		String uniqueReference = UUID.randomUUID().toString().replace("-", ""); //only used for correlation from lithium to sphonic - 35 char limit on uniqueReference
		if (configuration.getCruksMode().contentEquals(CRUKS_MODE_TEST_PASS)) {
			uniqueReference = "Pass";
		} else if (configuration.getCruksMode().contentEquals(CRUKS_MODE_TEST_FAIL)) {
			uniqueReference = "Fail";
		}
		return uniqueReference;
	}

	private void populateResponseDataMap(final Object responseObj, String cruksId, Map<String, String> data) {
		data.put("cruksId", cruksId);
		if (responseObj instanceof CRUKSRegistrationResponse) {
			final CRUKSRegistrationResponse response = (CRUKSRegistrationResponse) responseObj;
			SphonicDataUtil.resolve(() -> response.getSphonicResponse().getData().getVendorData()
					.getDebtorReference())
					.ifPresent(debtorReference -> data.put("debtorReference", debtorReference));
			SphonicDataUtil.resolve(() -> response.getSphonicResponse().getData().getVendorData()
					.getIsRegistered())
					.ifPresent(isRegistered -> data.put("isRegistered", isRegistered));
		} else if (responseObj instanceof CRUKSLoginResponse) {
			final CRUKSLoginResponse response = (CRUKSLoginResponse) responseObj;
			SphonicDataUtil.resolve(() -> response.getSphonicResponse().getData().getVendorData()
					.getDebtorReference())
					.ifPresent(debtorReference -> data.put("debtorReference", debtorReference));
			SphonicDataUtil.resolve(() -> response.getSphonicResponse().getData().getVendorData()
					.getIsRegistered())
					.ifPresent(isRegistered -> data.put("isRegistered", isRegistered));
		}
	}

	public String getDobFormatted(int dobDay, int dobMonth, int dobYear) {
		DateTime dob = new DateTime().withDate(dobYear, dobMonth, dobDay)
				.withTime(0, 0, 0, 0);
		DateTimeFormatter dtf = DateTimeFormat.forPattern(CRUKS_DOB_FORMAT);
		return dtf.print(dob);
	}

	public CRUKSRegistrationResponse cruksRegistration(Configuration configuration, String accessToken, String firstName,
	        String lastName, String lastNamePrefix, String dob, String bsn, String birthPlace, String uniqueReference,
	        ArrayList<RawAuthorizationData> rawAuthorizationDataList) throws Status500InternalServerErrorException {
		// Using a random UUID for request id. Should it be stored somewhere? SVC-access will store the raw request,
		// but searching for the request id will not be pleasant.
		CRUKSRegistrationRequest request = buildCRUKSRegistrationRequest(UUID.randomUUID().toString(), firstName,
				lastName, lastNamePrefix, dob, bsn, birthPlace, uniqueReference);
		CRUKSRegistrationResponse response = callExternalProvider(configuration, accessToken, request);
		// Cannot log BSN.
		request.getRequestData().getAdditionalDetails().setBsn(null);
		log.trace("Cruks registration [request="+request+ ", response="+response+"]");
		addRawAuthorizationData(rawAuthorizationDataList, request, response);
		return response;
	}

	public CRUKSLoginResponse cruksLogin(Configuration configuration, String accessToken, String cruksId,
	        String uniqueReference, ArrayList<RawAuthorizationData> rawAuthorizationDataList)
			throws Status500InternalServerErrorException {
		// Using a random UUID for request id. Should it be stored somewhere? SVC-access will store the raw request,
		// but searching for the request id will not be pleasant.
		CRUKSLoginRequest request = buildCRUKSLoginRequest(UUID.randomUUID().toString(), cruksId, uniqueReference);
		CRUKSLoginResponse response = callExternalProvider(configuration, accessToken, request);
		log.trace("Cruks login [request="+request+", response="+response+"]");
		addRawAuthorizationData(rawAuthorizationDataList, request, response);
		return response;
	}

	private void validateRequiredFields(ExternalAuthorizationRequest request, boolean bsnRequired, boolean placeOfBirthRequired) {
		Assert.notNull(request.getPlayerBasic(), "PlayerBasic must not be null");

		List<String> missingFields = new ArrayList<>();

		if (bsnRequired) {
			Assert.notNull(request.getAdditionalData(), "additionalData must not be null");
			Map<String, String> additionalData = request.getAdditionalData();
			String bsn = additionalData.get("bsn");
			if (bsn == null || bsn.trim().isEmpty()) {
				missingFields.add("additionalData.bsn");
			}
		} else if (placeOfBirthRequired){
			String placeOfBirth = request.getPlayerBasic().getPlaceOfBirth();
			String bsn = (request.getAdditionalData() != null)
					? request.getAdditionalData().get("bsn")
					: null;
			// place of birth may be passed through with an empty string
			if ((placeOfBirth == null) &&
					(bsn == null || bsn.trim().isEmpty())) {
				missingFields.add("playerBasic.placeOfBirth OR additionalDetails.bsn");
			}
		}
		if (request.getPlayerBasic().getLastName() == null ||
				request.getPlayerBasic().getLastName().trim().isEmpty()) {
			missingFields.add("playerBasic.lastName");
		}
		if (request.getPlayerBasic().getDobDay() == null) {
			missingFields.add("playerBasic.dobDay");
		}
		if (request.getPlayerBasic().getDobMonth() == null) {
			missingFields.add("playerBasic.dobMonth");
		}
		if (request.getPlayerBasic().getDobYear() == null) {
			missingFields.add("playerBasic.dobYear");
		}
		if (request.getPlayerBasic().getEmail() == null ||
				request.getPlayerBasic().getEmail().trim().isEmpty()) {
			missingFields.add("playerBasic.email");
		}

		if (!missingFields.isEmpty()) {
			String missingFieldsStr = missingFields.stream()
					.map(String::toString)
					.collect(Collectors.joining(", "));
			throw new IllegalArgumentException("One or more required fields not provided. ["+missingFieldsStr+"]");
		}
	}

	private void addRawAuthorizationData(ArrayList<RawAuthorizationData> rawAuthorizationDataList, Object request,
	        Object response) {
		if (rawAuthorizationDataList != null) {
			RawAuthorizationData rawData = RawAuthorizationData.builder().build();
			rawData.setRawRequestToProvider((request != null) ? JsonStringify.objectToString(request) : null);
			rawData.setRawResponseFromProvider((response != null) ? JsonStringify.objectToString(response) : null);
			rawAuthorizationDataList.add(rawData);
		}
	}

	private CRUKSRegistrationRequest buildCRUKSRegistrationRequest(String requestId, String firstNames, String surname,
	        String surnamePreFix, String dateOfBirth, String bsn, String birthPlace, String uniqueReference) {
		CRUKSRegistrationRequest request = CRUKSRegistrationRequest.builder()
				.requestDetails(RequestDetails.builder()
						.requestId(requestId)
						.requestDateTime(DateTime.now().toDateTimeISO().toString())
						.build())
				.requestData(RequestData.builder()
						.name(Name.builder()
								.firstNames(firstNames)
								.surname(surname)
								.surnamePreFix(surnamePreFix)
								.build())
						.dateOfBirth(DateOfBirth.builder()
								.dateOfBirth(dateOfBirth)
								.build())
						.additionalDetails(AdditionalDetails.builder()
								.bsn(bsn)
								.birthPlace(birthPlace)
								.uniqueReference(uniqueReference)
								.build())
						.build())
				.build();
		return request;
	}

	private CRUKSLoginRequest buildCRUKSLoginRequest(String requestId, String cruksId, String uniqueReference) {
		CRUKSLoginRequest request = CRUKSLoginRequest.builder()
				.requestDetails(RequestDetails.builder()
						.requestId(requestId)
						.requestDateTime(DateTime.now().toDateTimeISO().toString())
						.build())
				.requestData(lithium.service.access.provider.sphonic.schema.cruks.login.RequestData.builder()
						.additionalDetails(lithium.service.access.provider.sphonic.schema.cruks.login.AdditionalDetails
								.builder()
								.cruksId(cruksId)
								.uniqueReference(uniqueReference)
								.build())
						.build())
				.build();
		return request;
	}

	private CRUKSRegistrationResponse callExternalProvider(Configuration configuration, String accessToken,
	        CRUKSRegistrationRequest request) throws Status500InternalServerErrorException {
		String url = configuration.getCruksUrl() + "/" + configuration.getMerchantId() + "/"
				+ configuration.getCruksRegistrationWorkflowName();
		sphonicHTTPService.setRest(restService.
						restTemplate(
								configuration.getConnectTimeout(),
								configuration.getConnectionRequestTimeout(),
								configuration.getSocketTimeout()));
		return sphonicHTTPService.postForEntity(accessToken, url, request, CRUKSRegistrationResponse.class);
	}

	private CRUKSLoginResponse callExternalProvider(Configuration configuration, String accessToken,
	        CRUKSLoginRequest request) throws Status500InternalServerErrorException {
		String url = configuration.getCruksUrl() + "/" + configuration.getMerchantId() + "/"
				+ configuration.getCruksLoginWorkflowName();
		sphonicHTTPService.setRest(restService.
						restTemplate(
								configuration.getConnectTimeout(),
								configuration.getConnectionRequestTimeout(),
								configuration.getSocketTimeout()));
		return sphonicHTTPService.postForEntity(accessToken, url, request, CRUKSLoginResponse.class);
	}

	public String validateCruksId(String cruksId, String domainName) throws Status550ServiceDomainClientException, Status512ProviderNotConfiguredException, Status500InternalServerErrorException {
		String result;

		try {
			Configuration configuration = configurationService.getDomainConfiguration(domainName);

			String accessToken = sphonicAuthenticationService.getAccessToken(authenticationRepository,
					domainName, configuration.getAuthenticationUrl(), configuration.getUsername(),
					configuration.getPassword(), configuration.getConnectTimeout(), configuration.getConnectionRequestTimeout(), configuration.getSocketTimeout());

			String uniqueReference = getUniqueReference(configuration);

			CRUKSLoginResponse cruksLoginResponse = cruksLogin(configuration, accessToken, cruksId, uniqueReference,
					new ArrayList<>());

			result = cruksLoginResponse.getSphonicResponse().getData().getResult().toUpperCase();
		} catch (Exception e ){
			log.error("CRUKS validation failed [domain=" + domainName + "] | " + e.getMessage(), e);
			throw e;
		}

		return result;

	}

}
