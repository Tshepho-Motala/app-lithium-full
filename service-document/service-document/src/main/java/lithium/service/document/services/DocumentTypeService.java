package lithium.service.document.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.document.client.objects.DocumentPurpose;
import lithium.service.document.data.entities.DocumentType;
import lithium.service.document.data.entities.DocumentTypeMappingName;
import lithium.service.document.data.objects.TextValue;
import lithium.service.document.data.repositories.DocumentTypeMappingNameRepository;
import lithium.service.document.data.repositories.DocumentTypeRepository;
import lithium.service.document.data.repositories.DomainRepository;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@Service
public class DocumentTypeService {

    @Autowired
    private ChangeLogService changeLogService;
    @Autowired
    private CachingDomainClientService cachingDomainClientService;
    @Autowired
    private DocumentTypeRepository documentTypeRepository;
    @Autowired
    private DocumentTypeMappingNameRepository documentTypeMappingNameRepository;
    @Autowired
    private DomainRepository domainRepository;

    public List<lithium.service.document.client.objects.DocumentType> listPerDomain(String domainName) {
        return documentTypeRepository.findAllByDomainName(domainName)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public DocumentType getDocumentType(Long id) {
        return documentTypeRepository.findOne(id);
    }

    public lithium.service.document.client.objects.DocumentType save(String domainName, lithium.service.document.client.objects.DocumentType documentType, LithiumTokenUtil tokenUtil) {
        DocumentType.DocumentTypeBuilder builder = DocumentType.builder();

        String comment = "Created new document type: " + documentType.getType() + "(" + documentType.getPurpose() + ")";
        DocumentType copyDocumentType = new DocumentType();
        String changeType = "create";
        if (nonNull(documentType.getId())) {
            DocumentType previousEntity = documentTypeRepository.findOne(documentType.getId());
            copyDocumentType.setType(previousEntity.getType());
            copyDocumentType.setPurpose(previousEntity.getPurpose());
            copyDocumentType.setIconName(previousEntity.getIconName());
            copyDocumentType.setEnabled(previousEntity.isEnabled());
            copyDocumentType.setTypeSensitive(previousEntity.isTypeSensitive());
            copyDocumentType.setMappingNames(previousEntity.getMappingNames());
            comment = "Updated document type: " + documentType.getType() + "(" + documentType.getPurpose() + ")";
            changeType = "edit";

            builder = previousEntity.toBuilder();
        }

        lithium.service.document.data.entities.Domain domainLocal = domainRepository.findOrCreateByName(domainName, lithium.service.document.data.entities.Domain::new);

        DocumentType documentTypeEntity = builder
                .purpose(DocumentPurpose.fromPurpose(documentType.getPurpose()))
                .type(documentType.getType())
                .domain(domainLocal)
                .iconBase64(documentType.getIconBase64())
                .iconName(documentType.getIconName())
                .iconType(documentType.getIconType())
                .iconSize(documentType.getIconSize())
                .enabled(documentType.isEnabled())
                .typeSensitive(documentType.isTypeSensitive())
                .build();

        documentTypeEntity = documentTypeRepository.save(documentTypeEntity);

        List<DocumentTypeMappingName> updatedMappingNames = resolveUpdatedMappingNames(documentTypeEntity, documentType.getMappingNames());
        documentTypeEntity.setMappingNames(updatedMappingNames);

        try {
            String[] fields = {"purpose", "type", "iconName", "enabled", "mappingNames", "typeSensitive"};
            List<ChangeLogFieldChange> clfc = changeLogService.copy(documentTypeEntity, copyDocumentType, fields);
            Domain domain = cachingDomainClientService.getDomainClient().findByName(domainName).getData();
            changeLogService.registerChangesForNotesWithFullNameAndDomain("domain.document.type", changeType, domain.getId(),
                    tokenUtil.guid(), tokenUtil, comment, null, clfc, Category.DOCUMENTS, SubCategory.DOCUMENT_TYPE, 40, domainName);
        } catch (Throwable e) {
            log.warn("Problem adding changelog on " + changeType + " document type: DocumentType -> {}, exception -> {}", documentTypeEntity, e);
        }

        return convertToDto(documentTypeEntity);
    }

    private List<DocumentTypeMappingName> resolveUpdatedMappingNames(DocumentType documentType, List<String> updatedMappingNames) {
        Map<String, DocumentTypeMappingName> existMappingNames = documentType.getMappingNames()
                .stream().collect(Collectors.toMap(DocumentTypeMappingName::getName, Function.identity()));

        List<String> mappingNamesToDelete = new ArrayList<>(existMappingNames.keySet());
        mappingNamesToDelete.removeAll(updatedMappingNames);

        for (String key : mappingNamesToDelete) {
            DocumentTypeMappingName mappingNameDeleting = existMappingNames.get(key);
            documentTypeMappingNameRepository.delete(mappingNameDeleting);
            existMappingNames.remove(key);
            log.debug("Removed mapping name: " + mappingNameDeleting);
        }

        for (String mappingName : updatedMappingNames) {
            if (!existMappingNames.containsKey(mappingName)) {
                DocumentTypeMappingName newMapping = documentTypeMappingNameRepository.save(
                        DocumentTypeMappingName.builder()
                                .documentType(documentType)
                                .name(mappingName)
                                .build());
                existMappingNames.put(newMapping.getName(), newMapping);
                log.debug("Saved new mapping name: " + newMapping);
            }
        }
        return new ArrayList<>(existMappingNames.values());
    }

    public List<TextValue> enabledTypesPerDomain(String domainName, boolean internalOnly) {
        List<DocumentPurpose> documentPurposes = internalOnly ? Collections.singletonList(DocumentPurpose.INTERNAL) : Arrays.stream(DocumentPurpose.values()).collect(Collectors.toList());
        return documentTypeRepository.findAllByDomainNameAndPurposeInAndEnabledTrue(domainName, documentPurposes)
                .stream()
                .map(dt -> new TextValue(dt.getType() + " (" + dt.getPurpose().name() + ")", dt.getId(),
                    dt.isTypeSensitive()))
                .collect(Collectors.toList());
    }

    private lithium.service.document.client.objects.DocumentType convertToDto(DocumentType entity) {
        return lithium.service.document.client.objects.DocumentType.builder()
                .id(entity.getId())
                .purpose(entity.getPurpose().purpose())
                .type(entity.getType())
                .iconBase64(entity.getIconBase64())
                .iconName(entity.getIconName())
                .iconType(entity.getIconType())
                .iconSize(entity.getIconSize())
                .enabled(entity.isEnabled())
                .modifiedDate(entity.getModifiedDate())
                .typeSensitive(entity.isTypeSensitive())
                .mappingNames(entity.getMappingNames().stream().map(DocumentTypeMappingName::getName).collect(Collectors.toList()))
                .build();
    }

    public Response<ChangeLogs> changelog(String domainName, int page) throws Exception {
        Domain domain = cachingDomainClientService.getDomainClient().findByName(domainName).getData();
        Long domainId = domain != null ? domain.getId() : -1;
        return changeLogService.listLimited(ChangeLogRequest.builder()
                .entityRecordId(domainId)
                .entities(new String[]{"domain.document.type"})
                .page(page)
                .build()
        );
    }

    public lithium.service.document.client.objects.DocumentType resolveDocumentType(String documentTypeMapping, DocumentPurpose documentPurposeName, String domainName) {
        DocumentType documentType = documentTypeRepository.findByDomainNameAndPurposeAndMappingNamesName(domainName, documentPurposeName, documentTypeMapping);
        if (documentType == null)
            documentType = documentTypeRepository.findByDomainNameAndPurposeAndType(domainName, documentPurposeName, "Other");
        return convertToDto(documentType);
    }

    public List<DocumentType> getDocumentTypesForDomain(String domainName) {
        return documentTypeRepository.findAllByDomainName(domainName);
    }

    public DocumentType findOrCreateDocumentType(lithium.service.document.data.entities.Domain domain, DocumentPurpose internal, String other) {
        return documentTypeRepository.findOrCreate(domain, internal, other);
    }
}
