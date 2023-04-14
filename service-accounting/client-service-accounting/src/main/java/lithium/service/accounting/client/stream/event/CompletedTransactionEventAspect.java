package lithium.service.accounting.client.stream.event;

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
@ConditionalOnBean( annotation = EnableAccountingTransactionCompletedEvent.class )
public class CompletedTransactionEventAspect {

  @Autowired
  private CompletedTransactionEventService completedTransactionEventService;

  @Around( "@within(enableAccountingTransactionCompletedEvent)" )
  public Object completedTransactionEvent(ProceedingJoinPoint joinPoint, EnableAccountingTransactionCompletedEvent enableAccountingTransactionCompletedEvent)
      throws Throwable
  {
    for (String tranType: enableAccountingTransactionCompletedEvent.transactionTypeCodes()) {
      log.info("Enabling TransactionCompletedEvent for type(s): " + ((!tranType.isEmpty()) ? tranType : "all"));
      completedTransactionEventService.addQueue(tranType, enableAccountingTransactionCompletedEvent.enhanceGameData());
    }

    return joinPoint.proceed();
  }
}