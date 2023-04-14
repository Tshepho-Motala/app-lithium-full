package lithium.service.accounting.stream;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * You will need to have a class that implements the {@link ICompletedSummaryAccountTransactionTypeProcessor} interface for this to work.
 */
@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
@Import( {CompletedSummaryAccountTransactionTypeEventAspect.class, CompletedSummaryAccountTransactionTypeEventConfig.class,
    CompletedSummaryAccountTransactionTypeEventService.class} )
public @interface EnableAccountingSummaryAccountTransactionTypeCompletedEvent {

  String[] transactionTypeCodes() default {""};
}
