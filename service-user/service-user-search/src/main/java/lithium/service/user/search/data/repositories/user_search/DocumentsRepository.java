package lithium.service.user.search.data.repositories.user_search;

import lithium.service.user.search.data.entities.Document;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DocumentsRepository extends PagingAndSortingRepository<Document, Long>, JpaSpecificationExecutor<Document> {
  Document findById(long id);
}
