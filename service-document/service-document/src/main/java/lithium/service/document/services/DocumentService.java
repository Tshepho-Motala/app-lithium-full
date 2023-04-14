package lithium.service.document.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.document.client.objects.DocumentInfo;
import lithium.service.document.client.objects.DocumentPurpose;
import lithium.service.document.client.objects.DocumentRequest;
import lithium.service.document.client.objects.enums.AuthorType;
import lithium.service.document.client.objects.enums.DocumentReviewStatus;
import lithium.service.document.client.objects.enums.DocumentStatus;
import lithium.service.document.client.objects.enums.DocumentType;
import lithium.service.document.exceptions.Status404DocumentNotFoundException;
import lithium.service.document.util.DocumentValidatorUtil;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.document.data.entities.AuthorService;
import lithium.service.document.data.entities.Document;
import lithium.service.document.data.entities.DocumentFile;
import lithium.service.document.data.entities.DocumentLabelValue;
import lithium.service.document.data.entities.File;
import lithium.service.document.data.entities.Function;
import lithium.service.document.data.entities.LabelValue;
import lithium.service.document.data.entities.Owner;
import lithium.service.document.data.repositories.DocumentFileRepository;
import lithium.service.document.data.repositories.DocumentLabelValueRepository;
import lithium.service.document.data.repositories.DocumentRepository;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Deprecated
@Service
@Slf4j
public class DocumentService {

	@Autowired private OwnerService ownerService;
	@Autowired private AuthorServiceService authorServiceService;
	@Autowired private FunctionService functionService;
	@Autowired private StatusService statusService;
	@Autowired private DocumentFileRepository documentFileRepo;
	@Autowired private FileService fileService;
	@Autowired private LabelValueService labelValueService;
	@Autowired private DocumentLabelValueRepository documentLabelValueRepository;
	@Autowired private DocumentRepository documentRepo;
	@Autowired private ModelMapper mapper;
	@Autowired private ChangeLogService changeLogService;
	@Autowired private UserApiInternalClientService userClient;
	@Autowired private DocumentValidatorUtil documentValidatorUtil;
	@Autowired private LithiumConfigurationProperties config;
	@Autowired private DocumentV2Service documentV2Service;
	@Autowired private CachingDomainClientService cachingDomainClientService;
	@Autowired private DocumentTypeService documentTypeService;


	public void labels(long documentId, String[] labels, boolean updateOnly, boolean addOnly) {

		List<DocumentLabelValue> dlvList = documentLabelValueRepository.findByDocumentId(documentId);

		if (labels != null) {
			for (String labelAndValue: labels) {
				String[] labelAndValueSplit = labelAndValue.split("=");
				if (labelAndValueSplit.length == 2) {
					String label = labelAndValueSplit[0];
					String value = labelAndValueSplit[1];
					DocumentLabelValue glv = label(documentId, label, value, addOnly);
					dlvList.remove(glv);
				}
			}
		}

		if(!updateOnly) {
			for(DocumentLabelValue glv: dlvList) {
				glv.setDeleted(true);
				documentLabelValueRepository.save(glv);
			}
		}
	}

	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	public DocumentLabelValue label(
			@PathVariable Long documentId,
			@RequestParam String key,
			@RequestParam String value,
			boolean addOnly) {

		DocumentLabelValue currentdlv = documentLabelValueRepository.findByDocumentIdAndLabelValueLabelNameAndDeletedFalse(documentId, key);

		if(currentdlv != null) {
			if(currentdlv.getLabelValue().getValue().contentEquals(value) || addOnly) {
				return currentdlv;
			}

			currentdlv.setDeleted(true);
			documentLabelValueRepository.save(currentdlv);
		}

		LabelValue lv = labelValueService.findOrCreate(key, value);

		DocumentLabelValue dlv = documentLabelValueRepository.findByDocumentIdAndLabelValueId(documentId, lv.getId());
		if(dlv == null) {
			dlv = DocumentLabelValue.builder()
					.documentId(documentId)
					.labelValue(lv)
					.build();

			documentLabelValueRepository.save(dlv);
		} else if (dlv.isDeleted()) {
			dlv.setDeleted(false);
			documentLabelValueRepository.save(dlv);
		}
		return dlv;
	}

	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	public Document createDocument(String name, String statusString, String documentFunction, String ownerGuid, String authorServiceName, String authorGuid) throws Exception {

		AuthorService authorService = authorServiceService.findOrCreateAuthorService(authorServiceName);
		Owner owner = ownerService.findOrCreateOwner(ownerGuid);
		Function function = functionService.findOrCreateFunction(documentFunction, authorService);
		lithium.service.document.data.entities.Status status = statusService.findOrCreateStatus(statusString, authorService);

		Document document = Document.builder()
				.archived(false)
				.deleted(false)
				.authorService(authorService)
				.owner(owner)
				.status(status)
				.function(function)
				.name(name)
				.uuid(UUID.randomUUID().toString())
				.build();

		document = documentRepo.save(document);

		try {
			User user = userClient.getUserByGuid(ownerGuid);

			String message = "Document created (id:" + document.getId() + ", domain:" + user.getDomain().getName() + ", owner:" + document.getOwner().getGuid() +
					", version:" + document.getVersion() + ", author:" + user.guid() + ")";
			log.info(message);

			List<ChangeLogFieldChange> clfc = changeLogService.compare(document, new Document(),
					new String[]{"archived", "deleted", "status", "function", "name", "authorService", "owner", "id", "version", "uuid"});

			changeLogService.registerChangesForNotesWithFullNameAndDomain("user.document", "create", user.getId(), authorGuid,
					null, message, null, clfc, Category.DOCUMENTS, SubCategory.DOCUMENT_UPLOAD, 0,
					user.getDomain().getName());
		} catch (UserNotFoundException | UserClientServiceFactoryException e) {
			log.error("Can't create document upload changelog " + ExceptionUtils.getRootCauseMessage(e));
		}

		return document;
	}

	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	public Document updateDocument(String name, String uuid, String statusString, String documentFunction, boolean archive, boolean delete, String authorGuid) throws Exception {

		Document document = documentRepo.findByUuid(uuid);


		if (document != null) {
			Function function = functionService.findOrCreateFunction(documentFunction, document.getAuthorService());
			lithium.service.document.data.entities.Status status = statusService.findOrCreateStatus(statusString, document.getAuthorService());

			Document documentNew = Document.builder()
					.archived(nullOrNew(document.isArchived(), archive))
					.deleted(nullOrNew(document.isDeleted(), delete))
					.status(nullOrNew(document.getStatus(), status))
					.function(nullOrNew(document.getFunction(), function))
					.name(nullOrNew(document.getName(), name))
					.authorService(document.getAuthorService())
					.owner(document.getOwner())
					.id(document.getId())
					.version(document.getVersion())
					.uuid(document.getUuid())
					.lastFileUploadDate(document.getLastFileUploadDate())
					.migrated(false)
					.build();

			try {
				User user = userClient.getUserByGuid(document.getOwner().getGuid());
				String message = "Document " + (document.isDeleted() ? "deleted" : "updated") + " (id:" + document.getId() + ", domain:" + user.getDomain().getName() + ", owner:" + document.getOwner().getGuid() +
						", version:" + document.getVersion() + ", author:" + user.guid() + ")";
				log.info(message);

				List<ChangeLogFieldChange> clfc = changeLogService.compare(documentNew, document,
						new String[]{"archived", "deleted", "status", "function", "name", "authorService", "owner", "id", "version", "uuid", "lastFileUploadDate"});

				changeLogService.registerChangesForNotesWithFullNameAndDomain("user.document", "edit", user.getId(), authorGuid,
						null, message, null, clfc, Category.DOCUMENTS, SubCategory.DOCUMENT_UPLOAD, 0,
						user.getDomain().getName());
			} catch (UserNotFoundException | UserClientServiceFactoryException e) {
				log.error("Can't create document upload changelog " + ExceptionUtils.getRootCauseMessage(e));
			}

			documentNew = documentRepo.save(documentNew);

			return documentNew;
		}
		return document;
	}

	private <E> E nullOrNew(E oldValue, E newValue) {
		if (newValue == null) {
			return oldValue;
		}
		return newValue;
	}

	public Response<DocumentFile> saveFile(String documentUuid, byte[] fileData, String mimeType, String name, String authorGuid, String documentStatus, LithiumTokenUtil lithiumTokenUtil) throws Exception {
		return saveFile(documentUuid, fileData, mimeType, name, authorGuid, documentStatus, 1, lithiumTokenUtil);
	}


	public Response<DocumentFile> saveFile(String documentUuid, byte[] fileData, String mimeType, String name, String authorGuid, String documentStatus, int documentPage, LithiumTokenUtil lithiumTokenUtil) throws Exception {
		Document doc = documentRepo.findByUuid(documentUuid);

		if (doc == null) {
			log.warn("Unable to find document with uuid: " + documentUuid + " in order to save file data: " + Arrays.toString(fileData));
			return Response.<DocumentFile>builder().status(Status.NOT_FOUND).build();
		}

		documentValidatorUtil.checkIfDocumentIsValid(fileData, name, lithiumTokenUtil);

		if (documentStatus != null && !documentStatus.isEmpty()) {
			updateDocument(doc.getName(), doc.getUuid(), documentStatus, doc.getFunction().getName(), doc.isArchived(), doc.isDeleted(), authorGuid);
		}

		List<DocumentFile> docFiles = documentFileRepo.findByDocumentIdAndDeletedFalse(doc.getId());
		DocumentFile docFile = null;
		for (DocumentFile documentFile : docFiles) {
			if (documentFile != null && fileService.isGraphicContentEqual(documentFile.getFile(), fileData)) {

				return Response.<DocumentFile>builder().data(documentFile).status(Status.OK).build();

			} else if (documentFile != null && documentFile.getDocumentPage() == documentPage) {
				docFile = documentFile;
				documentFile.setDeleted(true);
				documentFileRepo.save(documentFile);
				break;
			}
		}

		File file = fileService.saveFile(fileData, mimeType, name);

		DocumentFile docFileNew = DocumentFile.builder()
				.deleted(false)
				.documentId(doc.getId())
				.file(file)
				.documentPage(documentPage)
				.build();

		try {
			User user = userClient.getUserByGuid(doc.getOwner().getGuid());

			String message = "DocumentFile saved (id:" + doc.getId() + ", domain:" + user.getDomain().getName() + ", owner:" + doc.getOwner().getGuid() +
					", version:" + doc.getVersion() + ", author:" + user.guid() + ")";
			log.info(message);

			List<ChangeLogFieldChange> clfc = changeLogService.compare(docFileNew, docFile != null ? docFile : new DocumentFile(0,0, 0L,new File(),false, new Date(), 0),
					new String[] { "version", "id", "documentId", "file", "deleted", "uploadDate", "documentPage" });

			changeLogService.registerChangesForNotesWithFullNameAndDomain("user.document", "create", user.getId(), authorGuid,
					null, message, null, clfc, Category.DOCUMENTS, SubCategory.DOCUMENT_UPLOAD, 0,
					user.getDomain().getName());
		} catch (UserNotFoundException | UserClientServiceFactoryException e) {
			log.error("Can't create document upload changelog " + ExceptionUtils.getRootCauseMessage(e));
		}

		docFileNew = documentFileRepo.save(docFileNew);
		doc.setLastFileUploadDate(docFileNew.getUploadDate());
		doc.setMigrated(false);
		doc.setName(docFileNew.getFile().getName());
		doc = documentRepo.save(doc);

		return Response.<DocumentFile>builder().data(docFileNew).status(Status.OK).build();
	}

	public lithium.service.document.client.objects.DocumentFile mapWithUuid(String documentUuid, DocumentFile documentFile) {
		lithium.service.document.client.objects.DocumentFile file = mapper.map(documentFile, lithium.service.document.client.objects.DocumentFile.class);
		file.setDocumentUuid(documentUuid);
		return file;
	}


	private List<DocumentLabelValue> getDocumentLabelValues(Document d) {
		return documentLabelValueRepository.findByDocumentIdAndDeletedFalse(d.getId());
	}

	public Response<List<lithium.service.document.client.objects.Document>> findDocumentByOwnerAndAuthorService(String ownerGuid, String authorServiceName, String authorGuid) {
		List<Document> docList = documentRepo.findAllByAuthorServiceNameAndOwnerGuid(authorServiceName, ownerGuid);

		if(docList == null || docList.isEmpty()) {
			return Response.<List<lithium.service.document.client.objects.Document>>builder().status(Status.NOT_FOUND).build();
		}

		List<lithium.service.document.client.objects.Document> resultDocList = new ArrayList<>();
		for (Document d : docList) {
			List<DocumentFile> documentFiles = documentFileRepo.findByDocumentIdAndDeletedFalse(d.getId())
					.stream()
					.filter(documentFile -> !documentV2Service.isV2DocumentFile(documentFile))
					.collect(Collectors.toList());

			lithium.service.document.client.objects.Document doc = lithium.service.document.client.objects.Document.builder()
					.archived(d.isArchived())
					.authorServiceName(d.getAuthorService().getName())
					.deleted(d.isDeleted())
					.functionName(d.getFunction().getName())
					.id(d.getId())
					.name(d.getName())
					.ownerGuid(d.getOwner().getGuid())
					.statusName(d.getStatus().getName())
					.uuid(d.getUuid())
					.version(d.getVersion())
					.lastFileUploadDate(d.getLastFileUploadDate())
					.pages(documentFiles.stream().map(DocumentFile::getDocumentPage).collect(Collectors.toList()))
					.build();

			resultDocList.add(doc);
		}

		return Response.<List<lithium.service.document.client.objects.Document>>builder().data(resultDocList).status(Status.OK).build();
	}

	public Response<lithium.service.document.client.objects.DocumentFile> findV1FileByDocumentUuid(String documentUuid, Integer page) {

		try {
			Document doc = documentRepo.findByUuid(documentUuid);

			DocumentFile docFile = documentFileRepo.findByDocumentIdAndDocumentPage(doc.getId(), page)
					.stream()
					.filter(documentFile -> !documentV2Service.isV2DocumentFile(documentFile))
					.findAny()
					.orElse(null);

			return Response.<lithium.service.document.client.objects.DocumentFile>builder().data(mapWithUuid(documentUuid, docFile)).status(Status.OK).build();

		} catch (Exception e) {
			log.error("Unable to find file for document uuid: " + documentUuid, e);
			return Response.<lithium.service.document.client.objects.DocumentFile>builder().status(Status.NOT_FOUND).build();
		}
	}

	public DocumentFile findByDocumentFileId(Long documentFileId) throws Status404DocumentNotFoundException {
		return documentV2Service.getDocumentFile(documentFileId);
	}

	public DocumentInfo createAndUploadDocument(DocumentRequest documentRequest, LithiumTokenUtil tokenUtil) throws Exception {

		String domainName = documentRequest.getDomainName();
		String fileName = documentRequest.getFileName();
		String documentTypeName = documentRequest.getDocumentType();
		byte[] content = documentRequest.getContent();
		String mimeType = documentRequest.getMimeType();
		String userGuid = documentRequest.getUserGuid();
		DocumentPurpose documentPurpose = documentRequest.getDocumentPurpose();
		Long documentTypeId = documentTypeService.resolveDocumentType(documentTypeName, documentPurpose, domainName).getId();
		DocumentReviewStatus reviewStatus = documentRequest.getReviewStatus();
		List<String> versions = cachingDomainClientService.getUploadDocumentVersion(domainName);

		DocumentInfo documentInfo = DocumentInfo.builder()
				.fileName(fileName)
				.documentType(documentTypeName)
				.reviewStatus(reviewStatus.getName().toUpperCase())
				.typeId(documentTypeId)
				.build();

		if (versions.contains("v1")) {
			int docPage = documentRequest.getDocPage();
			Document document = createDocument(fileName, DocumentStatus.NEW.getStatusName(),
					DocumentType.VERIFICATION_DOCUMENT.getTypeName(), userGuid, AuthorType.USER_DOCUMENT_EXTERNAL.getTypeName(), userGuid);
			DocumentFile documentFile = saveFile(document.getUuid(), content, mimeType, fileName, userGuid, "New", docPage, tokenUtil).getData();
			documentInfo.setFileLink(config.getGatewayPublicUrl() + "/service-user/" + domainName + "/users/documents/downloadFile?documentUuid=" + document.getUuid() + "&page=" + docPage);
			documentInfo.setDocumentFileId(documentFile.getId());
			documentFile.setUploadDate(documentFile.getUploadDate());
		}
		if (versions.contains("v2")) {
			documentInfo = documentV2Service.uploadAndCreateDocument(content, userGuid, documentTypeId, reviewStatus.getName(), null, false, fileName, tokenUtil);
		}

		return documentInfo;
	}

	public long countNotMigratedDocuments() {
		return documentRepo.countByMigratedFalse();
	}

	public List<Document> findNotMigratedDocuments(Pageable page) {
		return documentRepo.findAllByMigratedFalse(page);
	}

	public void saveDocuments(List<Document> userDocuments) {
		documentRepo.saveAll(userDocuments);
	}
}
