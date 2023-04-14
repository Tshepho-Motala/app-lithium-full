package lithium.service.accounting.stream;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@ConditionalOnBean( annotation = EnableAccountingSummaryAccountTransactionTypeCompletedEvent.class )
public class CompletedSummaryAccountTransactionTypeEventAspect {

  @Autowired
  private CompletedSummaryAccountTransactionTypeEventService completedSummaryAccountTransactionTypeEventService;

  @Around( "@within(enableAccountingSummaryAccountTransactionTypeCompletedEvent)" )
  public Object completedSummaryAccountTransactionTypeEvent(ProceedingJoinPoint joinPoint,
      EnableAccountingSummaryAccountTransactionTypeCompletedEvent enableAccountingSummaryAccountTransactionTypeCompletedEvent)
  throws Throwable
  {
    for (String tranType: enableAccountingSummaryAccountTransactionTypeCompletedEvent.transactionTypeCodes()) {
      log.info("Enabling SummaryAccountTransactionTypeCompletedEvent for type(s): " + ((!tranType.isEmpty()) ? tranType : "all"));
      completedSummaryAccountTransactionTypeEventService.addQueue(tranType);
    }

    return joinPoint.proceed();
  }
}