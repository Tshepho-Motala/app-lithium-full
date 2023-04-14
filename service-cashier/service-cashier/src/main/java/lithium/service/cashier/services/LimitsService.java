package lithium.service.cashier.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.cashier.data.entities.Limits;
import lithium.service.cashier.data.repositories.LimitsRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LimitsService {
	@Autowired
	private LimitsRepository limitsRepository;
	
	public void copy(Limits from, Limits to) {
		to.setId(from.getId());
		to.setMinAmount(from.getMinAmount());
		to.setMaxAmount(from.getMaxAmount());
		to.setMinFirstTransactionAmount(from.getMinFirstTransactionAmount());
		to.setMaxFirstTransactionAmount(from.getMaxFirstTransactionAmount());
		to.setMaxAmountDay(from.getMaxAmountDay());
		to.setMaxAmountWeek(from.getMaxAmountWeek());
		to.setMaxAmountMonth(from.getMaxAmountMonth());
		to.setMaxTransactionsDay(from.getMaxTransactionsDay());
		to.setMaxTransactionsWeek(from.getMaxTransactionsWeek());
		to.setMaxTransactionsMonth(from.getMaxTransactionsMonth());
		to.setMinAmountDec(from.getMinAmountDec());
		to.setMaxAmountDec(from.getMaxAmountDec());
		to.setMaxAmountDayDec(from.getMaxAmountDayDec());
		to.setMaxAmountWeekDec(from.getMaxAmountWeekDec());
		to.setMaxAmountMonthDec(from.getMaxAmountMonthDec());
	}
	
	public Limits create(Limits limits) {
		return limitsRepository.save(limits);
	}
	
	public Limits create(
		Long minAmount,
		Long maxAmount,
		Long minFirstTransactionAmount,
		Long maxFirstTransactionAmount,
		Long maxAmountDay,
		Long maxAmountWeek,
		Long maxAmountMonth,
		Long maxTransactionsDay,
		Long maxTransactionsWeek,
		Long maxTransactionsMonth
	) {
		log.debug("Saving new Limits Structure.");
		return limitsRepository.save(
			Limits.builder()
			.minAmount(minAmount)
			.maxAmount(maxAmount)
			.minFirstTransactionAmount(minFirstTransactionAmount)
			.maxFirstTransactionAmount(maxFirstTransactionAmount)
			.maxAmountDay(maxAmountDay)
			.maxAmountWeek(maxAmountWeek)
			.maxAmountMonth(maxAmountMonth)
			.maxTransactionsDay(maxTransactionsDay)
			.maxTransactionsWeek(maxTransactionsWeek)
			.maxTransactionsMonth(maxTransactionsMonth)
			.build()
		);
	}
	
	public Limits update(
		Long limitsId,
		Long minAmount,
		Long maxAmount,
		Long minFirstTransactionAmount,
		Long maxFirstTransactionAmount,
		Long maxAmountDay,
		Long maxAmountWeek,
		Long maxAmountMonth,
		Long maxTransactionsDay,
		Long maxTransactionsWeek,
		Long maxTransactionsMonth
	) {
		Limits limits = limitsRepository.findOne(limitsId);
		if (limits == null) limits = Limits.builder().build();
		limits.setMinAmount(minAmount);
		limits.setMaxAmount(maxAmount);
		limits.setMinFirstTransactionAmount(minFirstTransactionAmount);
		limits.setMaxFirstTransactionAmount(maxFirstTransactionAmount);
		limits.setMaxAmountDay(maxAmountDay);
		limits.setMaxAmountWeek(maxAmountWeek);
		limits.setMaxAmountMonth(maxAmountMonth);
		limits.setMaxTransactionsDay(maxTransactionsDay);
		limits.setMaxTransactionsWeek(maxTransactionsWeek);
		limits.setMaxTransactionsMonth(maxTransactionsMonth);
		
		return limitsRepository.save(limits);
	}
	
	public Limits find(Long limitsId) {
		return limitsRepository.findOne(limitsId);
	}

	public Limits save(Limits limit) {
		return limitsRepository.save(limit);
	}
	
	public void delete(Limits limits) {
		limitsRepository.delete(limits);
	}
	
	public void delete(Long limitsId) {
		Limits limits = find(limitsId);
		log.info("Delete Limits : "+limits);
		limitsRepository.delete(limits);
	}
}
