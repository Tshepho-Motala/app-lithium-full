package lithium.service.cashier.client.event;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * You will need to have a class that implements the {@link ICashierFirstDepositProcessor} interface for this to work.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({CashierFirstDepositEventSink.class})
public @interface EnableCashierFirstDepositEvent {

}
