package lithium.service.document.data.repositories;

import lithium.service.document.client.objects.DocumentFileProjection;
import lithium.service.document.data.entities.DocumentFile;
import lithium.service.document.data.entities.DocumentType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DocumentFileRepository extends PagingAndSortingRepository<DocumentFile, Long> {
	List<DocumentFile> findByDocumentIdAndDeletedFalse(long documentId);
	List<DocumentFile> findByDocumentIdAndDocumentPage(long documentId, int page);
	Optional<DocumentFile> findById(long id);

	List<DocumentFileProjection> findByDeletedFalseAndDocumentIdIn(@Param("ids") Set<Long> ids);
	default DocumentFile findOne(Long id) {
		return findById(id).orElse(null);
	}
}
