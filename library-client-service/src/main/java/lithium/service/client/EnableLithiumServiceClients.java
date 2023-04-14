package lithium.service.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import lithium.systemauth.EnableSystemAuth;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableOAuth2Client
@EnableSystemAuth
@Import({LithiumServiceClientConfiguration.class})
public @interface EnableLithiumServiceClients {

}
