package lithium.service.cashier.services;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.cashier.data.entities.MethodStage;
import lithium.service.cashier.data.entities.MethodStageField;
import lithium.service.cashier.data.repositories.MethodStageFieldRepository;

@Service
public class MethodStageFieldService {
	@Autowired
	private MethodStageFieldRepository repo;
	
	public List<MethodStageField> findInputFieldsByMethodStage(MethodStage stage) {
		return findByMethodStageAndInput(stage, true);
	}

	public List<MethodStageField> findOutputFieldsByMethodStage(MethodStage stage) {
		return findByMethodStageAndInput(stage, false);
	}
	
	public List<MethodStageField> findByMethodStageAndInput(MethodStage stage, boolean input) {
		return repo.findByStageAndInputOrderByDisplayOrder(stage, input);
	}
	
	public MethodStageField findByCode(MethodStage stage, boolean input, String code) {
		return repo.findByStageAndCodeAndInput(stage, code, input);
	}
	
	public MethodStageField findOne(MethodStageField field) {
		return repo.findOne(field.getId());
	}
	
	public MethodStageField findOne(Long id) {
		return repo.findOne(id);
	}
		
	public MethodStageField save(MethodStageField field) {
		return repo.save(field);
	}

	public void delete(MethodStageField field) { repo.delete(field); }
	
}
