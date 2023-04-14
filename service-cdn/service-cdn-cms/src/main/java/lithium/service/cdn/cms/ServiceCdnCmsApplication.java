package lithium.service.cdn.cms;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.service.cdn.cms.services.ServiceCdnCmsInit;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

@EnableCustomHttpErrorCodeExceptions
@EnableLithiumServiceClients
@EnableChangeLogService
@LithiumService
public class ServiceCdnCmsApplication extends LithiumServiceApplication {

    @Autowired
    private ServiceCdnCmsInit init;

    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceCdnCmsApplication.class, args);
    }

    @EventListener
    public void startup(ApplicationStartedEvent e) throws Exception {
        super.startup(e);
        init.initDomains();
    }
}
