package lithium.service.accounting.domain.summary.storage.repositories;

import lithium.service.accounting.domain.summary.storage.entities.AccountCode;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountCodeRepository extends JpaRepository<AccountCode, Long> {
	@CacheEvict({
		"lithium.service.accounting.domain.summary.storage.entities.AccountCode.byCode",
		"lithium.service.accounting.domain.summary.storage.entities.AccountCode.byId",
	})
	@Override
	<S extends AccountCode> S save(S entity);

	@Cacheable(value = "lithium.service.accounting.domain.summary.storage.entities.AccountCode.byId",
			unless = "#result == null")
	default AccountCode findOne(Long id) {
		return findById(id).orElse(null);
	}
	
	@Cacheable(value = "lithium.service.accounting.domain.summary.storage.entities.AccountCode.byCode",
			unless = "#result == null")
	AccountCode findByCode(String code);
}
