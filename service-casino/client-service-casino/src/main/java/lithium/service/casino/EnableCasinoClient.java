package lithium.service.casino;

import lithium.service.domain.client.EnableDomainClient;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({CasinoClientService.class})
public @interface EnableCasinoClient {

}
