package lithium.tokens;

import lithium.systemauth.SystemAuthConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables spring boot autoconfiguration of a LothiumTokenUtilService bean that can be autowired
 * to obtain a Lithium Token from a principal.
 *
 * @see LithiumTokenUtilService
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(LithiumTokenUtilService.class)
public @interface EnableTokenUtilServices {
}
