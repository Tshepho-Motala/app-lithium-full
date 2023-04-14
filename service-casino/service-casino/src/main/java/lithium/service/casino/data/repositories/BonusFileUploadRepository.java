package lithium.service.casino.data.repositories;

import lithium.service.casino.data.entities.BonusFileUpload;
import lithium.service.casino.data.entities.BonusRevision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BonusFileUploadRepository extends PagingAndSortingRepository<BonusFileUpload, Long> {
	Page<BonusFileUpload> findByBonusRevision(BonusRevision bonusRevision, Pageable pageable);

	default BonusFileUpload findOne(Long id) {
		return findById(id).orElse(null);
	}
}
