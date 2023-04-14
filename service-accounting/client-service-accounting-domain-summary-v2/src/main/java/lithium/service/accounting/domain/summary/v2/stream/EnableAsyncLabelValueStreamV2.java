package lithium.service.accounting.domain.summary.v2.stream;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({AsyncLabelValueStreamConfigurationV2.class})
public @interface EnableAsyncLabelValueStreamV2 {
}