package lithium.service.mail.client;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Retention(RUNTIME)
@Target(TYPE)
@Import({DefaultEmailTemplateFileReader.class,DefaultEmailTemplateRegisterService.class})
public @interface EnableDefaultEmailTemplateRegistrationService {
}