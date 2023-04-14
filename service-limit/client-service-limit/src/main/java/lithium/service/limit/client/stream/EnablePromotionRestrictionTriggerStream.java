package lithium.service.limit.client.stream;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({PromotionRestrictionTriggerConfiguration.class})
public @interface EnablePromotionRestrictionTriggerStream {
}
