package lithium.service.document.data.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.document.data.entities.Document;



@Deprecated
public interface DocumentRepository extends PagingAndSortingRepository<Document, Long> {
    Document findById(long id);

    Document findByUuid(String uuid);

    List<Document> findAllByAuthorServiceNameAndOwnerGuid(String authorServiceName, String ownerGuid);

    List<Document> findAllByMigratedFalse(Pageable page);

    Long countByMigratedFalse();
}
