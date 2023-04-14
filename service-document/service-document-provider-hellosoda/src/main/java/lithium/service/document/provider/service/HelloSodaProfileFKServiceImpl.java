package lithium.service.document.provider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.config.LithiumConfigurationProperties;
import lithium.modules.ModuleInfo;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.document.provider.api.exceptions.Status412FailToGetJobDetailsException;
import lithium.service.document.provider.api.exceptions.Status414FailFromHelloSodaServiceException;
import lithium.service.document.provider.api.exceptions.Status540ProviderNotConfiguredException;
import lithium.service.document.provider.api.schema.FKContact;
import lithium.service.document.provider.api.schema.FKReport;
import lithium.service.document.provider.api.schema.FKScores;
import lithium.service.document.provider.api.schema.FKSummaries;
import lithium.service.document.provider.api.schema.JobRequest;
import lithium.service.document.provider.api.schema.JobResponse;
import lithium.service.document.provider.api.schema.User;
import lithium.service.document.provider.config.ProviderConfig;
import lithium.service.document.provider.config.ProviderConfigService;
import lithium.service.document.provider.entity.UserDocumentStatus;
import lithium.service.document.provider.repository.UserDocumentStatusRepository;
import lithium.service.kyc.client.exceptions.Status459VerificationResultNotFountException;
import lithium.service.kyc.client.objects.VerificationKycAttempt;
import lithium.service.kyc.entities.VerificationResult;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.Address;
import lithium.service.user.client.objects.UserVerificationStatusUpdate;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static lithium.service.document.provider.Utils.toJson;
import static lithium.service.document.provider.service.VerificationAttemptService.NOT_AVAILABLE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Service
@Slf4j
@AllArgsConstructor
@EnableUserApiInternalClientService
public class HelloSodaProfileFKServiceImpl implements HelloSodaProfileFKService {

    private static final String FACEBOOK_NAME = "facebook";
    private static final String FACEBOOK_METHOD_TYPE = "facebook";
    private static final String STATUS_NAME_COMPLETED = "completed";
    private static final String NAME_MATCH = "name_match";

    private static final BigDecimal FACEBOOK_SOCIAL_FRAUD_THRESHOLD = BigDecimal.valueOf(7.0);
    private static final BigDecimal ID_SCORE_THRESHOLD = BigDecimal.valueOf(3L);
    private static final BigDecimal NAME_MATCH_THRESHOLD = BigDecimal.valueOf(0.7);

    private final ProviderConfigService providerConfigService;
    private final ModuleInfo moduleInfo;
    private final UserApiInternalClientService userClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate;
    private final UserDocumentStatusRepository statusRepository;
    private final ChangeLogService changeLogService;

    private final LithiumConfigurationProperties config;
    private final VerificationAttemptService verificationAttemptService;


    @Override
    public JobResponse submitFKCheck(String fkToken, String guid, String clientId, Long tokenUtilId) throws Status540ProviderNotConfiguredException, UserClientServiceFactoryException, UserNotFoundException, Status414FailFromHelloSodaServiceException, LithiumServiceClientFactoryException {
        lithium.service.user.client.objects.User user = userClient.getUserByGuid(guid);
        final ProviderConfig providerConfig = providerConfigService.getConfig(moduleInfo.getModuleName(), user.getDomain().getName());
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>() {{
            add("content-type", "application/json");
            add("Authorization", "Bearer " + providerConfig.getProfileBearer());
        }};
        Address residentialAddress = user.getResidentialAddress();

        lithium.service.document.provider.api.schema.User.Home home = null;
        if (nonNull(residentialAddress)) {
            home = lithium.service.document.provider.api.schema.User.Home.builder()
                    .city(residentialAddress.getCity())
                    .line1(residentialAddress.getAddressLine1())
                    .country(residentialAddress.getCountry())
                    .postcode(residentialAddress.getPostalCode())
                    .countryCode(residentialAddress.getCountryCode())
                    .build();
        }
        User userData = lithium.service.document.provider.api.schema.User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthdate(user.getDateOfBirth().toString("YYYY-MM-dd"))
                .home(home)
                .build();

        if (user.getEmail() != null) {
            FKContact FKContact = lithium.service.document.provider.api.schema.FKContact.builder().email(user.getEmail()).build();
            userData.setFKContact(FKContact);
        }

        String notifyUrl = config.getGatewayPublicUrl() + "/" + moduleInfo.getModuleName() + "/webhook/facebook/notify";


        Map<String, String> tokens = new HashMap<>();
        tokens.put(FACEBOOK_NAME, fkToken);

        JobRequest jobRequest = JobRequest.builder()
                .applicationId(clientId)
                .data(userData)
                .consumerId(UUID.randomUUID().toString())
                .notifyUrl(notifyUrl)
                .tokens(tokens)
                .commit(true)
                .build();

        log.debug("Prepared to commit job request: " + jobRequest + " for userGuid" + user.getGuid());
        ResponseEntity<Object> response =
                restTemplate.exchange(providerConfig.getProfileApiV1Url(),
                        POST, new HttpEntity<>(toJson(jobRequest), headers), Object.class, new HashMap<>());

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Got wrong response from hello soda (" + response.getStatusCode().value() + ") " + response.getBody() + "for userGuid: " + user.getGuid());
            throw new Status414FailFromHelloSodaServiceException(response.getBody().toString());
        }
        log.debug("Got Job response: " + response.getBody() + " for userGuid" + user.getGuid());

        JobResponse jobResponse = mapper.convertValue(response.getBody(), JobResponse.class);

        VerificationResult kycData = verificationAttemptService.registerKYCAttempt(user.getGuid(), user.getDomain().getName(), jobResponse.getJobId(), moduleInfo.getModuleName(), FACEBOOK_METHOD_TYPE);
        UserDocumentStatus docStatus = new UserDocumentStatus(jobResponse.getJobId(), jobResponse.getStatus(), user.guid(), tokenUtilId, "facebook", null, jobResponse.getConsumerId(), user.getDomain().getName(), false, kycData.getId());
        statusRepository.save(docStatus);
        try {
            List<ChangeLogFieldChange> changeLogFieldChanges = changeLogService.copy(docStatus, new UserDocumentStatus(),
                    new String[]{"jobId", "status", "userGuid", "functionName", "reportBody"});
            String changeLogComments = "Job request  committed with jobId (" + jobResponse.getJobId() + ", " + user.guid() + ")";

            changeLogService.registerChangesForNotesWithFullNameAndDomain("document.helloSoda.facebook.provider", "create",
                    docStatus.getUserId(), docStatus.getUserGuid(), null, changeLogComments,
                    null, changeLogFieldChanges, Category.ACCOUNT, SubCategory.KYC, 1, docStatus.getDomainName());
        } catch (Exception e) {
            log.error("User " + user.getGuid() + " hs facebook kyc attempt added, but changelog failed. (" + docStatus + ")", e);
        }
        return jobResponse;
    }

    private VerificationKycAttempt createAttempt(FKReport report, boolean passed, long verificationResultId) {
        KycSuccessVerificationResponse.KycSuccessVerificationResponseBuilder builder = KycSuccessVerificationResponse.builder();
        if (nonNull(report.getSummaries()) && report.getSummaries().containsKey(FACEBOOK_NAME)) {
            FKSummaries summaries = report.getSummaries().get(FACEBOOK_NAME);
            if (summaries.getBirthdate() != null && !summaries.getBirthdate().isEmpty()) {
                builder.dob(summaries.getBirthdate());
            } else {
                builder.dob(NOT_AVAILABLE);
            }
            if (summaries.getName() != null && !summaries.getName().isEmpty()) {
                builder.fullName(summaries.getName());
            } else {
                builder.fullName(NOT_AVAILABLE);
            }
        }
        builder.resultMessageText(passed ? "Successfully passed Facebook verification" : "Failed to pass verification via Facebook");
        builder.success(passed);
        return VerificationKycAttempt.builder()
                .verificationResultId(verificationResultId)
                .kycSuccessVerificationResponse(builder.build())
                .build();
    }

    @Override
    public ResponseEntity<String> processFacebookHSReport(String jobId) throws Status540ProviderNotConfiguredException, Status412FailToGetJobDetailsException, UserClientServiceFactoryException, UserNotFoundException, LithiumServiceClientFactoryException, Status459VerificationResultNotFountException {

        UserDocumentStatus udStatus = statusRepository.getOne(jobId);
        if (udStatus != null && udStatus.isComplete()) {
            log.info("HelloSoda FK verification job " + jobId + " already complete");
            return ResponseEntity.ok().build();
        }

        if (udStatus == null) {
            log.error("HelloSoda FK verification job " + jobId + " not found");
            throw new Status412FailToGetJobDetailsException();
        }

        String userGuid = udStatus.getUserGuid();
        String domainName = udStatus.getDomainName();
        Long userId = udStatus.getUserId();
        UserDocumentStatus udStatusCopy = copy(udStatus);

        FKReport report = getFKReportByJobId(udStatus.getJobId(), domainName);

        boolean passed;
        udStatus.setComplete(true);

        if (nonNull(report) && nonNull(report.getScores())) {
            passed = processResult(report.getScores(), userGuid, userId);
            verificationAttemptService.updateVerificationResult(createAttempt(report, passed, udStatus.getKycVerificationResultId()), userGuid);
            udStatus.setReportBody(toJson(report.toString()));
            udStatus.setStatus(STATUS_NAME_COMPLETED);
        }
        statusRepository.save(udStatus);

        addChangeLog(udStatusCopy, udStatus, "Got webhook for + " + udStatus.getJobId() + ". (" + udStatus.getFunctionName() + ") status set to \"" + udStatus.getStatus() + "\"");


        return ResponseEntity.ok().build();
    }

    private void makeUserAgeOnlyVerified(String userGuid, Long userId) throws UserClientServiceFactoryException, UserNotFoundException {
        UserVerificationStatusUpdate.UserVerificationStatusUpdateBuilder updateBuilder = UserVerificationStatusUpdate.builder()
                .userGuid(userGuid)
                .statusId(VerificationStatus.AGE_ONLY_VERIFIED.getId())
                .userId(userId)
                .ageVerified(true);
        userClient.editUserVerificationStatus(updateBuilder.build());
    }

    private boolean processResult(FKScores scores, String userGuid, long userId) throws UserClientServiceFactoryException, UserNotFoundException {
        boolean isSocialFraudPassed = false;
        boolean isScorePassed = false;
        boolean isNameMatchPassed = false;
        boolean isOver18 = false;

        if (scores.getSocialFraud() != null && scores.getSocialFraud().containsKey(FACEBOOK_NAME) &&
                scores.getSocialFraud().get(FACEBOOK_NAME).compareTo(FACEBOOK_SOCIAL_FRAUD_THRESHOLD) < 0) {
            isSocialFraudPassed = true;
        }
        if (scores.getOver18() != null && scores.getOver18() > 0) {
            isOver18 = true;
        }
        if (scores.getIdScore().compareTo(ID_SCORE_THRESHOLD) >= 0) {
            isScorePassed = true;
        }
        if (scores.getIdConfidence() != null && scores.getIdConfidence().containsKey(NAME_MATCH)
                && scores.getIdConfidence().get(NAME_MATCH).compareTo(NAME_MATCH_THRESHOLD) >= 0) {
            isNameMatchPassed = true;
        }

        boolean passed = isSocialFraudPassed && isScorePassed && isNameMatchPassed && isOver18;

        if (passed) {
            makeUserAgeOnlyVerified(userGuid, userId);
        }
        return passed;
    }


    private FKReport getFKReportByJobId(String jobId, String domainName) throws Status540ProviderNotConfiguredException, Status412FailToGetJobDetailsException {
        ProviderConfig config = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);

        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>() {{
            add("Authorization", "Bearer " + config.getProfileBearer());
        }};
        String profileApiUrl = config.getProfileApiUrl();

        ResponseEntity<Object> exchange = restTemplate.exchange(profileApiUrl + "/jobs/" + jobId + "/report", GET, new HttpEntity<>(null, headers), Object.class, new HashMap<>());
        log.debug("Report for jobId:" + jobId + " is:" + exchange.getBody());
        if (exchange.getStatusCodeValue() != 200) {
            log.warn("Fail to get job details: " + exchange.getBody());
            throw new Status412FailToGetJobDetailsException();
        }
        return nonNull(exchange.getBody()) ? mapper.convertValue(exchange.getBody(), FKReport.class) : null;
    }

    private UserDocumentStatus copy(UserDocumentStatus status) {
        return status.toBuilder().build();
    }

    private void addChangeLog(UserDocumentStatus old, UserDocumentStatus documentStatus, String comments) {
        try {
            List<ChangeLogFieldChange> changeLogFieldChanges = changeLogService.copy(
                    documentStatus,
                    old,
                    new String[]{
                            "reportBody", "complete", "status"
                    }
            );
            changeLogService.registerChangesForNotesWithFullNameAndDomain("document.helloSoda.facebook.provider", "edit",
                    old.getUserId(), old.getUserGuid(), null,
                    comments,
                    null, changeLogFieldChanges, Category.ACCOUNT, SubCategory.DOCUMENT_UPLOAD, 1, old.getDomainName());
        } catch (Exception e) {
            log.error("User document status added, but changelog failed. (" + old + ")", e);
        }
    }

}
