package lithium.service.accounting.client.stream.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * You will need to have a class that implements the {@link ICompletedTransactionProcessor} interface for this to work.
 */
@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
@Import( {CompletedTransactionEventAspect.class, CompletedTransactionEventConfig.class, CompletedTransactionEventService.class} )
public @interface EnableAccountingTransactionCompletedEvent {

  String[] transactionTypeCodes() default {""};

  boolean enhanceGameData() default false;
}
