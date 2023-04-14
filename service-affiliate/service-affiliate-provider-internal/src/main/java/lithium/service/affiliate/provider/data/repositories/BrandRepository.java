package lithium.service.affiliate.provider.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.Brand;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Long> {
	
	Brand findByMachineName(String name);
}
