package lithium.service.raf.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.raf.data.entities.Referral;

public interface ReferralRepository extends PagingAndSortingRepository<Referral, Long>, JpaSpecificationExecutor<Referral> {
	Referral findByPlayerGuid(String playerGuid);
}
