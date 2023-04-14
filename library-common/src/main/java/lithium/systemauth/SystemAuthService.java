package lithium.systemauth;

import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lithium.tokens.JWTUser;
import lithium.tokens.LithiumTokenUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SystemAuthService implements Runnable {

	@Autowired SystemAuthConfigurationProperties properties;
	
	private Thread thread = new Thread(this);
	private SystemAuthToken token = null;
	
	@PostConstruct
	public void init() {
		if (properties.getTokenUrl() == null || properties.getTokenUrl().isEmpty()) 
			throw new RuntimeException("lithium.system.auth.token-url not specified. Did you include the common application.yml?");
		if (properties.getUsername() == null || properties.getUsername().isEmpty()) 
			throw new RuntimeException("lithium.system.auth.username not specified.");
		if (properties.getPassword() == null || properties.getPassword().isEmpty())
			throw new RuntimeException("lithium.system.auth.password not specified.");

		if (properties.getStandalone() == null || !properties.getStandalone()) {
			log.info("Starting SystemAuthService");
			thread.start();
		} else {
			log.info("Skipping SystemAuthService");
		}
	}
	
	@PreDestroy
	public void destroy() {
		log.info("Stopping SystemAuthService");
		thread.interrupt();
		try {
			thread.join();
		} catch (InterruptedException ie) {}
	}
	
	public void run() {
		boolean renew = true;
		try {
			int failcount = 0;
			while (true) {
				try {
					if (renew) 
					synchronized (this) {
						token = obtain(properties);
						this.notifyAll();
						failcount = 0;
						renew = false;
					}
					log.debug("Obtained system token. Expires at " + token.getExpires());
				} catch (Exception e) {
					if (failcount++ > 10) {
						log.error("Unable to obtain system token. Retrying. (Fail Count:"+failcount+") " + e, e);
					} else {
						log.info("Unable to obtain system token. Retrying. (Fail Count:"+failcount+") " + e);
					}
				}
				
				if (token != null) {
					long tokenExpiresIn = token.getExpires().getMillis() - new Date().getTime();
					if (tokenExpiresIn < 120000) renew = true; 
				}
				
				Thread.sleep(10000);
			}
		} catch (InterruptedException ie) {
			
		}
	}
	
	public static SystemAuthToken obtain(SystemAuthConfigurationProperties properties) throws Exception {
		log.debug("Obtaining system token from " + properties.getTokenUrl() + " using username " + properties.getUsername());
		RestTemplate r = new RestTemplate();
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("grant_type", "client_credentials");
		HttpHeaders headers = new HttpHeaders();
		String username = properties.getUsername();
		String password = properties.getPassword();
		headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String,String>>(map, headers);
		ResponseEntity<String> result = r.exchange(properties.getTokenUrl(), HttpMethod.POST, request, String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(result.getBody());
		SystemAuthToken token = new SystemAuthToken();
		token.setToken(root.get("access_token").asText());
		token.setExpires(new DateTime().plusSeconds(root.get("expires_in").asInt()));
		return token;
	}

	public String getTokenValue() throws TimeoutException {

		if (!thread.isAlive()) throw new RuntimeException("The system authenticator service is not running. "
				+ "Did you specify @EnableSystemAuth?");
		
		if (token == null) {
			synchronized (this) {
				try {
					this.wait(10000);
				} catch (InterruptedException e) {
				}
			}
		}
		
		if (token == null) {
			throw new TimeoutException("Timed out waiting for system auth token");
		} else {
			return token.getToken();
		}
	}

	public LithiumTokenUtil getSystemLithiumTokenUtil(TokenStore tokenStore, String systemUserDomainName, String systemUserUsername) throws TimeoutException {
		LithiumTokenUtil systemToken = null;
		String systemTokenString = getTokenValue();
		systemToken = LithiumTokenUtil.builder(tokenStore, systemTokenString).build();
		systemToken.setJwtUser(
				JWTUser.builder()
						.domainName(systemUserDomainName)
						.username(systemUserUsername)
						.guid(systemUserDomainName + "/" + systemUserUsername) //Need to do this so the token util does not try and force an ID guid in this fake user
						.build());
		return systemToken;
	}
}
