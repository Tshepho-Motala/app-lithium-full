package lithium.service.access.provider.sphonic.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.modules.ModuleInfo;
import lithium.service.access.provider.sphonic.data.entities.Authentication;
import lithium.service.access.provider.sphonic.data.entities.Domain;
import lithium.service.access.provider.sphonic.data.objects.AuthenticationConfiguration;
import lithium.service.access.provider.sphonic.data.repositories.DomainRepository;
import lithium.service.access.provider.sphonic.data.repositories.SphonicAuthenticationRepository;
import lithium.service.access.provider.sphonic.schema.AuthenticationRequest;
import lithium.service.access.provider.sphonic.schema.AuthenticationResponse;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SphonicAuthenticationService {
	@Autowired private CachingDomainClientService cachingDomainClientService;
	@Autowired private DomainRepository domainRepository;
	@Autowired private SphonicHTTPService sphonicHTTPService;
	@Autowired private ProviderClientService providerClientService;
	@Autowired private RestService restService;

	private static final String PROVIDER_PROPERTY_AUTHENTICATION_URL = "AuthenticationUrl";
	private static final String PROVIDER_PROPERTY_USERNAME = "Username";
	private static final String PROVIDER_PROPERTY_PASSWORD = "Password";
	private static final String PROVIDER_PROPERTY_EXPIRATION_DELAY = "ExpirationDelay";
	private static final int DEFAULT_EXPIRATION_DELAY = 120;
	private static final String CONNECTION_REQUEST_TIMEOUT= "connectionRequestTimeout";
	private static final String CONNECT_TIMEOUT = "connectTimeout";
	private static final String SOCKET_TIMEOUT = "socketTimeout";

	//FIXME to be fixed on https://jira.livescore.com/browse/PLAT-6748: overloading this method for now, until iDin implements timeout configurations
	// then all other services to follow MUST implement timeout configurations
	public String getAccessToken(SphonicAuthenticationRepository repository, String domainName, String url,
	        String username, String password) throws Status500InternalServerErrorException,
			Status512ProviderNotConfiguredException {
		return getAccessToken(repository, domainName, url, username, password, 60000, 60000, 60000);
	}

	public String getAccessToken(SphonicAuthenticationRepository repository, String domainName, String url,
								 String username, String password,
								 Integer connectTimeout, Integer connectionRequestTimeout, Integer socketTimeout) throws Status500InternalServerErrorException,
			Status512ProviderNotConfiguredException {
		log.trace("Get access token");
		Authentication authentication = repository.findByDomainName(domainName);
		if ((authentication == null) ||
				(authentication != null && authentication.getExpirationDate().before(new Date()))) {
			log.warn("No token available or token is expired, fallback to using HTTP for authentication");
			return authenticate(repository, domainName, url, username, password, null, null, false,
					connectTimeout, connectionRequestTimeout, socketTimeout);
		}
		return authentication.getAccessToken();
	}

	public void evictExpiredTokens(SphonicAuthenticationRepository repository) {
		log.trace("Evict all tokens");
		Iterable<Authentication> authentications = repository.findAll();
		Iterator<Authentication> iterator = authentications.iterator();
		while (iterator.hasNext()) {
			Authentication authentication = iterator.next();
			if (authentication.getExpirationDate().before(new Date())) {
				log.trace("Expired token=" + authentication.getAccessToken() + "found for domain=" + authentication.getDomain().getName() + ". Evicting now."
						+ " [expirationDate="+authentication.getExpirationDate()+"]");
				repository.deleteByDomainName(authentication.getDomain().getName());
			}
		}
	}

	@TimeThisMethod
	public void authenticateAllDomains(ModuleInfo moduleInfo, SphonicAuthenticationRepository repository) {
		log.trace("Authenticate all domains");
		try {
			SW.start("findAllPlayerDomains");
			List<lithium.service.domain.client.objects.Domain> playerDomains = cachingDomainClientService
					.getDomainClient().findAllPlayerDomains().getData();
			SW.stop();
			if (playerDomains != null) {
				for (lithium.service.domain.client.objects.Domain domain: playerDomains) {
					SW.start(domain.getName() + "_findByUrlAndDomainName");
					Provider provider = providerClientService.findProviderByUrlAndDomainNameIfExists(
							moduleInfo.getModuleName(), domain.getName());
					SW.stop();
					if (provider != null && BooleanUtils.isTrue(provider.getEnabled())) {
						Authentication authentication = repository.findByDomainName(domain.getName());
						if (authentication == null) {
							log.trace("Provider " + moduleInfo.getModuleName() + " is setup for " + domain.getName()
									+ " and there is no token stored. Going to authenticate and store the token.");
							SW.start("getAuthenticationConfiguration");
							AuthenticationConfiguration configuration = getAuthenticationConfiguration(moduleInfo,
									domain.getName());
							SW.stop();
							SW.start("authenticate");
							authenticate(repository, domain.getName(), configuration.getAuthenticationUrl(),
									configuration.getUsername(), configuration.getPassword(), configuration.getExpirationDelay(), moduleInfo.getModuleName(), true,
									configuration.getConnectTimeout(), configuration.getConnectionRequestTimeout(), configuration.getSocketTimeout());
							SW.stop();

						}
					} else {
						log.trace("Provider " + moduleInfo.getModuleName() + " is not setup for " + domain.getName());
					}
				}
			}
		} catch (Exception e) {
			log.error("Failed to authenticate all domains | " + e.getMessage(), e);
		}
	}

	private AuthenticationConfiguration getAuthenticationConfiguration(ModuleInfo moduleInfo, String domainName)
			throws Status512ProviderNotConfiguredException, Status550ServiceDomainClientException {
		Iterable<ProviderProperty> properties = providerClientService.getProviderProperties(moduleInfo.getModuleName(),
				domainName);

		AuthenticationConfiguration configuration = new AuthenticationConfiguration();
		for (ProviderProperty property: properties) {
			if (property.getName().contentEquals(PROVIDER_PROPERTY_AUTHENTICATION_URL))
				configuration.setAuthenticationUrl(property.getValue());
			if (property.getName().contentEquals(PROVIDER_PROPERTY_USERNAME))
				configuration.setUsername(property.getValue());
			if (property.getName().contentEquals(PROVIDER_PROPERTY_PASSWORD))
				configuration.setPassword(property.getValue());
			if(property.getName().contentEquals(PROVIDER_PROPERTY_EXPIRATION_DELAY)) {
				String value  = StringUtil.isNumeric(property.getValue()) ? property.getValue() : String.valueOf(DEFAULT_EXPIRATION_DELAY);
				configuration.setExpirationDelay(Optional.ofNullable(value).map(Integer::parseInt).orElse(null));
			}
			if (property.getName().contentEquals(CONNECT_TIMEOUT) && !StringUtil.isEmpty(property.getValue()) && StringUtil.isNumeric(property.getValue()))
				configuration.setConnectTimeout(Integer.parseInt(property.getValue()));
			if (property.getName().contentEquals(CONNECTION_REQUEST_TIMEOUT) && !StringUtil.isEmpty(property.getValue()) && StringUtil.isNumeric(property.getValue()))
				configuration.setConnectionRequestTimeout(Integer.parseInt(property.getValue()));
			if (property.getName().contentEquals(SOCKET_TIMEOUT) && !StringUtil.isEmpty(property.getValue()) && StringUtil.isNumeric(property.getValue()))
				configuration.setSocketTimeout(Integer.parseInt(property.getValue()));
		}

		List<String> missingProperties = new ArrayList<>();
		if (configuration.getAuthenticationUrl() == null || configuration.getAuthenticationUrl().trim().isEmpty()) {
			missingProperties.add(PROVIDER_PROPERTY_AUTHENTICATION_URL);
		}
		if (configuration.getUsername() == null || configuration.getUsername().trim().isEmpty()) {
			missingProperties.add(PROVIDER_PROPERTY_USERNAME);
		}
		if (configuration.getPassword() == null || configuration.getPassword().trim().isEmpty()) {
			missingProperties.add(PROVIDER_PROPERTY_PASSWORD);
		}
		if (!missingProperties.isEmpty()) {
			String missingPropertiesStr = missingProperties.stream().collect(Collectors.joining(", "));
			throw new IllegalArgumentException("One or more required configuration properties not set."
					+ " ["+missingPropertiesStr+"]");
		}

		return configuration;
	}

	private String authenticate(SphonicAuthenticationRepository repository, String domainName, String url,
								String username, String password, Integer expirationDelay, String moduleName, boolean store,
								Integer connectTimeout, Integer connectionRequestTimeout, Integer socketTimeout) throws Status500InternalServerErrorException {
		log.trace("Authenticating " + domainName);
		AuthenticationResponse authenticationResponse = authenticate(url, username, password, connectTimeout, connectionRequestTimeout, socketTimeout);
		if (store) {
			// Shave off 2 minutes from token expiration, so that we can preemptively request a new token prior
			// to expiration.
			DateTime expirationDt = DateTime.now().plusSeconds(authenticationResponse.getExpiresIn().intValue() - Optional.ofNullable(expirationDelay).orElse(DEFAULT_EXPIRATION_DELAY));
			Domain domain = domainRepository.findOrCreateByName(domainName, () -> new Domain());

			repository.save(Authentication.builder()
					.domain(domain)
					.accessToken(authenticationResponse.getAccessToken())
					.expirationDate(expirationDt.toDate())
					.build());
			log.info("Sphonic access_token is saved [access_token=" + authenticationResponse.getAccessToken() + "; domain=" + domain + "; expiration_time=" + expirationDt.toString() + "; moduleName=" + moduleName + "]");
		}
		return authenticationResponse.getAccessToken();
	}

	private AuthenticationResponse authenticate(String url, String username, String password)
			throws Status500InternalServerErrorException {
		try {
			AuthenticationRequest request = AuthenticationRequest.builder()
					.username(username)
					.password(password)
					.build();
			return sphonicHTTPService.postForForm(url, request, AuthenticationResponse.class);
		} catch (Exception e) {
			String msg = "Sphonic authentication failed";
			log.error(msg + " | " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
	}

	private AuthenticationResponse authenticate(String url, String username, String password,
												Integer connectTimeout, Integer connectionRequestTimeout, Integer socketTimeout)
			throws Status500InternalServerErrorException {
		sphonicHTTPService.setRest(restService.restTemplate(connectTimeout, connectionRequestTimeout, socketTimeout));
		return authenticate(url, username, password);
	}
}
