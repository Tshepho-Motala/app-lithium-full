package lithium.service.casino.data.repositories;

import lithium.service.casino.data.entities.BonusFileRunDetail;
import lithium.service.casino.data.entities.BonusFileUpload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BonusFileRunDetailRepository extends PagingAndSortingRepository<BonusFileRunDetail, Long> {
	Page<BonusFileRunDetail> findByBonusFileUpload(BonusFileUpload bonusFileUpload, Pageable pageable);
}
