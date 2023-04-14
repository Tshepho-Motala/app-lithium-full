package lithium.service.access.data.repositories;

import java.util.Date;
import lithium.service.access.data.entities.RawTransactionData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RawTransactionDataRepository extends PagingAndSortingRepository<RawTransactionData, Long> {
  Page<RawTransactionData> findAllByCreationDateBefore(Date creationDate, Pageable pageable);
}
