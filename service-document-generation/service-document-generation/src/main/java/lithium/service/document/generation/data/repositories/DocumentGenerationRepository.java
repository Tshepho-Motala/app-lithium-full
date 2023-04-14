package lithium.service.document.generation.data.repositories;

import lithium.service.document.generation.client.objects.CsvProvider;
import lithium.service.document.generation.data.entities.DocumentGeneration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface DocumentGenerationRepository extends PagingAndSortingRepository<DocumentGeneration, Long> {
    DocumentGeneration findFirstByProviderAndStatusAndAuthorGuid(CsvProvider provider, int status, String authorGuid);
    Page<DocumentGeneration> findAllByAuthorGuid(String guid, Pageable pageable);
    List<DocumentGeneration> findByStatusInAndProviderAndCreatedDateBefore(List<Integer> statuses, CsvProvider provider, Date date);
}
