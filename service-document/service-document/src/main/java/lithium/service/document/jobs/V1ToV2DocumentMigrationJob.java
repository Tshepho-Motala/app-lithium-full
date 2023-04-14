package lithium.service.document.jobs;

import lithium.service.document.client.objects.DocumentPurpose;
import lithium.service.document.client.objects.enums.DocumentReviewStatus;
import lithium.service.document.data.entities.Document;
import lithium.service.document.data.entities.DocumentFile;
import lithium.service.document.data.entities.DocumentType;
import lithium.service.document.data.entities.DocumentV2;
import lithium.service.document.data.entities.Domain;
import lithium.service.document.data.entities.ReviewStatus;
import lithium.service.document.data.repositories.DomainRepository;
import lithium.service.document.data.repositories.ReviewStatusRepository;
import lithium.service.document.services.DocumentService;
import lithium.service.document.services.DocumentTypeService;
import lithium.service.document.services.DocumentV2Service;
import lithium.service.document.services.FileService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static lithium.service.document.ServiceDocumentInit.DOCUMENT_TYPE_NAME_BANK_STATEMENT;
import static lithium.service.document.ServiceDocumentInit.DOCUMENT_TYPE_NAME_DRIVERS_LICENSE;
import static lithium.service.document.ServiceDocumentInit.DOCUMENT_TYPE_NAME_OTHER;
import static lithium.service.document.ServiceDocumentInit.DOCUMENT_TYPE_NAME_PASSPORT;
import static lithium.service.document.ServiceDocumentInit.DOCUMENT_TYPE_NAME_SELFIE_ID;
import static lithium.service.document.ServiceDocumentInit.DOCUMENT_TYPE_NAME_UTILITY_BILL;

@Slf4j
@Service
public class V1ToV2DocumentMigrationJob {
    private static final String PASSPORT_SHORT_PART = "PASSP";
    private static final String PASPORT_TYPO_SHORT_PART = "PASP";
    private static final String SELFIE_ID_SHORT_PART = "SELF";
    private static final String DRIVER_LICENSE_SHORT_PART = "DRIVER";
    private static final String BANK_STATEMENT_SHORT_PART = "BANK";
    private static final String UTILITY_BILL_SHORT_PART = "UTIL";
    private AtomicLong migratedDocsCount;
    private AtomicLong v2DocsCreatedCount;
    private long total;

    private Map<Domain, List<DocumentType>> cashedDocumentTypesPerDomain;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentV2Service documentV2Service;

    @Autowired
    private FileService fileService;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private DocumentTypeService documentTypeService;

    @Autowired
    private ReviewStatusRepository reviewStatusRepository;

    @Async
    public void migrateDocumentsData(int pageSize, Long delay) throws InterruptedException {
        log.info("/service-document/v1-to-v2-migration-job, isMigrationJobStarted={}", JobState.isStarted);
        if (!JobState.isStarted) {
            migratedDocsCount = new AtomicLong(0);
            v2DocsCreatedCount = new AtomicLong(0);
            total = documentService.countNotMigratedDocuments();
            cashedDocumentTypesPerDomain = new HashMap<>();
            Pageable page = PageRequest.of(0, pageSize);
            ReviewStatus reviewStatus = reviewStatusRepository.findOrCreateByName(DocumentReviewStatus.HISTORIC.getName(), ReviewStatus::new);
            JobState.start();
            long lastIterationMigratedCount;
            do {
                if (JobState.isTerminated) break;
                lastIterationMigratedCount = migrate(page, reviewStatus);
                log.info("Migration is running:" + migratedDocsCount.get() + " of " + total + " completed," + v2DocsCreatedCount.get() + " new V2 Docs Created");
                throttleMigration(delay);
            } while (lastIterationMigratedCount > 0);
            stopJobAndResetCounters("finished");
            log.info(":: Migration of documents is finished.");
        } else {
            log.info("Migration is running:" + migratedDocsCount.get() + " of " + total + " completed," + v2DocsCreatedCount.get() + " new V2 Docs Created");
        }
    }

    private long migrate(Pageable page, ReviewStatus reviewStatus) {

        List<Document> needToMigrateV1Docs = documentService.findNotMigratedDocuments(page);

        buildAndSaveNewV2Docs(reviewStatus, needToMigrateV1Docs);

        markV1DocsAsMigratedAndSave(needToMigrateV1Docs);

        migratedDocsCount.getAndAdd(needToMigrateV1Docs.size());
        return needToMigrateV1Docs.size();
    }

    private void markV1DocsAsMigratedAndSave(List<Document> needToMigrateV1Docs) {
        needToMigrateV1Docs.forEach(document -> document.setMigrated(true));
        documentService.saveDocuments(needToMigrateV1Docs);
    }

    private void buildAndSaveNewV2Docs(ReviewStatus reviewStatus, List<Document> needToMigrateV1Docs) {
        List<DocumentV2> newV2Docs = buildV2Docs(needToMigrateV1Docs, reviewStatus);
        newV2Docs.forEach(this::saveNewV2Docs);
    }

    private List<DocumentV2> buildV2Docs(List<Document> v1Documents, ReviewStatus reviewStatus) {
        List<DocumentV2> v2Docs = new ArrayList<>();
        v1Documents.forEach(docV1 -> {
            List<DocumentV2> newV2documents = buildDocsV2(docV1, reviewStatus);
            v2Docs.addAll(newV2documents);
        });
        return v2Docs;
    }

    private List<DocumentV2> buildDocsV2(Document docV1, ReviewStatus reviewStatus) {

        List<DocumentV2> resultDocsV2 = new ArrayList<>();

        List<DocumentFile> documentFiles = fileService.findByDocumentIdAndDeletedFalse(docV1.getId()).stream()
                .filter(documentFile -> !documentV2Service.isV2DocumentFile(documentFile))
                .collect(Collectors.toList());
        log.debug("Found " + documentFiles.size() + " docFiles for docId " + docV1.getId());

        if (documentFiles.size() > 0) {

            Domain domain = domainRepository.findOrCreateByName(docV1.getOwner().getGuid().split("/")[0], Domain::new);

            DocumentType docType = resolveDocType(docV1.getFunction().getName(), domain);

            for (DocumentFile file : documentFiles) {
                DocumentV2 documentV2 = DocumentV2.builder()
                        .documentFile(file)
                        .domain(domain)
                        .owner(docV1.getOwner())
                        .fileName(file.getFile().getName())
                        .sensitive(false)
                        .documentType(docType)
                        .reviewStatus(reviewStatus)
                        .build();
                resultDocsV2.add(documentV2);
            }
        }
        return resultDocsV2;
    }

    private DocumentType resolveDocType(String functionName, Domain domain) {
        String upperCaseName = functionName.toUpperCase();

        if (upperCaseName.contains(SELFIE_ID_SHORT_PART)) {
            return getOrCreateDocumentType(DOCUMENT_TYPE_NAME_SELFIE_ID, domain);

        } else if (upperCaseName.contains(PASSPORT_SHORT_PART) || upperCaseName.contains(PASPORT_TYPO_SHORT_PART)) {
            return getOrCreateDocumentType(DOCUMENT_TYPE_NAME_PASSPORT, domain);

        } else if (upperCaseName.contains(DRIVER_LICENSE_SHORT_PART)) {
            return getOrCreateDocumentType(DOCUMENT_TYPE_NAME_DRIVERS_LICENSE, domain);

        } else if (upperCaseName.contains(BANK_STATEMENT_SHORT_PART)) {
            return getOrCreateDocumentType(DOCUMENT_TYPE_NAME_BANK_STATEMENT, domain);

        } else if (upperCaseName.contains(UTILITY_BILL_SHORT_PART)) {
            return getOrCreateDocumentType(DOCUMENT_TYPE_NAME_UTILITY_BILL, domain);

        } else return getOrCreateDocumentType(DOCUMENT_TYPE_NAME_OTHER, domain);
    }

    private DocumentType getOrCreateDocumentType(String name, Domain domain) {

        List<DocumentType> documentTypes = getDocumentTypesForDomain(domain);

        return documentTypes.stream()
                .filter(documentType -> documentType.getType().equalsIgnoreCase(name)).findFirst()
                .orElseGet(() -> documentTypeService.findOrCreateDocumentType(domain, DocumentPurpose.INTERNAL, name));
    }

    private List<DocumentType> getDocumentTypesForDomain(Domain domain) {
        if (!cashedDocumentTypesPerDomain.containsKey(domain)) {
            cashedDocumentTypesPerDomain.put(domain, documentTypeService.getDocumentTypesForDomain(domain.getName()));
        }
        return cashedDocumentTypesPerDomain.get(domain);
    }

    private void saveNewV2Docs(DocumentV2 documentV2) {
        if (!isDocV2ForFileAlreadyExist(documentV2)) {
            documentV2Service.saveDocument(documentV2);
            v2DocsCreatedCount.getAndIncrement();
        } else {
            log.debug("Cant save new V2Doc. Such OwnerGuid: " + documentV2.getOwner().getGuid() +
                    " and DocumentFile.File.FileName: " + documentV2.getDocumentFile().getFile().getName() + " the couple already exists");
        }
    }

    private boolean isDocV2ForFileAlreadyExist(DocumentV2 documentV2) {
        return documentV2Service.isDocV2Exist(documentV2.getOwner(), documentV2.getDocumentFile().getFile());
    }

    private void throttleMigration(Long delay) throws InterruptedException {
        Thread.sleep(delay);
    }

    public void stopJobAndResetCounters(String reason) {
        JobState.terminate();
        log.info("Docs V1toV2 MigrationJob is " + reason + " with "
                + migratedDocsCount.get() + " of " + total + " V1 docs processed, "
                + v2DocsCreatedCount.get() + " new V2 Docs Created");
        migratedDocsCount = new AtomicLong(0);
        v2DocsCreatedCount = new AtomicLong(0);
        total = 0L;
        cashedDocumentTypesPerDomain = new HashMap<>();
    }

    public long getMigratedDocsCount() {
        return migratedDocsCount.get();
    }

    public long getV2CreatedDocCount() {
        return v2DocsCreatedCount.get();
    }

    public long getTotal() {
        return total;
    }

    @Getter
    private static class JobState {

        private static boolean isStarted;
        private static boolean isTerminated;

        public static void start() {
            isStarted = true;
            isTerminated = false;
        }

        public static void terminate() {
            isTerminated = true;
            isStarted = false;
        }
    }
}
