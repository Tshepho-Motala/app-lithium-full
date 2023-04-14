package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.PlayerLimit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;

public interface PlayerLimitRepository extends PagingAndSortingRepository<PlayerLimit, Long> {

	PlayerLimit findByPlayerGuidAndGranularityAndType(String playerGuid, int granularity, int type);
	Page<PlayerLimit> findByType(Integer type, Pageable pageRequest);
	Page<PlayerLimit> findByTypeAndGranularity(Integer type, int granularity, Pageable pageRequest);
	
	@Modifying
	@Transactional
	void deleteByPlayerGuidAndGranularityAndType(String playerGuid, int granularity, int type);
	
	@Override

	<S extends PlayerLimit> S save(S arg0);
	
	Page<PlayerLimit> findByDomainNameAndGranularityAndType(String domainName, int granularity, int type, Pageable pageRequest);


}
