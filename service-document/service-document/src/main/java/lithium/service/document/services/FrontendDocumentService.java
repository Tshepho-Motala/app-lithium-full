package lithium.service.document.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status405UnsupportedDocumentTypeException;
import lithium.exceptions.Status406OverLimitFileSizeException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.document.client.objects.DocumentInfo;
import lithium.service.document.client.objects.DocumentPurpose;
import lithium.service.document.client.objects.DocumentRequest;
import lithium.service.document.client.objects.mail.DwhNotificationPlaceholdersBuilder;
import lithium.service.document.client.objects.enums.DocumentReviewStatus;
import lithium.service.document.client.objects.mail.DwhTemplate;
import lithium.service.document.client.objects.mail.MailRequest;
import lithium.service.document.data.entities.DocumentFile;
import lithium.service.document.util.DocumentValidatorUtil;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;

@Slf4j
@Service
public class FrontendDocumentService {
    @Autowired
    private DocumentV2Service documentService;

    @Autowired
    protected LithiumServiceClientFactory services;

    @Autowired
    private DwhNotificationService dwhNotificationService;

    @Autowired
    private UserApiInternalClientService userClient;

    @Autowired
    private LimitInternalSystemService limits;

    @Autowired
    private ChangeLogService changeLogService;

    @Autowired
    private DocumentValidatorUtil documentValidatorUtil;

    public Response<DocumentFile> uploadVerificationDocument(String documentType, MultipartFile multipartFile, LithiumTokenUtil tokenUtil) throws
            Status500InternalServerErrorException, Status405UnsupportedDocumentTypeException, Status406OverLimitFileSizeException, IOException {
        if (tokenUtil == null || tokenUtil.username() == null && tokenUtil.domainName() == null) {
            log.error("Failed to upload document, invalid auth token", tokenUtil);
            throw new Status500InternalServerErrorException("Failed to upload document");
        }

        documentValidatorUtil.checkIfDocumentIsValid(multipartFile, documentType, tokenUtil);

        String userGuid = tokenUtil.guid();
        String domainName = tokenUtil.domainName();
        try {
            DocumentRequest request = DocumentRequest.builder()
                    .fileName(multipartFile.getOriginalFilename())
                    .content(multipartFile.getBytes())
                    .mimeType(multipartFile.getContentType())
                    .docPage(0)
                    .documentPurpose(DocumentPurpose.INTERNAL)
                    .documentType(documentType)
                    .domainName(domainName)
                    .userGuid(userGuid)
                    .reviewStatus(DocumentReviewStatus.WAITING)
                    .build();

            DocumentInfo documentInfo = documentService.createAndUploadDocument(request, tokenUtil);
            sendDwhNotification(userGuid, domainName, documentInfo, tokenUtil);

            DocumentFile documentFile = documentService.getDocumentFile(documentInfo.getDocumentFileId());
            Response<DocumentFile> documentInfoResponse = Response.<DocumentFile>builder()
                    .data(documentFile)
                    .status(Response.Status.OK)
                    .build();
            return documentInfoResponse;
        } catch (Exception ex) {
            log.error("Failed to upload document userGuid={}", userGuid, ex);
            throw new Status500InternalServerErrorException("Failed to upload document userGuid=" + userGuid);
        }
    }

    private void sendDwhNotification(String userGuid, String domainName, DocumentInfo documentInfo, LithiumTokenUtil tokenUtil) throws Exception {
        try {
            User user = userClient.getUserByGuid(userGuid);
            Set<Placeholder> dwhPlaceholders = new DwhNotificationPlaceholdersBuilder()
                    .setDomainName(domainName)
                    .setPlayerGuid(userGuid)
                    .setPlayerLink("/#/dashboard/players/" + domainName + "/" + user.getId() + "/summary")
                    .setAccountStatus(user.getStatus().getName())
                    .setVerificationStatus(limits.getVerificationStatusCode(user.getVerificationStatus()))
                    .setAgeVerified(nonNull(user.getAgeVerified()) && user.getAgeVerified() ? "Yes" : "No")
                    .setAddressVerified(nonNull(user.getAddressVerified()) && user.getAddressVerified() ? "Yes" : "No")
                    .setFileName1(documentInfo.getFileName())
                    .setFileLink1(documentInfo.getFileLink())
                    .setFileTimestamp1(documentInfo.getUploadDate().toString())
                    .setDocumentType(documentInfo.getDocumentType())
                    .build();

            dwhNotificationService.sendMail(MailRequest.builder()
                    .domainName(domainName)
                    .userGuid(userGuid)
                    .template(DwhTemplate.UPLOADED_DOCUMENT_TEMPLATE)
                    .placeholders(dwhPlaceholders)
                    .build());
            List<ChangeLogFieldChange> clfc = changeLogService.compare(documentInfo, new DocumentInfo(), new String[]{"id", "fileName", "uploadDate", "reviewStatus", "sensitive", "documentFileId", "typeId", "reviewReasonId", "id", "fileLink", "documentType"});

            changeLogService.registerChangesForNotesWithFullNameAndDomain("user.document", "create", user.getId(), tokenUtil.guid(), tokenUtil, null, null, clfc, Category.DOCUMENTS, SubCategory.DOCUMENT_UPLOAD, 0, user.getDomain().getName());
        } catch (UserNotFoundException | UserClientServiceFactoryException e) {
            log.error("Can't send notification to DWH due " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

}
