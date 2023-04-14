package lithium.service.notifications.client;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
@EnableCaching
@Import(NotificationInternalSystemService.class)
public @interface EnableNotificationsInternalSystemService {
}
