package lithium.service.cashier.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.Method;
import lithium.service.cashier.data.entities.MethodStage;

public interface MethodStageRepository extends PagingAndSortingRepository<MethodStage, Long>, JpaSpecificationExecutor<MethodStage> {
	MethodStage findByMethodAndNumberAndDeposit(Method method, int number, boolean deposit);
	List<MethodStage> findByMethodAndDepositOrderByNumber(Method method, boolean deposit);

	default MethodStage findOne(Long id) {
		return findById(id).orElse(null);
	}
}
