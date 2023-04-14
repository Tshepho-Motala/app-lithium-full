package lithium.service.accounting.domain.summary.storage.repositories;

import lithium.service.accounting.domain.summary.storage.entities.AccountType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {
	@CacheEvict({
		"lithium.service.accounting.domain.summary.storage.entities.AccountType.byId",
		"lithium.service.accounting.domain.summary.storage.entities.AccountType.byCode",
	})
	@Override
	<S extends AccountType> S save(S entity);

	@Cacheable(value = "lithium.service.accounting.domain.summary.storage.entities.AccountType.byId",
			unless = "#result == null")
	default AccountType findOne(Long id) {
		return findById(id).orElse(null);
	}

	@Cacheable(value = "lithium.service.accounting.domain.summary.storage.entities.AccountType.byCode",
			unless = "#result == null")
	AccountType findByCode(String code);
}
