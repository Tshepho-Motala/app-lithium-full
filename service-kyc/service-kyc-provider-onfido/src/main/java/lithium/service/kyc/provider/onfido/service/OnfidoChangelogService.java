package lithium.service.kyc.provider.onfido.service;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.modules.ModuleInfo;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.kyc.client.KycResultsClient;
import lithium.service.kyc.client.objects.VerificationKycAttempt;
import lithium.service.kyc.entities.VerificationResult;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.kyc.provider.onfido.entitites.OnfidoCheck;
import lithium.service.kyc.provider.onfido.exceptions.Status414NotFoundOnfidoCheckException;
import lithium.service.kyc.provider.onfido.objects.OnfidoAttemptCheck;
import lithium.service.kyc.provider.onfido.repositories.OnfidoCheckRepository;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@AllArgsConstructor
@Slf4j
public class OnfidoChangelogService {

    private final ModuleInfo moduleInfo;
    private final UserApiInternalClientService userApiInternalClientService;
    private final ChangeLogService changeLogService;
    public static final String NOT_AVAILABLE = "Not available";
    private final LithiumServiceClientFactory services;
    private final OnfidoCheckRepository onfidoCheckRepository;

    public void registerInitialAttempt(OnfidoAttemptCheck attemptCheck) {
        registerInitialAttempt(attemptCheck, moduleInfo.getModuleName());
        try {
            User user = userApiInternalClientService.getUserByGuid(attemptCheck.getUserGuid());
            List<ChangeLogFieldChange> changeLogFieldChanges = changeLogService.copy(attemptCheck, new OnfidoAttemptCheck(),
                    new String[]{"checkId", "applicantId", "status", "userGuid", "reportNames", "resultsUri", "documentReports"});

            changeLogService.registerChangesForNotesWithFullNameAndDomain("kyc.onfido.provider", "create",
                    user.getId(), attemptCheck.getUserGuid(), null, attemptCheck.getComment(),
                    null, changeLogFieldChanges, Category.ACCOUNT, SubCategory.KYC, 1, attemptCheck.getDomainName());
        } catch (Throwable e) {
            log.error("Can't update changelog related registration initial Onfido attempt(" + attemptCheck + ") due " + e.getMessage(), e);
        }

    }

    public void registerFinishAttempt(Long kycResultId, OnfidoAttemptCheck attemptCheck) {
        updateVerificationResult(kycResultId, attemptCheck);
        try {
            User user = userApiInternalClientService.getUserByGuid(attemptCheck.getUserGuid());
            List<ChangeLogFieldChange> changeLogFieldChanges = changeLogService.copy(attemptCheck, new OnfidoAttemptCheck(),
                    new String[]{"checkId", "applicantId", "status", "userGuid", "reportNames", "resultsUri", "documentReports"});

            changeLogService.registerChangesForNotesWithFullNameAndDomain("kyc.onfido.provider", "edit",
                    user.getId(), attemptCheck.getUserGuid(), null, attemptCheck.getComment(),
                    null, changeLogFieldChanges, Category.ACCOUNT, SubCategory.KYC, 1, attemptCheck.getDomainName());
        } catch (Throwable e) {
            log.error("Can't update changelog related registration initital Onfido attempt(" + attemptCheck + ") due " + e.getMessage(), e);
        }

    }

    public void registerInitialAttempt(OnfidoAttemptCheck attemptCheck, String providerName) {
        try {
            KycResultsClient kycResultsClient = services.target(KycResultsClient.class, "service-kyc", true);

            KycSuccessVerificationResponse verificationResponse = KycSuccessVerificationResponse.builder()
                    .createdOn(new Date())
                    .success(false)
                    .manual(true)
                    .address(NOT_AVAILABLE)
                    .providerRequestId(attemptCheck.getCheckId())
                    .countryOfBirth(NOT_AVAILABLE)
                    .dobYearOnly(false)
                    .phoneNumber(NOT_AVAILABLE)
                    .resultMessageText(attemptCheck.getComment())
                    .build();

            VerificationKycAttempt attempt = VerificationKycAttempt.builder()
                    .userGuid(attemptCheck.getUserGuid())
                    .domainName(attemptCheck.getDomainName())
                    .providerName(providerName)
                    .methodName(attemptCheck.getReportNames().toString())
                    .kycSuccessVerificationResponse(verificationResponse)
                    .build();

            log.debug("KYC Onfido verification attempt:" + attempt + " added for user " + attemptCheck.getUserGuid());
            VerificationResult result = kycResultsClient.addVerificationResult(attempt).getData();
            updateKycVerificationResultId(attemptCheck.getCheckId(), result.getId());
        } catch (Exception e) {
            log.error("Can't register Onfido verification attempt (" + attemptCheck.getUserGuid() + ", " + attemptCheck.getCheckId() + ") due " + e.getMessage(), e);
        }
    }

    public void updateVerificationResult(Long kycResultId, OnfidoAttemptCheck attemptCheck) {
        try {
            log.debug("Requested attempt to update kyc verification result for user: " + attemptCheck.getUserGuid());
            KycResultsClient kycResultsClient = services.target(KycResultsClient.class, "service-kyc", true);

            KycSuccessVerificationResponse kycVerificationResponse = attemptCheck.getResponse();
            if (isNull(kycVerificationResponse)) {
                kycVerificationResponse = KycSuccessVerificationResponse.builder()
                        .build();
            }
            if (isNull(kycVerificationResponse.getResultMessageText())) {
                kycVerificationResponse.setResultMessageText(attemptCheck.getComment());
            }

            VerificationKycAttempt attempt = VerificationKycAttempt.builder()
                    .userGuid(attemptCheck.getUserGuid())
                    .domainName(attemptCheck.getDomainName())
                    .verificationResultId(kycResultId)
                    .kycSuccessVerificationResponse(kycVerificationResponse)
                    .build();

            kycResultsClient.updVerificationResult(attempt);
        } catch (Exception e) {
            log.error("Can't update kyc verification result (" + attemptCheck.getUserGuid() + ", " + attemptCheck.getCheckId() + ") due " + e.getMessage(), e);
        }
    }

    private void updateKycVerificationResultId(String checkId, Long kycResultId) throws Status414NotFoundOnfidoCheckException {
        OnfidoCheck onfidoCheck = onfidoCheckRepository.findByCheckId(checkId)
                .orElseThrow(() -> new Status414NotFoundOnfidoCheckException("Can't find Onfido check"));
        onfidoCheck.setKycVerificationResultId(kycResultId);
        onfidoCheckRepository.save(onfidoCheck);
    }
}
