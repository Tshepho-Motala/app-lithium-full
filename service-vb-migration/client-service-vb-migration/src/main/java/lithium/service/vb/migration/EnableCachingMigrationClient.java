package lithium.service.vb.migration;

import lithium.service.vb.migration.service.CachingMigrationClientService;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
@Import({CachingMigrationClientService.class})
@EnableCaching
public @interface EnableCachingMigrationClient {

}
