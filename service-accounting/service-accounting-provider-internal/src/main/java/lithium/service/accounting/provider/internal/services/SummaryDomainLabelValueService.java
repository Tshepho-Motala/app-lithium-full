package lithium.service.accounting.provider.internal.services;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import lithium.metrics.LithiumMetricsService;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryDomainLabelValue;
import lithium.service.accounting.provider.internal.data.objects.group.PeriodAccountCodeLabelValueGroup;
import lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountLabelValueGroup;
import lithium.service.accounting.provider.internal.data.repositories.SummaryAccountLabelValueRepository;
import lithium.service.accounting.provider.internal.data.repositories.SummaryDomainLabelValueRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SummaryDomainLabelValueService {

	@Autowired PeriodService periodService;
	
	@Autowired SummaryDomainLabelValueRepository repository;
	@Autowired SummaryAccountLabelValueRepository sourceRepository;
	@Autowired LithiumMetricsService metrics;

	@Value("${lithium.service.accounting.provider.internal.services.maxConcurrentProcesses:5}")
	private int maxConcurrentProcesses;
	
	@Transactional(rollbackFor=java.lang.Exception.class, propagation=Propagation.REQUIRED)
	public void calculateDamaged(LinkedHashSet<PeriodAccountCodeLabelValueGroup> periodAccountCodeLabelValueSet) throws Exception {
		ForkJoinPool forkJoinPool = null;	
		try {
			forkJoinPool = new ForkJoinPool(maxConcurrentProcesses);
			Boolean allExecutionCompletedWithoutError = forkJoinPool.submit(() ->
				periodAccountCodeLabelValueSet.parallelStream()
				.map( (entry) -> {
					log.debug("Running in forkjoin thread.");
					SummaryAccountLabelValueGroup group = sourceRepository
							.groupBy(entry.getPeriod(), entry.getAccountCode(), entry.getTransactionType(), entry.getCurrency(), entry.getLabelValue());
					if (!calculate(group)) {
						return false;
					}
					return true;
				})
				.noneMatch( (result) -> {
					return !result; //Return all false for true results so the noneMatch, returns true for all is well
				} )
			)
			.get(); //this makes it an overall blocking call 
			if (!allExecutionCompletedWithoutError) throw new Exception("Problem in summary job execution. Forkjoin executions failed.");
		} catch (InterruptedException | ExecutionException e) {
			log.error("Problem with summary job forkjoin executions", e);
			
		} finally {
			if (forkJoinPool != null) {
				forkJoinPool.shutdown(); //always remember to shutdown the pool
			}
		}
	}

//	@Transactional(rollbackFor=java.lang.Exception.class, propagation=Propagation.REQUIRED)
//	public void calculate(Period period) {
//		log.debug(this.getClass().getSimpleName() + " calculate " + period);
//		repository.updateTag(period, 1);
//		
//		List<SummaryAccountLabelValueGroup> groupList = sourceRepository.groupBy(period);
//		for (SummaryAccountLabelValueGroup group: groupList) {
//			calculate(group);
//		}
//		
//		repository.deleteByPeriodAndTag(period, 1);
//	}

//	@Transactional(rollbackFor=java.lang.Exception.class, propagation=Propagation.REQUIRED)
	//Modified this to return false if exception occured, to let us know in the caller to force a rollback
	private boolean calculate(SummaryAccountLabelValueGroup group) {
		log.debug(this.getClass().getSimpleName() + " calculate " + group);
		if(group == null) return true; //means it is a labelvalue that is not summarized, so this is expected
		try {
			metrics.timer(log).time("calculate_summary_account_label_value_group", (StopWatch sw) -> {
			sw.start("readFromDb");
			SummaryDomainLabelValue summary = repository.findByPeriodAndAccountCodeAndTransactionTypeAndCurrencyAndLabelValue(
					group.getPeriod(), group.getAccountCode(), group.getTransactionType(), group.getCurrency(), group.getLabelValue());
			sw.stop();
			sw.start("buildSummaryObject");
			if (summary == null) {
				summary = SummaryDomainLabelValue.builder()
						.accountCode(group.getAccountCode())
						.transactionType(group.getTransactionType())
						.currency(group.getCurrency())
						.period(group.getPeriod())
						.labelValue(group.getLabelValue())
						.build();
			}
			summary.setTranCount(group.getTranCount());
			summary.setDebitCents(group.getDebitCents());
			summary.setCreditCents(group.getCreditCents());
			summary.setTag(2);
			sw.stop();
			sw.start("saveToDb");
			repository.save(summary);
			sw.stop();
			});
		} catch (Exception e) {
			log.error("Problem in summary label value job execution: " + group.toString(), e);
			return false;
		}
		return true;
	}

}
