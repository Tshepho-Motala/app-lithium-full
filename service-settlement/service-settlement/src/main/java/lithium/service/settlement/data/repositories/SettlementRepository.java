package lithium.service.settlement.data.repositories;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.settlement.data.entities.Settlement;

public interface SettlementRepository extends PagingAndSortingRepository<Settlement, Long>, JpaSpecificationExecutor<Settlement> {
	Settlement findByBatchSettlementsNameIgnoreCaseAndEntityUuidAndDateStartAndDateEndAndOpenTrue(String batchName, String entityUuid, Date dateStart, Date dateEnd);
	Settlement findByBatchSettlementsNameIgnoreCaseAndUserGuidAndDateStartAndDateEndAndOpenTrue(String batchName, String userGuid, Date dateStart, Date dateEnd);
	List<Settlement> findByDateStartAndDateEnd(Date dateStart, Date dateEnd);
	@Modifying
	@Transactional
	void deleteByDomainNameAndBatchSettlementsName(String domainName, String batchName);
}
