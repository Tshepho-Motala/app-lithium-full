package lithium.service.cashier.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.Method;

public interface MethodRepository extends PagingAndSortingRepository<Method, Long> {
	Method findByCode(String code);

	default Method findOne(Long id) {
		return findById(id).orElse(null);
	}

}
