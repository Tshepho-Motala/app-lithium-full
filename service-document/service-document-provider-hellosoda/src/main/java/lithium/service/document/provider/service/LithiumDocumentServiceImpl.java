package lithium.service.document.provider.service;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.document.client.DocumentClientService;
import lithium.service.document.client.EnableDocumentClient;
import lithium.service.document.client.objects.DocumentInfo;
import lithium.service.document.client.objects.DocumentPurpose;
import lithium.service.document.client.objects.DocumentRequest;
import lithium.service.document.client.objects.mail.DwhNotificationPlaceholdersBuilder;
import lithium.service.document.client.objects.enums.DocumentReviewStatus;
import lithium.service.document.client.objects.mail.DwhTemplate;
import lithium.service.document.client.objects.mail.MailRequest;
import lithium.service.document.provider.api.schema.ReportResponse;
import lithium.service.document.provider.entity.HelloSodaStatus;
import lithium.service.document.provider.entity.UserDocumentStatus;
import lithium.service.document.provider.repository.UserDocumentStatusRepository;
import lithium.service.kyc.client.objects.VerificationKycAttempt;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserVerificationStatusUpdate;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static lithium.service.document.provider.Utils.toJson;

@Service
@Slf4j
@EnableDocumentClient
@EnableUserApiInternalClientService
@AllArgsConstructor
public class LithiumDocumentServiceImpl implements LithiumDocumentService {

    private final HelloSodaDocumentService helloSodaDocumentService;
    private final DocumentClientService documentServiceClient;
    private final UserDocumentStatusRepository statusRepository;
    private final ChangeLogService changeLogService;
    private final UserApiInternalClientService userClient;
    private final LithiumConfigurationProperties config;
    private final LimitInternalSystemService limits;
    private final VerificationAttemptService verificationAttemptService;

    @Override
    public ResponseEntity<String> updateDocument(String jobId) throws Exception, UserClientServiceFactoryException, UserNotFoundException {
        UserDocumentStatus udStatus = statusRepository.getOne(jobId);
        if (udStatus.isComplete()) {
            log.info("Verification job " + jobId + " already complete");
            return ResponseEntity.ok().build();
        }

        String userGuid = udStatus.getUserGuid();
        String sessionId = udStatus.getSessionId();
        String domainName = udStatus.getDomainName();
        Long userId = udStatus.getUserId();
        UserDocumentStatus udStatusCopy = copy(udStatus);

        ReportResponse reportResponse = helloSodaDocumentService.getReportByJobId(jobId, domainName);
        HelloSodaStatus status = resolveStatus(reportResponse, userGuid);

        udStatus.setStatus(status.name());
        udStatus.setFunctionName(reportResponse.getIdcheck().getClassification().getClassName());
        udStatus.setReportBody(toJson(reportResponse));

        udStatus = statusRepository.saveAndFlush(udStatus);

        addChangeLog(udStatusCopy, udStatus, "Got webhook for + " + udStatus.getJobId() + ". (" + udStatus.getFunctionName() + ") status set to \"" + udStatus.getStatus() + "\"");

        UserVerificationStatusUpdate.UserVerificationStatusUpdateBuilder updateBuilder = new UserVerificationStatusUpdate().builder()
                .userId(userId)
                .comment("Based on the verification of documents by the helloSoda service (" + udStatus.getFunctionName() + ")")
                .userGuid(userGuid);

        List<DocumentInfo> uploadDocumentsList = downloadAndSaveUserImages(reportResponse.getIdcheck().getBackMetrics(), sessionId, jobId, status, domainName, userGuid);
        log.info("User " + userGuid + " documents uploaded: " + uploadDocumentsList.stream().map(documentInfo -> documentInfo.getFileName()).collect(Collectors.joining(", ")));

        boolean sendDwhNotification = true;
        if (HelloSodaStatus.completeVerified.equals(status)) {
            userClient.editUserVerificationStatus(updateBuilder.statusId(VerificationStatus.EXTERNALLY_VERIFIED.getId()).ageVerified(true).addressVerified(true).build());
            sendDwhNotification = false;
            log.info("User " + userGuid + " age and address verified");
        } else if (HelloSodaStatus.ageVerified.equals(status)) {
            UserVerificationStatusUpdate.UserVerificationStatusUpdateBuilder statusBuilder = updateBuilder.statusId(VerificationStatus.AGE_ONLY_VERIFIED.getId()).ageVerified(true);
            if (userClient.getUserByGuid(userGuid).getAddressVerified()) {
                statusBuilder.statusId(VerificationStatus.EXTERNALLY_VERIFIED.getId());
                sendDwhNotification = false;
            }
            userClient.editUserVerificationStatus(statusBuilder.build());
            log.info("User " + userGuid + " age verified");
        }

        verificationAttemptService.updateVerificationResult(createAttempt(reportResponse, udStatus.getKycVerificationResultId(), HelloSodaStatus.completeVerified.name().equals(status)), userGuid);

        if (sendDwhNotification) {
            sendDwhNotification(domainName, userGuid, uploadDocumentsList);
        }

        udStatusCopy = copy(udStatus);
        udStatus.setComplete(true);
        udStatus = statusRepository.save(udStatus);
        addChangeLog(udStatusCopy, udStatus, "Verification job complete");

        return ResponseEntity.ok().build();
    }

    private void sendDwhNotification(String domainName, String userGuid, List<DocumentInfo> uploadDocumentsList) throws UserClientServiceFactoryException, UserNotFoundException, Exception {
        User user = userClient.getUserByGuid(userGuid);
        DocumentInfo frontSideFile = uploadDocumentsList.get(0);
        DwhNotificationPlaceholdersBuilder placeholders = new DwhNotificationPlaceholdersBuilder()
                .setDomainName(domainName)
                .setPlayerGuid(user.getGuid())
                .setPlayerLink("/#/dashboard/players/" + domainName + "/" + user.getId() + "/summary")
                .setAccountStatus(user.getStatus().getName())
                .setVerificationStatus(limits.getVerificationStatusCode(user.getVerificationStatus()))
                .setAgeVerified(nonNull(user.getAgeVerified()) && user.getAgeVerified() ? "Yes" : "No")
                .setAddressVerified(nonNull(user.getAddressVerified()) && user.getAddressVerified() ? "Yes" : "No")
                .setFileName1(frontSideFile.getFileName())
                .setFileLink1(frontSideFile.getFileLink())
                .setFileTimestamp1(frontSideFile.getUploadDate().toString())
                .setDocumentType(frontSideFile.getDocumentType());
        if (uploadDocumentsList.size() > 1) {
            DocumentInfo backSideFile = uploadDocumentsList.get(1);
            placeholders
                    .setFileName2(backSideFile.getFileName())
                    .setFileLink2(backSideFile.getFileLink())
                    .setFileTimestamp2(backSideFile.getUploadDate().toString());
        }
        documentServiceClient.sendDwhNotification(MailRequest.builder()
                .domainName(domainName)
                .userGuid(user.getGuid())
                .template(DwhTemplate.UPLOADED_DOCUMENT_TEMPLATE)
                .placeholders(placeholders.build())
                .build());
    }

	private HelloSodaStatus resolveStatus(ReportResponse reportResponse, String userGuid) {

		Optional<LocalDate> expirationDate = getExpirationDate(reportResponse);

		if (isExpired(expirationDate)) {
			expirationDate.ifPresent(date -> log.warn("Document expired, expiration date: " + date + ", guid: " + userGuid));
			return HelloSodaStatus.fail;
		}

		if (hasDocumentDecision(reportResponse) && hasAddressDecision(reportResponse)) {
			return HelloSodaStatus.completeVerified;
		}
		if (hasDocumentDecision(reportResponse)) {
			return HelloSodaStatus.ageVerified;
		}

		return HelloSodaStatus.fail;
	}

	private boolean hasDocumentDecision(ReportResponse reportResponse) {
		return Optional.ofNullable(reportResponse)
				.map(ReportResponse::getBespoke)
				.map(ReportResponse.Bespoke::getDocumentDecision)
				.map("pass"::equals)
				.orElse(false);
	}

	private boolean hasAddressDecision(ReportResponse reportResponse) {
		return Optional.ofNullable(reportResponse)
				.map(ReportResponse::getBespoke)
				.map(ReportResponse.Bespoke::getAddressDecision)
				.map("pass"::equals)
				.orElse(false);
	}

	private boolean isExpired(Optional<LocalDate> date) {
		return date
				.map(expirationDate -> LocalDate.now().isAfter(expirationDate))
				.orElse(false);
	}

	private Optional<LocalDate> getExpirationDate(ReportResponse reportResponse) {
		try {
			return Optional.ofNullable(reportResponse)
					.map(ReportResponse::getIdcheck)
					.map(ReportResponse.IdCheck::getDocument)
					.map(ReportResponse.IdCheck.Document::getExpirationDate)
					.map(date -> LocalDate.parse(date, DateTimeFormatter.ISO_DATE));
		} catch (Exception ex) {
			log.warn("Can't check document expiration date response: " + reportResponse);
		}
		return Optional.empty();
	}

	private VerificationKycAttempt createAttempt(ReportResponse report, long verificationResultId, boolean statusVerifiedSuccess) {

        KycSuccessVerificationResponse.KycSuccessVerificationResponseBuilder builder = KycSuccessVerificationResponse.builder();
        boolean passed = false;
        if (nonNull(report.getIdcheck())) {
            passed = "Passed".equalsIgnoreCase(report.getIdcheck().getResult());
            if (nonNull(report.getIdcheck().getBiographic())) {
                ReportResponse.IdCheck.Biographic biographic = report.getIdcheck().getBiographic();
                builder
                        .fullName(biographic.getFullName())
                        .dob(biographic.getBirthDate())
                        .countryOfBirth(biographic.getBirthPlace())
                        .address(biographic.getAddress());
            }
        }
        if (nonNull(report.getBespoke())) {
            builder
                    .documentDecision(report.getBespoke().getDocumentDecision())
                    .addressDecision(report.getBespoke().getAddressDecision());
        }



        builder.resultMessageText("iDocufy verification passed: " + passed + ", user verified: " + statusVerifiedSuccess);
        builder.success(statusVerifiedSuccess);
        return VerificationKycAttempt.builder()
                .verificationResultId(verificationResultId)
                .kycSuccessVerificationResponse(builder.build())
                .build();
    }

    @Override
    public List<DocumentInfo> downloadAndSaveUserImages(ReportResponse.IdCheck.Metrics backMetrics, String sessionId, String jobId, HelloSodaStatus status, String domainName, String userGuid) throws Exception {
        List<DocumentInfo> list = new ArrayList<>();
        byte[] frontSideImage = helloSodaDocumentService.getFrontSideImage(sessionId, domainName);
        ReportResponse reportByJobId = helloSodaDocumentService.getReportByJobId(jobId, domainName);
        String documentType = reportByJobId.getIdcheck().getClassification().getClassName();
        DocumentReviewStatus reviewStatus = HelloSodaStatus.fail.equals(status) ? DocumentReviewStatus.INVALID : DocumentReviewStatus.VALID;

        DocumentRequest request = DocumentRequest.builder()
                .fileName("front-" + jobId + ".jpeg")
                .content(frontSideImage)
                .mimeType("image/jpeg")
                .docPage(1)
                .documentPurpose(DocumentPurpose.INTERNAL)
                .documentType(documentType)
                .domainName(domainName)
                .userGuid(userGuid)
                .reviewStatus(reviewStatus)
                .build();
        DocumentInfo documentInfo = documentServiceClient.createAndUploadDocument(request).getData();
        list.add(documentInfo);
        if (backMetrics != null
                && backMetrics.getGlareMetric() != null
                && backMetrics.getHorizontalResolution() != null
                && backMetrics.getSharpnessMetric() != null
                && backMetrics.getVerticalResolution() != null
        ) {
            byte[] backSideImage = helloSodaDocumentService.getBackSideImage(sessionId, domainName);
            DocumentRequest requestBackSide = DocumentRequest.builder()
                    .fileName("back-" + jobId + ".jpeg")
                    .content(backSideImage)
                    .mimeType("image/jpeg")
                    .docPage(2)
                    .documentPurpose(DocumentPurpose.INTERNAL)
                    .documentType(documentType)
                    .domainName(domainName)
                    .userGuid(userGuid)
                    .reviewStatus(reviewStatus)
                    .build();
            list.add(documentServiceClient.createAndUploadDocument(requestBackSide).getData());
        }
        return list;
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
                            "status", "functionName", "reportBody", "complete"
                    }
            );
            changeLogService.registerChangesForNotesWithFullNameAndDomain("document.helloSoda.provider", "edit",
                    old.getUserId(), old.getUserGuid(), null,
                    comments,
                    null, changeLogFieldChanges, Category.ACCOUNT, SubCategory.DOCUMENT_UPLOAD, 1, old.getDomainName());
        } catch (Exception e) {
            log.error("User document status added, but changelog failed. (" + old + ")", e);
        }
    }
}
