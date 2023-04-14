package lithium.service.document.data.repositories;

import lithium.service.document.data.entities.DocumentType;
import lithium.service.document.data.entities.Domain;
import lithium.service.document.client.objects.DocumentPurpose;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;

public interface DocumentTypeRepository extends PagingAndSortingRepository<DocumentType, Long> {
	List<DocumentType> findAllByDomainName(String domainName);

	List<DocumentType> findAllByDomainNameAndPurposeInAndEnabledTrue(String domainName, Collection<DocumentPurpose> purposes);

	DocumentType findByDomainAndPurposeAndType(Domain domain, DocumentPurpose purpose, String type);

	default DocumentType findOne(Long id) {
		return findById(id).orElse(null);
	}

	DocumentType findByDomainNameAndPurposeAndType(String domainName, DocumentPurpose documentPurpose, String type);

	DocumentType findByDomainNameAndPurposeAndMappingNamesName(String domainName, DocumentPurpose documentPurpose, String mappingName);

	default DocumentType findOrCreate(Domain domain, DocumentPurpose purpose, String type) {
		DocumentType t = findByDomainAndPurposeAndType(domain, purpose, type);
		if (t == null) {
			t = DocumentType.builder()
					.domain(domain)
					.type(type)
					.purpose(purpose)
					.enabled(true)
					.build();
			save(t);
		}
		return t;
	}
}
