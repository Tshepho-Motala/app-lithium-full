package lithium.rest;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add the annotation and create a rest template from the RestTemplateBuilder with qualifier lithium.rest
 * Additional customizers can also be created to allow in-service changes
 * The RestTemplateBuilder can also be used to modify many aspects of the instantiated RestTemplate
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({LithiumRestTemplateConfiguration.class})
public @interface EnableRestTemplate {
}
