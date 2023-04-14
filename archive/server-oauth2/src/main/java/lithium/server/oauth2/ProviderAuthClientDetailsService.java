package lithium.server.oauth2;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.ProviderAuthClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProviderAuthClientDetailsService implements ClientDetailsService {
	@Autowired LithiumServiceClientFactory services;

	private ProviderAuthClient providerAuthClient(String domainName, String code) throws ClientRegistrationException {
		try {
			DomainClient domainClient = services.target(DomainClient.class, "service-domain", true);
			Response<ProviderAuthClient> response = domainClient.findProviderAuthClient(domainName, code);
			if (response.isSuccessful()) {
				return response.getData();
			}
			return null;
		} catch (Exception e) {
			throw new ClientRegistrationException(e.getMessage());
		}
	}

	/**
	 * Load a client by the client id. This method must not return null.
	 *
	 * @param clientId The client id.
	 * @return The client details (never null).
	 * @throws ClientRegistrationException If the client account is locked, expired, disabled, or invalid for any other reason.
	 */
	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		log.debug("Searching for client details : "+clientId);
		ProviderAuthClientDetails client = ProviderAuthClientDetails.builder().clientId(clientId).build();
		if (clientId.equalsIgnoreCase("system")) {
			client = client.toBuilder()
				.clientSecret("VERYBLOODYgoodpasswordFORuseInSystemAUTHENTICATION99")
				.authority("ROLE_SYSTEM")
				.resourceId("oauth2-resource")
				.authorizedGrantType("client_credentials")
				.scope("any")
				.build();
		} else {
			try {
				if (clientId.equalsIgnoreCase("acme")) clientId = "default/acme"; //TODO: Can be removed at a much later stage, this is here to cater for acme:acmesecret that has been used since my life. Removing entry (default/acme) from provider_auth_client table will render this useless.
				ProviderAuthClient pac = providerAuthClient(clientId.split("/")[0], clientId.split("/")[1]);
				log.debug("ProviderAuthClient :: " + pac);
				if (pac == null) throw new ClientRegistrationException("Unknown Provider Auth Client");
				client = client.toBuilder()
					.clientSecret(pac.getPassword())
					.resourceId("oauth2-resource")
					.authorizedGrantType("authorization_code")
					.authorizedGrantType("refresh_token")
					.authorizedGrantType("password")
					.scope("openid")
					.build();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new ClientRegistrationException("Unknown Provider Auth Client");
			}
		}
		log.debug("returning : "+client);

		String resourceIds = client.getResourceIds().stream().collect(Collectors.joining(","));
		String scopes = client.getScopes().stream().collect(Collectors.joining(","));
		String grantTypes = client.getAuthorizedGrantTypes().stream().collect(Collectors.joining(","));
		String authorities = client.getAuthorities().stream().collect(Collectors.joining(","));

		BaseClientDetails base = new BaseClientDetails(client.getClientId(), resourceIds, scopes, grantTypes, authorities);
		base.setClientSecret(client.getClientSecret());
//		base.setAccessTokenValiditySeconds(client.getAccessTokenValiditySeconds());
//		base.setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds());
//		base.setAdditionalInformation(client.getAdditionalInformation());
		base.setAutoApproveScopes(client.getScopes());
		return base;
	}
}