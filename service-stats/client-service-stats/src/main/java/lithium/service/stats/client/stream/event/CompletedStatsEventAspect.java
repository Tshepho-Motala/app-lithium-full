package lithium.service.stats.client.stream.event;

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
@ConditionalOnBean( annotation = EnableStatsCompletedEvent.class )
public class CompletedStatsEventAspect {

  @Autowired
  private CompletedStatsEventService completedStatsEventService;

  @Around( "@within(enableStatsCompletedEvent)" )
  public Object completedStatsEvent(ProceedingJoinPoint joinPoint, EnableStatsCompletedEvent enableStatsCompletedEvent) throws Throwable {
    for (String events: enableStatsCompletedEvent.events()) {
      log.info("Enabling StatsCompletedEvent for type(s): " + ((!events.isEmpty()) ? events : "all"));
      completedStatsEventService.addQueue(events);
    }

    return joinPoint.proceed();
  }
}