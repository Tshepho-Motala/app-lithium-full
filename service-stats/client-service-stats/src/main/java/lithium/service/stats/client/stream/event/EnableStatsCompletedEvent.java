package lithium.service.stats.client.stream.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * You will need to have a class that implements the {@link ICompletedStatsProcessor} interface for this to work.
 */
@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
@Import( {CompletedStatsEventAspect.class, CompletedStatsEventConfig.class, CompletedStatsEventService.class} )
public @interface EnableStatsCompletedEvent {

  String[] events() default {""};
}
