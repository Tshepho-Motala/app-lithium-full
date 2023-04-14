package lithium.service.kyc.service;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.KycMetricService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.kyc.provider.objects.KycBank;
import lithium.service.kyc.entities.KYCReason;
import lithium.service.kyc.entities.MethodType;
import lithium.service.kyc.entities.VerificationResult;
import lithium.service.kyc.provider.clients.KycVerifyClient;
import lithium.service.kyc.provider.config.VerifyIdParameters;
import lithium.service.kyc.provider.exceptions.Status400BadRequestException;
import lithium.service.kyc.provider.exceptions.Status406InvalidVerificationNumberException;
import lithium.service.kyc.provider.exceptions.Status407InvalidVerificationIdException;
import lithium.service.kyc.provider.exceptions.Status424KycVerificationUnsuccessfulException;
import lithium.service.kyc.provider.exceptions.Status425IllegalUserStateException;
import lithium.service.kyc.provider.exceptions.Status427UserKycVerificationLifetimeAttemptsExceeded;
import lithium.service.kyc.provider.exceptions.Status428KycMismatchLastNameException;
import lithium.service.kyc.provider.exceptions.Status429KycMismatchDobException;
import lithium.service.kyc.provider.exceptions.Status504KycProviderEndpointUnavailableException;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.exceptions.Status515SignatureCalculationException;
import lithium.service.kyc.provider.exceptions.Status520KycProviderEndpointException;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.kyc.provider.objects.VerificationMethodType;
import lithium.service.kyc.provider.objects.VerifyParam;
import lithium.service.kyc.provider.objects.VerifyRequest;
import lithium.service.kyc.repositories.MethodTypeRepository;
import lithium.service.kyc.schema.VerificationStatusResponse;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.stats.client.enums.Event;
import lithium.service.stats.client.exceptions.Status513StatsServiceUnavailableException;
import lithium.service.stats.client.objects.StatEntry;
import lithium.service.stats.client.service.StatsClientService;
import lithium.service.stats.client.stream.QueueStatEntry;
import lithium.service.stats.client.stream.StatsStream;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserVerificationStatusUpdate;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lithium.metrics.builders.kyc.EntryPoint.KYC_SERVICE_ON_VERIFY;

@Slf4j
@Service
@AllArgsConstructor
public class UpdateVerificationStatusService {
    private final static int AGE_RESTRICTION = 18;
    private final static String USER_KYC_TOTAL_ATTEMPT_THRESHOLD = "user-kyc-total-attempt-threshold";
    private final static Integer DEFAULT_KYC_TOTAL_ATTEMPT_THRESHOLD = 3;
    private final static String KYC_METHOD_ORDERING = "kyc-method-ordering";

    private final LithiumServiceClientFactory services;
    private final UserApiInternalClientService userService;
    private final ProviderClientService providerClientService;
    private final CachingDomainClientService cachingDomainClientService;
    private final StatsStream statsStream;
    private final StatsClientService statsClientService;
    private final MethodTypeRepository methodTypeRepository;
    private final VerificationResultsService verificationResultsService;
    private final KycMetricService kycMetricService;

    public VerificationStatusResponse verify(VerifyRequest verifyRequest, String userGuid)
            throws UserNotFoundException, UserClientServiceFactoryException, LithiumServiceClientFactoryException, Status520KycProviderEndpointException,
            Status406InvalidVerificationNumberException, Status515SignatureCalculationException, Status407InvalidVerificationIdException,
            Status512ProviderNotConfiguredException, Status424KycVerificationUnsuccessfulException, Status504KycProviderEndpointUnavailableException,
            Status425IllegalUserStateException, Status400BadRequestException, Status550ServiceDomainClientException, Status500InternalServerErrorException,
            Status513StatsServiceUnavailableException, Status427UserKycVerificationLifetimeAttemptsExceeded,
            Status428KycMismatchLastNameException, Status429KycMismatchDobException {

        User user = userService.getUserByGuid(userGuid);

        if (user.getDateOfBirth() == null) {
            log.info("Users " + userGuid + " date of birth is not set");
            updateStatus(user, "Logged in player date of birth is not set", VerificationStatus.UNVERIFIED.getId());
            throw new Status425IllegalUserStateException("Logged in player date of birth is not set");
        }

        Long userVerificationStatusId = user.getVerificationStatus();
        if (nonNull(userVerificationStatusId) && VerificationStatus.isVerified(userVerificationStatusId)) {
            log.info("User " + userGuid + " already verified (" + userVerificationStatusId + ")");
            return VerificationStatusResponse.newFrom(getVerificationStatusById(userVerificationStatusId));
        }
        if (user.getDateOfBirth().plusYears(AGE_RESTRICTION).isAfterNow()) {
            log.info("Users " + userGuid + " is less that 18 years old");
            VerificationStatus userVerificationStatus = updateStatus(user, "User is less that 18 years old", VerificationStatus.UNDERAGED.getId());
            return VerificationStatusResponse.newFrom(userVerificationStatus);
        }

        VerificationMethodType verificationMethodName = verifyRequest.getVerificationMethodName();
        String definedLastName = verifyRequest.getFields().stream()
                .filter(verifyParam -> VerifyIdParameters.LAST_NAME_PARAM.equals(verifyParam.getKey()))
                .findAny()
                .map(VerifyParam::getValue)
                .orElseThrow(() -> {
                    log.info("User's " + userGuid + " last name is not provided in request");
                    return new Status400BadRequestException("Last name is not provided in request");
                });

        String domainName = user.getDomain().getName();

        List<Provider> activeProviders  = providerClientService.providers(domainName, ProviderConfig.ProviderType.KYC)
                .stream()
                .filter(Provider::getEnabled)
                .filter(provider -> "true".equals(provider.getProperties()
                        .stream()
                        .collect(Collectors.toMap(ProviderProperty::getName, ProviderProperty::getValue))
                        .get(verificationMethodName.getValue())))
                .collect(Collectors.toList());

        if (activeProviders.isEmpty()) {
            log.warn("No configured providers which support " + verificationMethodName);
            updateStatus(user, "No configured providers which support " + verificationMethodName, VerificationStatus.UNVERIFIED.getId());
            verificationResultsService.saveFailAttempt(user, verificationMethodName, "No configured providers which support " + verificationMethodName);
            throw new Status512ProviderNotConfiguredException(" which support " + verificationMethodName);
        }

        updateVerifyRequestWithUserData(verifyRequest, user);
        checkVerificationAttempts(domainName, userGuid);

        Iterator<Provider> providerIterator = activeProviders.iterator();
        while (providerIterator.hasNext()) {
            Provider provider = providerIterator.next();

            String kycProviderUrl = provider.getUrl();

	        KYCReason reason = KYCReason.REASON_UNDEFINED;
            KycSuccessVerificationResponse kvResponse = null;
            VerificationResult verificationResult = verificationResultsService.buildAndSaveInitialVerificationResult(user, verificationMethodName, provider.getName(), reason);
            log.debug("Initial Verification Result saved as" + verificationResult);

            try {

                log.info("Trying to verify " + userGuid + " with " + verificationMethodName + " ( **** " + lastNChars(verifyRequest.getFieldsAsMap().get(VerifyIdParameters.ID_NUMBER_PARAM), 5) + ") via " + kycProviderUrl);
                KycVerifyClient verifyClient = services.target(KycVerifyClient.class, kycProviderUrl, true);
                kycMetricService.startAttemptMetrics(domainName, KYC_SERVICE_ON_VERIFY, kycProviderUrl);

                ResponseEntity<KycSuccessVerificationResponse> verifyResponseEntity = verifyClient.verify(verifyRequest);
                kvResponse = verifyResponseEntity.getBody();

                String personLastName = kvResponse.getLastName();
                DateTime personDob = DateTime.parse(kvResponse.getDob(), DateTimeFormat.forPattern("yyyy-MM-dd"));

                String verifiedComment = checkIsUserEquals(definedLastName, user, personLastName, personDob, kvResponse.isDobYearOnly(), verificationMethodName, kycProviderUrl);

                verificationResultsService.updateWithKycVerificationData(kvResponse, verificationResult, KYCReason.CHECKS_PASSED);
                VerificationStatusResponse verificationStatusResponse = VerificationStatusResponse.newFrom(updateStatus(user, verifiedComment, VerificationStatus.EXTERNALLY_VERIFIED.getId()));
                updateVerificationAttemptStat(domainName, userGuid);
                kycMetricService.passAttemptMetrics(domainName, KYC_SERVICE_ON_VERIFY, kycProviderUrl);
		        return verificationStatusResponse;
	        } catch (Exception e) {
                reason = resolveReason(e);
                verificationResultsService.updateWithKycVerificationData(kvResponse, verificationResult, reason);
                kycMetricService.failAttemptMetrics(domainName, KYC_SERVICE_ON_VERIFY, kycProviderUrl);
		        if (providerIterator.hasNext()) {
			        log.warn("Can't verify user " + user.getGuid() + " using " + verificationMethodName + " on " + kycProviderUrl + " due " + e.getMessage() + ". Trying next provider");
		        } else {
			        boolean sendSms = false;
			        log.warn("Can't verify user " + user.getGuid() + " using " + verificationMethodName + " on " + kycProviderUrl + " due " + e.getMessage());
			        if (!isProviderSmileIdentityAndUnavailable(kycProviderUrl, e)) {
				        sendSms = true;
				        updateVerificationAttemptStat(domainName, userGuid);
			        }
                    updateStatus(user, "Can't verify user " + user.getGuid() + " using " + verificationMethodName + " on " + kycProviderUrl + " due " + e.getMessage(), VerificationStatus.UNVERIFIED.getId(), sendSms);
                    throw e;
                }
            }
        }

        if (isNull(userVerificationStatusId)) {
            updateStatus(user, "Can't verify user due to unexpected behaviour", VerificationStatus.UNVERIFIED.getId());
        }
        log.error("Can't verify user (" + userGuid + ") due to unexpected behaviour");
        throw new Status424KycVerificationUnsuccessfulException("We're unable to verify your account. Please use another method or update your details and retry.");
    }

    private KYCReason resolveReason(Exception e) {
        if (e instanceof Status429KycMismatchDobException) {
            return KYCReason.DOB_MISMATCH;
        } else if (e instanceof Status428KycMismatchLastNameException) {
            return KYCReason.SURNAME_MISMATCH;
        }
        return KYCReason.METHOD_UNAVAILABLE;
    }

    private boolean isProviderSmileIdentityAndUnavailable(String kycProviderUrl, Exception e) {
        boolean isSmileIdentity = "service-kyc-provider-smileidentity".equals(kycProviderUrl);
        boolean isProviderUnavailable = e.getClass().getCanonicalName().equals(Status504KycProviderEndpointUnavailableException.class.getCanonicalName());
        return isSmileIdentity && isProviderUnavailable;
    }

    private String lastNChars(String string, int charsNumber) {
        if (charsNumber > string.length()) {
            return "too short";
        }
        return string.substring(string.length() - charsNumber);
    }

    private void checkVerificationAttempts(String domainName, String userGuid) throws Status550ServiceDomainClientException, Status513StatsServiceUnavailableException, Status500InternalServerErrorException, Status427UserKycVerificationLifetimeAttemptsExceeded {
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        Integer totalAttemptThreshold = domain.findDomainSettingByName(USER_KYC_TOTAL_ATTEMPT_THRESHOLD)
                .map(Integer::valueOf)
                .orElse(DEFAULT_KYC_TOTAL_ATTEMPT_THRESHOLD);
        long userVerificationAttemptCount = statsClientService.getAllTimeStatCountForUser(userGuid, "user", Event.KYC_VERIFICATION_ATTEMPT.event());
        if (userVerificationAttemptCount >= totalAttemptThreshold) {
            log.info("User's " + userGuid + " kyc verification attempts is out (attempts used: " + userVerificationAttemptCount + ")");
            throw new Status427UserKycVerificationLifetimeAttemptsExceeded("It looks like you're having difficulty verifying yourself. Please contact customer support here to complete the process.");
        }
    }

    private void updateVerificationAttemptStat(String domainName, String userGuid) {
        QueueStatEntry queueStatEntry = QueueStatEntry.builder()
                .type(lithium.service.stats.client.enums.Type.USER.type())
                .event(Event.KYC_VERIFICATION_ATTEMPT.event())
                .entry(
                        StatEntry.builder()
                                .name(
                                        "stats." +
                                            "user" + "." +
                                                userGuid.replaceAll("/", ".") + "." +
                                                Event.KYC_VERIFICATION_ATTEMPT.event()
                                )
                                .domain(domainName)
                                .ownerGuid(userGuid)
                                .build()
                )
                .build();
        log.info("StatStream :: " + queueStatEntry);
        statsStream.register(queueStatEntry);
    }

    private void updateVerifyRequestWithUserData(VerifyRequest verifyRequest, User user) {
	    Map<String, String> requestMap = verifyRequest.getFieldsAsMap();
    	if (requestMap.get(VerifyIdParameters.GUID_PARAM) == null) verifyRequest.getFields().add(new VerifyParam(VerifyIdParameters.GUID_PARAM, user.getGuid()));
	    if (requestMap.get(VerifyIdParameters.DOMAIN_NAME_PARAM) == null) verifyRequest.getFields().add(new VerifyParam(VerifyIdParameters.DOMAIN_NAME_PARAM, user.getDomain().getName()));
	    if (requestMap.get(VerifyIdParameters.FIRST_NAME_PARAM) == null) verifyRequest.getFields().add(new VerifyParam(VerifyIdParameters.FIRST_NAME_PARAM, user.getFirstName()));
	    if (requestMap.get(VerifyIdParameters.DOB_PARAM) == null) verifyRequest.getFields().add(new VerifyParam(VerifyIdParameters.DOB_PARAM, user.getDateOfBirth().toString("yyyy-MM-dd")));
    }

    private String checkIsUserEquals(String definedLastName, User user, String personLastName, DateTime personDob, boolean checkDobYearOnly, VerificationMethodType verificationMethodName, String kycProviderUrl) throws Status428KycMismatchLastNameException, Status429KycMismatchDobException {

	    if (!personLastName.trim().equalsIgnoreCase(definedLastName.trim())) {
		    log.warn("Can't compare user's (" + definedLastName + ") and verified (" + personLastName + ") personLastName");
		    throw new Status428KycMismatchLastNameException();
	    }

	    if (isMoreThanNineTeenYearsPerson(personDob)) {
	    	String message = "Verification successful: exact name match and over 19 year old";
		    log.debug(message);
		    return message;
	    }

	    DateTime userDob = user.getDateOfBirth();

	    if (checkDobYearOnly) {
            if (personDob.getYear() != userDob.getYear()) {
                log.warn("Can't compare user's (" + userDob.getYear() + ") and verified (" + personDob.getYear() + ") personDob year ");
                throw new Status429KycMismatchDobException();
            }
        } else {
            if (!personDob.isEqual(userDob)) {
                log.warn("Can't compare user's (" + userDob.toString("yyyy-MM-dd") + ") and verified (" + personDob.toString("yyyy-MM-dd") + ") personDob ");
                throw new Status429KycMismatchDobException();
            }
        }
	    return "Verified using " + verificationMethodName + "(" + definedLastName + ", " + user.getDateOfBirth().toString("yyyy-MM-dd") + ") via " + kycProviderUrl;
    }

	private VerificationStatus updateStatus(User user, String comment, Long userVerificationStatusId) throws UserClientServiceFactoryException, UserNotFoundException {
        return updateStatus(user, comment, userVerificationStatusId, true);
    }

    private VerificationStatus updateStatus(User user, String comment, Long userVerificationStatusId, Boolean sendSms) throws UserClientServiceFactoryException, UserNotFoundException {
        User updatedUser = userService.editUserVerificationStatus(UserVerificationStatusUpdate.builder()
                .userId(user.getId())
                .statusId(userVerificationStatusId)
                .comment(comment)
                .userGuid(user.guid())
                .sendSms(sendSms)
                .build());
        log.info("User " + user.getGuid() + " verification status updated with id=[" + updatedUser.getVerificationStatus() + "]. " + comment);
        return getVerificationStatusById(userVerificationStatusId);
    }

    private VerificationStatus getVerificationStatusById(Long userVerificationStatusId) throws UserClientServiceFactoryException {
        Optional<VerificationStatus> verificationStatusOpt = Arrays.asList(VerificationStatus.values())
                .stream().filter(verificationStatus -> verificationStatus.getId() == userVerificationStatusId)
                .collect(Collectors.toList()).stream().findFirst();

        if (verificationStatusOpt.isPresent()) {
            return verificationStatusOpt.get();
        } else {
            log.error("Verification status not found id = [" + userVerificationStatusId + "]");
            throw new UserClientServiceFactoryException("Verification status not found id = [" + userVerificationStatusId + "]");
        }
    }

    public List<String> getKycMethodOrder(String domainName) throws Status550ServiceDomainClientException {
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        return domain.findDomainSettingByName(KYC_METHOD_ORDERING)
                .map(s -> Stream.of(s.split(",")).map(String::trim).collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

    public void setupVerificationMethodTypesFromEnum() {
        for (VerificationMethodType methodType : VerificationMethodType.values()) {
            methodTypeRepository.findOrCreateByName(
                    methodType.name(),
                    () -> MethodType.builder().name(methodType.getValue()).build()
            );
        }
    }

	private boolean isMoreThanNineTeenYearsPerson(DateTime personDob) {
		return (new DateTime()).compareTo(personDob.plusYears(19)) >=0 ;
	}

	public List<KycBank> banks(String kycProviderUrl, String domainName) throws Exception {
		KycVerifyClient verifyClient = services.target(KycVerifyClient.class, kycProviderUrl, true);
    	return verifyClient.banks(domainName);
	}
}
