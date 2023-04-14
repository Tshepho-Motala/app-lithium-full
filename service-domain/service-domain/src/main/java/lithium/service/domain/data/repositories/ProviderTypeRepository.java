package lithium.service.domain.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.domain.data.entities.ProviderType;

public interface ProviderTypeRepository extends PagingAndSortingRepository<ProviderType, Long> {
	ProviderType findByName(String name);

  default ProviderType findOne(Long id) {
    return findById(id).orElse(null);
  }
}
