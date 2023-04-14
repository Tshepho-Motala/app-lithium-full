package lithium.service.sms.provider.mobivate;


import lithium.application.LithiumShutdownSpringApplication;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.sms.LithiumServiceProviderApplication;
import lithium.services.LithiumService;

@LithiumService
@EnableLithiumServiceClients
public class ServiceSMSProviderMobivateApplication extends LithiumServiceProviderApplication {
    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceSMSProviderMobivateApplication.class, args);
    }
}