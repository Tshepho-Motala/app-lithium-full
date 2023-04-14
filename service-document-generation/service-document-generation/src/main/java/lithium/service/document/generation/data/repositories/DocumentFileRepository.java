package lithium.service.document.generation.data.repositories;

import lithium.service.document.generation.data.entities.DocumentFile;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface DocumentFileRepository extends PagingAndSortingRepository<DocumentFile, Long> {
    DocumentFile findDocumentFileByReference(String reference);
    List<DocumentFile> deleteDocumentFileByCreatedDateBefore(Date date);
}
