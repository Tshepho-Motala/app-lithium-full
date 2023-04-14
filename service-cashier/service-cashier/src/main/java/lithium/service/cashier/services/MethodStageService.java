package lithium.service.cashier.services;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.cashier.data.entities.Method;
import lithium.service.cashier.data.entities.MethodStage;
import lithium.service.cashier.data.repositories.MethodStageRepository;

@Service
public class MethodStageService {
	@Autowired
	private MethodStageRepository repo;
	
	public List<MethodStage> findByMethodAndDeposit(Method method, boolean deposit) {
		return repo.findByMethodAndDepositOrderByNumber(method, deposit);
	}
	
	public MethodStage findByMethodAndStageNumberAndDeposit(Method method, int number, boolean deposit) {
		return repo.findByMethodAndNumberAndDeposit(method, number, deposit);
	}
	
	public MethodStage findOne(MethodStage stage) {
		return repo.findOne(stage.getId());
	}
	
	public MethodStage findOne(Long id) {
		return repo.findOne(id);
	}
		
	public MethodStage save(MethodStage stage) {
		return repo.save(stage);
	}
	
}
