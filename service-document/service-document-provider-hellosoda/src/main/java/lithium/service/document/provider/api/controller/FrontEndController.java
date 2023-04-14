package lithium.service.document.provider.api.controller;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.config.LithiumConfigurationProperties;
import lithium.modules.ModuleInfo;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.document.provider.api.exceptions.Status414FailFromHelloSodaServiceException;
import lithium.service.document.provider.api.exceptions.Status540ProviderNotConfiguredException;
import lithium.service.document.provider.entity.UserDocumentStatus;
import lithium.service.document.provider.repository.UserDocumentStatusRepository;
import lithium.service.document.provider.service.HelloSodaDocumentService;
import lithium.service.document.provider.service.HelloSodaProfileFKService;
import lithium.service.document.provider.service.VerificationAttemptService;
import lithium.service.kyc.entities.VerificationResult;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/frontend/api")
@EnableUserApiInternalClientService
public class FrontEndController {

    public static final String PENDING_STATUS_NAME = "pending";
    public static final String FAIL_STATUS_NAME = "fail";
    public static final String IDOCUFY_METHOD_TYPE = "iDocufy";
    private static final String FACEBOOK_METHOD_TYPE = "facebook";

    private final HelloSodaDocumentService helloSodaDocumentService;
    private final LithiumConfigurationProperties config;
    private final UserApiInternalClientService userApiInternalClientService;
    private final ChangeLogService changeLogService;
    private final UserDocumentStatusRepository statusRepository;
    private final ModuleInfo moduleInfo;
    private final HelloSodaProfileFKService fkService;

    private final VerificationAttemptService verificationAttemptService;

    @PostMapping(value = "/submitJob/{jobId}/{sessionId}")
    public ResponseEntity<String> submitJob(@PathVariable String jobId,
                                            @PathVariable String sessionId,
                                            LithiumTokenUtil tokenUtil) throws UserNotFoundException, UserClientServiceFactoryException, Status540ProviderNotConfiguredException, Status414FailFromHelloSodaServiceException, LithiumServiceClientFactoryException {
        String status = "";
        String changeLogComments = "";
        String userGuid = tokenUtil.guid();
        try {
            final String notifyUrl = config.getGatewayPublicUrl() + "/" + moduleInfo.getModuleName() + "/webhook/notify";
            changeLogComments = "Sent prepare job request (" + jobId + ", " + userGuid + ")";
            helloSodaDocumentService.attachUserInformationToJob(userApiInternalClientService.getUserByGuid(userGuid),
                    tokenUtil.domainName(), notifyUrl, sessionId, jobId,
                    tokenUtil.getAuthentication().getOAuth2Request().getClientId());
            changeLogComments = "Sent commit job request (" + jobId + ", " + userGuid + ")";
            helloSodaDocumentService.commitJob(tokenUtil.domainName(), jobId, sessionId);

            status = PENDING_STATUS_NAME;
            log.info("Job committed (" + jobId + "," + userGuid + ")");
            return ResponseEntity.ok(PENDING_STATUS_NAME);
        } catch (Exception e) {
            status = FAIL_STATUS_NAME;
            changeLogComments = changeLogComments + " fail due " + e.getMessage();
            throw e;
        } finally {
            VerificationResult kycVerificationResult = null;
            if (FAIL_STATUS_NAME.equals(status)) {
                kycVerificationResult = verificationAttemptService.registerFailedAttempt(tokenUtil.guid(), tokenUtil.domainName(), jobId, moduleInfo.getModuleName(), IDOCUFY_METHOD_TYPE, changeLogComments);
            } else {
                kycVerificationResult = verificationAttemptService.registerKYCAttempt(tokenUtil.guid(), tokenUtil.domainName(), jobId, moduleInfo.getModuleName(), IDOCUFY_METHOD_TYPE);
            }
            UserDocumentStatus docStatus = new UserDocumentStatus(jobId, status, userGuid, tokenUtil.id(), IDOCUFY_METHOD_TYPE, null, sessionId, tokenUtil.domainName(), false, kycVerificationResult.getId());
            statusRepository.save(docStatus);
            try {
                List<ChangeLogFieldChange> changeLogFieldChanges = changeLogService.copy(docStatus, new UserDocumentStatus(),
                        new String[]{"jobId", "status", "userGuid", "functionName", "reportBody"});

                changeLogService.registerChangesForNotesWithFullNameAndDomain("document.helloSoda.provider", "create",
                        docStatus.getUserId(), docStatus.getUserGuid(), null, changeLogComments,
                        null, changeLogFieldChanges, Category.ACCOUNT, SubCategory.DOCUMENT_UPLOAD, 1, docStatus.getDomainName());
            } catch (Exception e) {
                log.error("User document status added, but changelog failed. (" + docStatus + ")", e);
            }
        }
    }

    @GetMapping("/changelogs/{userId}")
    public @ResponseBody
    Response<ChangeLogs> changeLogs(@PathVariable("userId") Long userId, @RequestParam int p) throws Exception {
        if (statusRepository.findAllByUserId(userId).isEmpty()) {
            log.info("User with id " + userId + " not verified");
            throw new Exception("User with id " + userId + " not verified");
        }
        return changeLogService.listLimited(
                ChangeLogRequest.builder()
                        .entityRecordId(userId)
                        .entities(new String[]{"document.helloSoda.provider"})
                        .page(p)
                        .build()
        );
    }

    @PostMapping("submitJob/facebook/{fkToken}")
    public ResponseEntity<String> submitFKJob(@PathVariable("fkToken") String fkToken,
                                              LithiumTokenUtil tokenUtil) throws Status540ProviderNotConfiguredException, UserClientServiceFactoryException, UserNotFoundException, Status414FailFromHelloSodaServiceException, LithiumServiceClientFactoryException {

        String userGuid = tokenUtil.guid();
        try {
            fkService.submitFKCheck(fkToken, userGuid, tokenUtil.getAuthentication().getOAuth2Request().getClientId(), tokenUtil.id());
            return ResponseEntity.ok(PENDING_STATUS_NAME);
        } catch (Exception e) {
            verificationAttemptService.registerFailedAttempt(tokenUtil.guid(), tokenUtil.domainName(), null, moduleInfo.getModuleName(), FACEBOOK_METHOD_TYPE, "validation fail due " + e.getMessage());
            return ResponseEntity.ok(FAIL_STATUS_NAME);
        }
    }
}
