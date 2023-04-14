package lithium.services;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * Listener added this way can receive the events before ApplicationContext is created and loaded.
 */
public class EnvironmentPrepareListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
    // By default Embedded Tomcat reject requests with encoded slashes in uri (e.g. need it to passing guid as a @PathVariable)
    // this property will be removed starting Tomcat 10.
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
    }
}
