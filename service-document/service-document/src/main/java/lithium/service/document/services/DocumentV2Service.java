package lithium.service.document.services;


import lithium.service.document.data.entities.DocumentFile;
import lithium.service.document.data.entities.DocumentType;
import lithium.service.document.data.entities.DocumentV2;
import lithium.service.document.data.entities.Domain;
import lithium.service.document.data.entities.File;
import lithium.service.document.data.entities.Owner;
import lithium.service.document.data.entities.ReviewReason;
import lithium.service.document.data.entities.ReviewStatus;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.document.client.objects.DocumentInfo;
import lithium.service.document.client.objects.DocumentPurpose;
import lithium.service.document.client.objects.DocumentRequest;
import lithium.service.document.client.objects.enums.DocumentReviewStatus;
import lithium.service.document.client.stream.UserDocumentsTriggerStream;
import lithium.service.document.data.objects.TextValue;
import lithium.service.document.data.repositories.DocumentFileRepository;
import lithium.service.document.data.repositories.DocumentTypeRepository;
import lithium.service.document.data.repositories.DocumentV2Repository;
import lithium.service.document.data.repositories.DomainRepository;
import lithium.service.document.data.repositories.ReviewReasonRepository;
import lithium.service.document.data.repositories.ReviewStatusRepository;
import lithium.service.document.exceptions.Status404DocumentNotFoundException;
import lithium.service.document.util.DocumentValidatorUtil;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserDocumentData;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class DocumentV2Service {

    @Autowired
    private DocumentV2Repository documentRepo;
    @Autowired
    private DocumentFileRepository documentFileRepository;
    @Autowired
    private DocumentTypeRepository documentTypeRepository;
    @Autowired
    private ReviewStatusRepository reviewStatusRepository;
    @Autowired
    private ReviewReasonRepository reviewReasonRepository;
    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private OwnerService ownerService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ChangeLogService changeLogService;
    @Autowired
    private UserDocumentsTriggerStream documentsTriggerStream;
    @Autowired
    private UserApiInternalClientService userClient;
    @Autowired
    private DocumentValidatorUtil documentValidatorUtil;
    @Autowired
    private DocumentTypeService documentTypeService;

    public DocumentInfo createAndUploadDocument(DocumentRequest documentRequest, LithiumTokenUtil tokenUtil) throws Exception {

        String domainName = documentRequest.getDomainName();
        String fileName = documentRequest.getFileName();
        String documentTypeName = documentRequest.getDocumentType();
        byte[] content = documentRequest.getContent();
        String userGuid = documentRequest.getUserGuid();
        DocumentPurpose documentPurpose = documentRequest.getDocumentPurpose();
        Long documentTypeId = documentTypeService.resolveDocumentType(documentTypeName, documentPurpose, domainName).getId();
        DocumentReviewStatus reviewStatus = documentRequest.getReviewStatus();

        return uploadAndCreateDocument(content, userGuid, documentTypeId, reviewStatus.getName(), null, false, fileName, tokenUtil);

    }

    public List<DocumentInfo> listPerUser(String guid, boolean includeSensitive) {

        List<DocumentV2> documents = includeSensitive ? documentRepo.findAllByOwnerGuidAndDeletedFalse(guid) : documentRepo.findAllByOwnerGuidAndSensitiveFalseAndDeletedFalse(guid);

        return documents.stream()
                .map(this::convertToDocumentInfo).collect(Collectors.toList());
    }

    private DocumentInfo convertToDocumentInfo(DocumentV2 document) {
        DocumentType documentType=document.getDocumentType();
        return DocumentInfo.builder()
                .id(document.getId())
                .reviewStatus(document.getReviewStatus().getName().toUpperCase())
                .sensitive(documentType!=null&&documentType.isTypeSensitive()?true:document.isSensitive())
                .typeId(document.getDocumentType().getId())
                .documentType(document.getDocumentType().getType())
                .reviewReasonId(Optional.ofNullable(document.getReviewReason()).map(ReviewReason::getId).orElse(null))
                .fileName(document.getFileName())
                .uploadDate(document.getDocumentFile().getUploadDate())
                .documentFileId(document.getDocumentFile().getId())
                .build();
    }

    public List<TextValue> availableReviewReasons(String domainName) {
        return StreamSupport.stream(reviewReasonRepository.findAllByDomainNameAndEnabledTrue(domainName).spliterator(), false)
                .map(reviewReason -> new TextValue(reviewReason.getName(), reviewReason.getId()))
                .collect(Collectors.toList());
    }

    public void updateDocument(DocumentInfo documentInfo, LithiumTokenUtil tokenUtil) throws Status404DocumentNotFoundException {
        DocumentV2 document = documentRepo.findById(documentInfo.getId())
                .orElseThrow(() -> {
                    log.error("Can't update document, id: " + documentInfo.getId() + ", due not found");
                    return new Status404DocumentNotFoundException("Not found document, id: " + documentInfo.getId());
                });
        DocumentV2 oldDocument = DocumentV2.builder()
                .id(document.getId())
                .domain(document.getDomain())
                .owner(document.getOwner())
                .documentType(document.getDocumentType())
                .reviewStatus(document.getReviewStatus())
                .reviewReason(document.getReviewReason())
                .sensitive(document.isSensitive())
                .deleted(document.isDeleted())
                .version(document.getVersion()).build();
        DocumentType documentType=documentTypeRepository.findOne(documentInfo.getTypeId());
        document.setDocumentType(documentType);
        document.setDocumentType(documentTypeRepository.findOne(documentInfo.getTypeId()));
        document.setReviewStatus(reviewStatusRepository.findOrCreateByName(documentInfo.getReviewStatus(), ReviewStatus::new));
        document.setReviewReason(Optional.ofNullable(documentInfo.getReviewReasonId()).map(id -> reviewReasonRepository.findOne(id)).orElse(null));
        document.setSensitive(documentType!=null&& documentType.isTypeSensitive()?true:documentInfo.isSensitive());
        saveAndSyncDocument(document);

        try {
            User user = userClient.getUserByGuid(document.getOwner().getGuid());

            String message = "Document V2 updated (id:" + document.getId() + ", domain:" + document.getDomain().getName() + ", owner:" + document.getOwner().getGuid() +
                    ", version:" + document.getVersion() + ", author:" + user.guid() + ")";
            log.info(message);

            List<ChangeLogFieldChange> clfc = changeLogService.compare(document, oldDocument,
                    new String[]{"id", "domain", "owner", "documentType", "reviewStatus", "reviewReason", "sensitive", "deleted", "version"});

            changeLogService.registerChangesForNotesWithFullNameAndDomain("user.document", "edit", user.getId(), user.guid(),
                    tokenUtil, message, null, clfc, Category.DOCUMENTS, SubCategory.DOCUMENT_UPLOAD, 0,
                    document.getDomain().getName());
        } catch (UserClientServiceFactoryException | Exception e) {
            log.error("Can't create document upload changelog " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public void deleteDocument(Long id , LithiumTokenUtil tokenUtil) throws Status404DocumentNotFoundException {
        DocumentV2 document = documentRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Can't delete document, id: " + id + ", due not found");
                    return new Status404DocumentNotFoundException("Not found document, id: " + id);
                });
        DocumentV2 oldDocument = DocumentV2.builder()
                .id(document.getId())
                .domain(document.getDomain())
                .owner(document.getOwner())
                .documentType(document.getDocumentType())
                .reviewStatus(document.getReviewStatus())
                .reviewReason(document.getReviewReason())
                .sensitive(document.isSensitive())
                .deleted(document.isDeleted())
                .version(document.getVersion()).build();
        document.setDeleted(true);
        saveAndSyncDocument(document);

        try {
            User user = userClient.getUserByGuid(document.getOwner().getGuid());

            String message = "Document V2 deleted (id:" + document.getId() + ", domain:" + document.getDomain().getName() + ", owner:" + document.getOwner().getGuid() +
                    ", version:" + document.getVersion() + ", author:" + user.guid() + ")";
            log.info(message);

            List<ChangeLogFieldChange> clfc = changeLogService.compare(document, oldDocument,
                    new String[]{"id", "domain", "owner", "documentType", "reviewStatus", "reviewReason", "sensitive", "deleted", "version"});

            changeLogService.registerChangesForNotesWithFullNameAndDomain("user.document", "delete", user.getId(), user.guid(),
                    tokenUtil, message, null, clfc, Category.DOCUMENTS, SubCategory.DOCUMENT_UPLOAD, 0,
                    document.getDomain().getName());
        } catch (UserClientServiceFactoryException | Exception e) {
            log.error("Can't create document upload changelog " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public DocumentInfo uploadAndCreateDocument(MultipartFile multipartFile, String ownerGuid, Long documentTypeId, String reviewStatusName, Long reviewReasonId, boolean sensitive, LithiumTokenUtil tokenUtil) throws Exception {
        return uploadAndCreateDocument(multipartFile.getBytes(), ownerGuid, documentTypeId, reviewStatusName, reviewReasonId, sensitive, multipartFile.getOriginalFilename(), tokenUtil);
    }

    public DocumentInfo uploadAndCreateDocument(byte[] fileContent, String ownerGuid, Long documentTypeId, String reviewStatusName, Long reviewReasonId, boolean sensitive, String fileName, LithiumTokenUtil tokenUtil) throws Exception {
        documentValidatorUtil.checkIfDocumentIsValid(fileContent, fileName, tokenUtil);

        Domain domain = domainRepository.findOrCreateByName(ownerGuid.split("/")[0], Domain::new);
        String contentType = documentValidatorUtil.resolveFileType(fileName,fileContent,ownerGuid);

        Owner owner = ownerService.findOrCreateOwner(ownerGuid);
        ReviewStatus reviewStatus = reviewStatusRepository.findOrCreateByName(reviewStatusName, ReviewStatus::new);
        DocumentType documentType = documentTypeRepository.findOne(documentTypeId);

        File file = fileService.saveFile(fileContent, contentType, fileName);

        DocumentFile documentFile = documentFileRepository.save(DocumentFile.builder()
                .deleted(false)
                .file(file)
                .documentPage(0)
                .build());

        DocumentV2 document = saveAndSyncDocument(DocumentV2.builder()
                .domain(domain)
                .owner(owner)
                .documentType(documentType)
                .reviewStatus(reviewStatus)
                .reviewReason(Optional.ofNullable(reviewReasonId).map(id -> reviewReasonRepository.findOne(id)).orElse(null))
                .documentFile(documentFile)
                .deleted(false)
                .sensitive(documentType!=null&&documentType.isTypeSensitive()?true:sensitive)
                .fileName(fileName)
                .build());

        documentFile.setDocumentId(document.getId());
        documentFileRepository.save(documentFile);
        DocumentInfo documentInfo = convertToDocumentInfo(document);

        try {
            User user = userClient.getUserByGuid(ownerGuid);
            documentInfo.setFileLink("/#/dashboard/players/" + domain.getName() + "/" + user.getId() + "/document?documentFileId=" + documentInfo.getDocumentFileId());

            String message = "Document V2 Document upload success (id:" + document.getId() + ", domain:" + document.getDomain().getName() + ", owner:" + document.getOwner().getGuid() +
                    ", version:" + document.getVersion() + ", author:" + user.guid() + ")";
            log.info(message);

            List<ChangeLogFieldChange> clfc = changeLogService.compare(document, new DocumentV2(),
                    new String[]{"id", "domain", "owner", "documentType", "reviewStatus", "reviewReason", "sensitive", "deleted", "version"});

            changeLogService.registerChangesForNotesWithFullNameAndDomain("user.document", "create", user.getId(), tokenUtil.guid(),
                    tokenUtil, message, null, clfc, Category.DOCUMENTS, SubCategory.DOCUMENT_UPLOAD, 0,
                    document.getDomain().getName());
        } catch (UserNotFoundException | UserClientServiceFactoryException e) {
            log.error("Can't create document upload changelog " + ExceptionUtils.getRootCauseMessage(e));
        }
        return documentInfo;
    }

    public DocumentFile getDocumentFile(Long documentFileId) throws Status404DocumentNotFoundException {
        DocumentFile documentFile = documentFileRepository.findById(documentFileId)
                .orElseThrow(()->{
                    log.error("Can't retrieve document file, id: " + documentFileId + ", due not found");
                    return new Status404DocumentNotFoundException("Not found document file, id: " + documentFileId);
                });
        updateFileName(documentFile);
        return documentFile;
    }

    private void updateFileName(DocumentFile documentFile) {
        documentRepo.findById(documentFile.getDocumentId())
                .ifPresent(documentV2 -> {
                    if (Objects.equals(documentFile.getId(), documentV2.getDocumentFile().getId())) {
                        documentFile.getFile().setName(documentV2.getFileName());
                    }
                });
    }

    private DocumentV2 saveAndSyncDocument(DocumentV2 document) {
        DocumentV2 doc = saveDocument(document);
        addDocumentToQueue(doc);
        return doc;
    }

	private void addDocumentToQueue(DocumentV2 document) {
		try {
			documentsTriggerStream.trigger(
				UserDocumentData.builder()
					.documentId(document.getId())
					.guid(document.getOwner().getGuid())
					.statusId(document.getReviewStatus().getId())
					.statusName(document.getReviewStatus().getName())
					.sensitive(document.isSensitive())
					.deleted(document.isDeleted())
					.build()
				);
		} catch (Exception ex) {
			log.error("Add UserDocumentData to user documents stream failed: " + ex.getMessage(), ex);
		}
	}
    public boolean isV2DocumentFile(DocumentFile documentFileToCheck) {
        return documentRepo.findById(documentFileToCheck.getDocumentId())
                .map(DocumentV2::getDocumentFile)
                .map(DocumentFile::getId)
                .filter(id -> id.equals(documentFileToCheck.getId()))
                .isPresent();
    }

    public boolean isDocV2Exist(Owner owner, File file){
        return documentRepo.findByOwnerAndDocumentFileFile(owner, file).size() > 0;
    }

    public DocumentV2 saveDocument(DocumentV2 data) {
        return documentRepo.save(data);
    }
}
