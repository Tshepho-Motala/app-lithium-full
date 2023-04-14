package lithium.service.accounting.provider.internal.services;

import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryDomainTransactionType;
import lithium.service.accounting.provider.internal.data.objects.group.PeriodAccountCodeTransactionTypeGroup;
import lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountTransactionTypeGroup;
import lithium.service.accounting.provider.internal.data.repositories.SummaryAccountTransactionTypeRepository;
import lithium.service.accounting.provider.internal.data.repositories.SummaryDomainTransactionTypeRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SummaryDomainTransactionTypeService {

	@Autowired PeriodService periodService;
	
	@Autowired SummaryDomainTransactionTypeRepository repository;
	@Autowired SummaryAccountTransactionTypeRepository sourceRepository;
	
	@Transactional(rollbackFor=java.lang.Exception.class, propagation=Propagation.REQUIRED)
	public void calculateDamaged(LinkedHashSet<PeriodAccountCodeTransactionTypeGroup> periodAccountCodeTransactionTypeSet) {
		for (PeriodAccountCodeTransactionTypeGroup entry: periodAccountCodeTransactionTypeSet) {
			SummaryAccountTransactionTypeGroup group = sourceRepository.groupBy(entry.getPeriod(), entry.getAccountCode(), entry.getTransactionType(), entry.getCurrency());
			calculate(group);
		}
	}

	@Transactional(rollbackFor=java.lang.Exception.class, propagation=Propagation.REQUIRED)
	public void calculate(Period period) {
		log.debug(this.getClass().getSimpleName() + " calculate " + period);
		repository.updateTag(period, 1);
		
		List<SummaryAccountTransactionTypeGroup> groupList = sourceRepository.groupBy(period);
		for (SummaryAccountTransactionTypeGroup group: groupList) {
			calculate(group);
		}
		
		repository.deleteByPeriodAndTag(period, 1);
	}

	@Transactional(rollbackFor=java.lang.Exception.class, propagation=Propagation.REQUIRED)
	public void calculate(SummaryAccountTransactionTypeGroup group) {
		log.debug(this.getClass().getSimpleName() + " calculate " + group);
		SummaryDomainTransactionType summary = repository.findByPeriodAndAccountCodeAndTransactionTypeAndCurrency(
				group.getPeriod(), group.getAccountCode(), group.getTransactionType(), group.getCurrency());
				
		if (summary == null) {
			summary = SummaryDomainTransactionType.builder()
					.accountCode(group.getAccountCode())
					.transactionType(group.getTransactionType())
					.currency(group.getCurrency())
					.period(group.getPeriod())
					.build();
		}
		summary.setTranCount(group.getTranCount());
		summary.setDebitCents(group.getDebitCents());
		summary.setCreditCents(group.getCreditCents());
		summary.setTag(2);
		repository.save(summary);
	}

}
