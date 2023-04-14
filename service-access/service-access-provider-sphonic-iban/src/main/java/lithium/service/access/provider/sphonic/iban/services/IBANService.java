package lithium.service.access.provider.sphonic.iban.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.access.client.objects.EAuthorizationOutcome;
import lithium.service.access.client.objects.ExternalAuthorizationRequest;
import lithium.service.access.client.objects.ProviderAuthorizationResult;
import lithium.service.access.client.objects.RawAuthorizationData;
import lithium.service.access.provider.sphonic.iban.config.Configuration;
import lithium.service.access.provider.sphonic.iban.services.exceptions.MissingIbanException;
import lithium.service.access.provider.sphonic.iban.storage.repositories.AuthenticationRepository;
import lithium.service.access.provider.sphonic.schema.RequestDetails;
import lithium.service.access.provider.sphonic.schema.iban.AdditionalDetails;
import lithium.service.access.provider.sphonic.schema.iban.IBANRequest;
import lithium.service.access.provider.sphonic.schema.iban.IBANResponse;
import lithium.service.access.provider.sphonic.schema.iban.Name;
import lithium.service.access.provider.sphonic.schema.iban.RequestData;
import lithium.service.access.provider.sphonic.schema.iban.VendorData;
import lithium.service.access.provider.sphonic.services.RestService;
import lithium.service.access.provider.sphonic.services.SphonicAuthenticationService;
import lithium.service.access.provider.sphonic.services.SphonicHTTPService;
import lithium.service.access.provider.sphonic.util.SphonicUserRevisionLabelValueUtil;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.kyc.client.KycResultsClient;
import lithium.service.kyc.client.objects.VerificationKycAttempt;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.Label;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserRevision;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static lithium.service.user.client.objects.Label.IBAN;

@Service
@Slf4j
public class IBANService {
	@Autowired private AuthenticationRepository authenticationRepository;
	@Autowired private ConfigurationService configurationService;
	@Autowired private SphonicAuthenticationService sphonicAuthenticationService;
	@Autowired private SphonicHTTPService sphonicHTTPService;
	@Autowired private ObjectMapper mapper;
	@Autowired private UserApiInternalClientService userApiInternalClientService;
	@Autowired private ChangeLogService changeLogService;
	@Autowired private RestService restService;
	@Autowired private LithiumServiceClientFactory lithiumServices;

	private static final String IBAN_RESULT_PASS = "PASS";
	private static final String IBAN_RESULT_FAIL = "FAIL";
	private static final String IBAN_RESULT_REVIEW = "REVIEW";
	private static final String IBAN_RESULT_ERROR = "ERROR";
	private static final String IBAN_MODE_TEST_PASS = "test_pass";
	private static final String IBAN_MODE_TEST_FAIL = "test_fail";

	public ProviderAuthorizationResult checkAuthorization(ExternalAuthorizationRequest request) {
		log.trace("Request " + request);
		ProviderAuthorizationResult result = ProviderAuthorizationResult.builder().build();

		try {
			String iban = resolveIban(request);
			PlayerBasic player = request.getPlayerBasic();
			if (isNull(player) && Objects.nonNull(request.getUserGuid())) {
				player = resolvePlayer(request.getUserGuid());
			}
			log.trace("Player " + player);

			Configuration configuration = configurationService.getDomainConfiguration(request.getDomainName());
			String accessToken = sphonicAuthenticationService.getAccessToken(authenticationRepository,
					request.getDomainName(), configuration.getAuthenticationUrl(), configuration.getUsername(),
					configuration.getPassword(), configuration.getConnectTimeout(), configuration.getConnectionRequestTimeout(), configuration.getSocketTimeout());

			String uniqueReference = resolveUniqueReference(player, configuration);

			RawAuthorizationData rawAuthorizationData = prepareEmptyAuthorizationData(result);

			IBANRequest ibanRequest = buildIBANRequest(UUID.randomUUID().toString(), buildFullName(player), iban, uniqueReference);
			rawAuthorizationData.setRawRequestToProvider(JsonStringify.objectToString(ibanRequest));

			IBANResponse ibanResponse = callExternalProvider(configuration, accessToken, ibanRequest);
			rawAuthorizationData.setRawResponseFromProvider(Optional.ofNullable(ibanResponse).map(IBANResponse::getRawBody).orElse(null));

			log.trace("IBAN verification [request=" + ibanRequest + ", response=" + ibanResponse + "]");

			result.setData(ibanResponse.getSphonicResponse().getData().toMap());
			String ibanResult = ibanResponse.getSphonicResponse().getData().getResult().toUpperCase();
            switch (ibanResult) {
                case IBAN_RESULT_PASS -> result.setAuthorisationOutcome(EAuthorizationOutcome.ACCEPT);
                case IBAN_RESULT_FAIL -> result.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
                case IBAN_RESULT_REVIEW -> result.setAuthorisationOutcome(EAuthorizationOutcome.REVIEW);
                case IBAN_RESULT_ERROR -> {
                    result.setAuthorisationOutcome(EAuthorizationOutcome.TIMEOUT);
                    log.error("IBAN API failed to get a valid response from Sphonic : result = ERROR " + ibanResponse.getSphonicResponse().getData().getVendorData().getIbanResult());
                }
                default -> {
                    log.error("Unhandled result received from iban workflow [result=" + ibanResult + "]");
                    result.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
                }
            }

            if (Objects.nonNull(request.getUserGuid())) {
                saveVerificationAttemptResult(configuration.getProviderName(), request.getUserGuid(), request.getDomainName(), ibanResponse);
            }
		} catch (MissingIbanException e) {
            log.error("Iban is missing for sphonic check : " + e.getMessage(), e);
			result.setErrorMessage(e.getMessage());
			result.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
		} catch (Throwable e) {
			log.error("Failed to perform HTTP request to sphonic : " + e.getMessage(), e);
			result.setAuthorisationOutcome(EAuthorizationOutcome.TIMEOUT);
			result.setErrorMessage(e.getMessage());
		}
		logVerificationAttemptToRelatedUser(request, result);
		return result;
	}

    private PlayerBasic resolvePlayer(String userGuid) throws UserClientServiceFactoryException {
        User user = userApiInternalClientService.getUserByGuid(userGuid);
        return PlayerBasic.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .lastNamePrefix(user.getLastNamePrefix())
                .build();
    }

    private void saveVerificationAttemptResult(String providerName, String guid, String domainName, IBANResponse ibanResponse) {
        VendorData ibanVendorData = ibanResponse.getSphonicResponse().getData().getVendorData();

        KycSuccessVerificationResponse kycVerificationResponse = KycSuccessVerificationResponse.builder()
            .createdOn(DateTime.now().toDate())
            .success(IBAN_RESULT_PASS.equalsIgnoreCase(ibanResponse.getSphonicResponse().getData().getResult()))
                    .fullName(ibanVendorData.getAssumedName())
                    .vendorsData(List.of(ibanVendorData.convertVendorData()))
                    .resultMessageText("IBAN verification via Sphonic complete: "+ibanResponse)
                    .build();

            VerificationKycAttempt attempt = VerificationKycAttempt.builder()
                    .userGuid(guid)
                    .domainName(domainName)
                    .verificationResultId(System.currentTimeMillis())
                    .kycSuccessVerificationResponse(kycVerificationResponse)
                    .providerName(providerName)
                    .methodName(IBAN)

                    .build();
        try {
            KycResultsClient kycResultsClient = lithiumServices.target(KycResultsClient.class, "service-kyc", true);
            kycResultsClient.addVerificationResult(attempt);
        } catch (LithiumServiceClientFactoryException ex) {
            log.error("Unable to save IBAN verification result guid={}, domain={}, result={}", guid, domainName, ibanResponse, ex);
        }
    }

    private void logVerificationAttemptToRelatedUser(ExternalAuthorizationRequest request, ProviderAuthorizationResult result) {
		String userGuid = request.getUserGuid();
		if (isNull(userGuid)) {
			userGuid = request.getAdditionalData().get(Label.GUID);
		}
		if (isNull(userGuid)) {
			log.warn("Can't store note about IBAN verification attempt due missing related user guid (" + request + ")");
			return;
		}
		try {
			List<ChangeLogFieldChange> changes = new ArrayList<>();

			result.getRawDataList().stream().findFirst()
					.map(RawAuthorizationData::getRawRequestToProvider)
					.ifPresent(requestToProvider -> changes.add(ChangeLogFieldChange.builder()
							.field("request")
							.toValue(requestToProvider)
							.build()));
			result.getRawDataList().stream().findFirst()
					.map(RawAuthorizationData::getRawResponseFromProvider)
					.ifPresent(responseFromProvider -> changes.add(ChangeLogFieldChange.builder()
							.field("response")
							.toValue(responseFromProvider)
							.build()));

			User user = userApiInternalClientService.getUserByGuid(userGuid);
			changeLogService.registerChangesSystem("user", "edit", user.getId(),
					"IBAN validation via svc-access-pr-sphonic-iban complete: " + result.getAuthorisationOutcome()
							+ "(" + Optional.ofNullable(result.getData()).map(map -> map.get("result")).orElse("no sphonic response") + ")",
							null, changes, Category.ACCOUNT, SubCategory.KYC, 80, user.getDomain().getName());
		} catch (Throwable e) {
			log.warn("Can't make note about IBAN verification attempt(" + userGuid + ") due " + e.getMessage(), e);
		}
	}

	private static RawAuthorizationData prepareEmptyAuthorizationData(ProviderAuthorizationResult result){
		ArrayList<RawAuthorizationData> rawAuthorizationDataList = new ArrayList<>();
		RawAuthorizationData rawAuthorizationData = RawAuthorizationData.builder()
				.build();
		rawAuthorizationDataList.add(rawAuthorizationData);
		result.setRawDataList(rawAuthorizationDataList);
		return rawAuthorizationData;
	}

	private static String resolveUniqueReference(PlayerBasic player, Configuration configuration) {
		String uniqueReference = player.getEmail();
		if (configuration.getIbanMode().contentEquals(IBAN_MODE_TEST_PASS)) {
			uniqueReference = "Pass";
		} else if (configuration.getIbanMode().contentEquals(IBAN_MODE_TEST_FAIL)) {
			uniqueReference = "Fail";
		}
		return uniqueReference;
	}

	private static String buildFullName(PlayerBasic player) {
		StringBuilder fullName = new StringBuilder();
		if (!ObjectUtils.isEmpty(player.getFirstName())) {
			fullName.append(player.getFirstName());
		}
		if (!ObjectUtils.isEmpty(player.getLastNamePrefix())) {
			fullName.append(" " + player.getLastNamePrefix());
		}
		if (!ObjectUtils.isEmpty(player.getLastName())) {
			fullName.append(" " + player.getLastName());
		}
		return fullName.toString();
	}

	private String resolveIban(ExternalAuthorizationRequest request) throws UserClientServiceFactoryException, UserNotFoundException, MissingIbanException {

		// If request.getUserGuid() is null, then this is called on a registration.
		// We need to have iban in the additionalDetails map.
		boolean isIbanInRequest = isNull(request.getUserGuid());
		if (isIbanInRequest) {
            validateRequiredFields(request, isIbanInRequest);
			return Optional.ofNullable(request.getAdditionalData().get(IBAN))
					.orElseThrow(() -> {
						String errorMessage = "Cannot do IBAN verification. IBAN could not be retrieved from request additional data.";
						log.error(errorMessage);
						return new MissingIbanException(errorMessage);});
		} else {
			User user = userApiInternalClientService.getUserByGuid(request.getUserGuid());
			log.trace("User " + user);

			return Optional.ofNullable(user.getCurrent())
					.map(UserRevision::getLabelValueList)
					.map(userRevisionLabelValues -> SphonicUserRevisionLabelValueUtil.getValueFromUserRevisionLabelValues(
							userRevisionLabelValues, IBAN))
					.orElseThrow(() -> {
						String errorMessage = "Cannot do IBAN verification. IBAN could not be retrieved from user service.";
						log.error(errorMessage + " [user.guid=" + user.guid() + "]");
						return new MissingIbanException(errorMessage);});
		}
	}

	private static void validateRequiredFields(ExternalAuthorizationRequest request, boolean ibanRequired) {
		Assert.notNull(request.getPlayerBasic(), "PlayerBasic must not be null");

		List<String> missingFields = new ArrayList<>();

		if (ibanRequired) {
			Assert.notNull(request.getAdditionalData(), "additionalData must not be null");
			Map<String, String> additionalData = request.getAdditionalData();
			String iban = additionalData.get("iban");
			if (iban == null || iban.trim().isEmpty()) {
				missingFields.add("additionalData.iban");
			}
		}
		if (request.getPlayerBasic().getFirstName() == null ||
				request.getPlayerBasic().getFirstName().trim().isEmpty()) {
			missingFields.add("playerBasic.firstName");
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

	private static IBANRequest buildIBANRequest(String requestId, String assumedName, String iban, String uniqueReference) {
		return IBANRequest.builder()
				.requestDetails(RequestDetails.builder()
						.requestId(requestId)
						.requestDateTime(DateTime.now().toDateTimeISO().toString())
						.build())
				.requestData(RequestData.builder()
						.name(Name.builder()
								.assumedName(assumedName)
								.build())
						.additionalDetails(AdditionalDetails.builder()
								.iban(iban)
								.uniqueReference(uniqueReference)
								.build())
						.build())
				.build();
	}

	private IBANResponse callExternalProvider(Configuration configuration, String accessToken, IBANRequest request)
			throws Status500InternalServerErrorException, IOException {
		String url = configuration.getIbanUrl() + "/" + configuration.getMerchantId() + "/"
				+ configuration.getIbanWorkflowName();
		sphonicHTTPService.setRest(restService.
						restTemplate(
								configuration.getConnectTimeout(),
								configuration.getConnectionRequestTimeout(),
								configuration.getSocketTimeout()));
		String rawBody = sphonicHTTPService.postForEntity(accessToken, url, request, String.class);
		IBANResponse ibanResponse = mapper.readValue(rawBody, IBANResponse.class);
		ibanResponse.setRawBody(rawBody);
		return ibanResponse;
	}
}
