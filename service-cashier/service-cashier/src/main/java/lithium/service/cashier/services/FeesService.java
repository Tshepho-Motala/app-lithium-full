package lithium.service.cashier.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.data.repositories.FeesRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FeesService {
	@Autowired
	private FeesRepository feesRepository;
	
	public void copy(Fees from, Fees to) {
		to.setId(from.getId());
		to.setFlat(from.getFlat());
		to.setPercentage(from.getPercentage());
		to.setMinimum(from.getMinimum());
		to.setFlatDec(from.getFlatDec());
		to.setMinimumDec(from.getMinimumDec());
		to.setStrategy(from.getStrategy());
	}
	
	public void delete(Fees fees) {
		feesRepository.delete(fees);
	}
	
	public Fees create(Fees fees) {
		return feesRepository.save(fees);
	}
	
	public Fees create(
		Long flat,
		BigDecimal percentage,
		Long minimum,
		int strategy
	) {
		log.debug("Saving new Fees Structure.");
		return feesRepository.save(
			Fees.builder()
			.flat(flat)
			.percentage(percentage)
			.minimum(minimum)
			.strategy(strategy)
			.build()
		);
	}
	
	public Fees update(
		Long feesId,
		Long flat,
		BigDecimal percentage,
		Long minimum,
		int strategy
	) {
		Fees fees = feesRepository.findOne(feesId);
		if (fees == null) fees = Fees.builder().build();
		fees.setFlat(flat);
		fees.setPercentage(percentage);
		fees.setMinimum(minimum);
		fees.setStrategy(strategy);
		
		return feesRepository.save(fees);
	}
	
	public Fees find(Long feesId) {
		return feesRepository.findOne(feesId);
	}
	
	public Fees save(Fees fees) {
		return feesRepository.save(fees);
	}
}
