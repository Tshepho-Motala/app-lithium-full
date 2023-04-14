package lithium.service.affiliate.provider.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.Ad;

public interface AdRepository extends PagingAndSortingRepository<Ad, Long>, JpaSpecificationExecutor<Ad> {

	public Ad findByGuid(String guid);

	default Ad findOne(Long id) {
		return findById(id).orElse(null);
	}


}