package lithium.service.mail.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.mail.data.entities.Provider;

public interface ProviderRepository extends PagingAndSortingRepository<Provider, Long> {
	Provider findByCode(String code);

	default Provider findOne(Long id) {
		return findById(id).orElse(null);
	}
}