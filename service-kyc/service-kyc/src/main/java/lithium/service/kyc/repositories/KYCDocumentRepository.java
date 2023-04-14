package lithium.service.kyc.repositories;

import lithium.service.kyc.entities.KYCDocument;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface KYCDocumentRepository extends PagingAndSortingRepository<KYCDocument, Long> {
}
