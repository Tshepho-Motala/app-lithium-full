package lithium.service.cashier.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.MethodStage;
import lithium.service.cashier.data.entities.MethodStageField;

public interface MethodStageFieldRepository extends PagingAndSortingRepository<MethodStageField, Long>, JpaSpecificationExecutor<MethodStageField> {
	MethodStageField findByStageAndCodeAndInput(MethodStage stage, String code, boolean input);
	List<MethodStageField> findByStageAndInputOrderByDisplayOrder(MethodStage stage, boolean input);

	default MethodStageField findOne(Long id) {
		return findById(id).orElse(null);
	}

}
