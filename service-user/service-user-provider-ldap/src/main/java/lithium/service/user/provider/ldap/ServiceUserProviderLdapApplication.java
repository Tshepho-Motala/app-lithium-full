package lithium.service.user.provider.ldap;

import lithium.application.LithiumShutdownSpringApplication;

import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableLithiumServiceClients
public class ServiceUserProviderLdapApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceUserProviderLdapApplication.class, args);
	}
}
