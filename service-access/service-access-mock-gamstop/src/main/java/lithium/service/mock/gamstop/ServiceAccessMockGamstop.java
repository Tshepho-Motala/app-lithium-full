package lithium.service.mock.gamstop;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableLithiumServiceClients
public class ServiceAccessMockGamstop extends LithiumServiceApplication {

    public static void main(String[] args){
        LithiumShutdownSpringApplication.run(ServiceAccessMockGamstop.class, args);
    }
}
