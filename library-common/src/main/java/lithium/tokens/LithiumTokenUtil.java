package lithium.tokens;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.UserGuidStrategy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Slf4j
public class LithiumTokenUtil {
	/**
	 * This needs to be saved as a static variable in LithiumTokenUtil, so that LithiumTokenUtil is able to determine the guid
	 * using the stategy defined, should the guid be null.
	 * @see: lithium.services.LithiumServiceApplication.startup)
	 */
	private static UserGuidStrategy userGuidStrategy;

	private String tokenValue;
	private TokenStore tokenStore;
	private OAuth2Authentication authentication;
	private OAuth2AccessToken accessToken = null;
	private ObjectMapper mapper = new ObjectMapper();
	private JWTUser jwtUser = null;
	private Locale locale;
	
	//TODO: Riaan to add private key authentication when receiving a token for decode
	public Long id() {
		return jwtUser.getId();
	}
	public Long sessionId() { return jwtUser.getSessionId(); }
	public String username() {
		return jwtUser.getUsername();
	}
	public String guid() {
		if (jwtUser.guid() == null) {
			log.trace("GUID_STRATEGY: "+userGuidStrategy.name());
			if (userGuidStrategy == null) throw new RuntimeException("PlayerGuidStrategy not defined!");
			switch (userGuidStrategy) {
				case ID:
					if (id() == null) throw new RuntimeException("Player Id is null but player guid strategy set to ID.");
					jwtUser.setGuid(domainName() + "/" + id());
					break;
				default:
				case USERNAME:
					jwtUser.setGuid(domainName() + "/" + username());
					break;
			}
		}
		return jwtUser.getGuid();
	}
	public String firstName() {
		return jwtUser.getFirstName();
	}
	public String lastName() {
		return jwtUser.getLastName();
	}
	public String userLegalName() {		return jwtUser.getFirstName() + " " + jwtUser.getLastName();
	}
	public String email() {
		return jwtUser.getEmail();
	}
	public Long domainId() {
		return jwtUser.getDomainId();
	}
	public String domainName() {
		return jwtUser.getDomainName();
	}
	public String apiToken() {
		return jwtUser.getApiToken();
	}
	
	public boolean hasAdminRole() {
		return hasAdminRole(domainName());
	}
	public boolean hasAdminRole(String domain) {
		return hasRole(domain, "ADMIN");
	}
	
	public boolean hasRole(String role) {
		return hasRole(domainName(), role);
	}
	public boolean hasRoles(String... roles) {
		for (String role:roles) {
			if (hasRole(domainName(), role)) {
				return true;
			}
		}
		return false;
	}
	public boolean hasRole(String domain, String role) {
		return jwtUser.hasRole(domain, role);
	}
	public boolean hasRolesForDomain(String domain, String... roles) {
		for (String role:roles) {
			if (hasRole(domain, role)) {
				return true;
			}
		}
		return false;
	}
	public boolean hasAllRolesForDomain(String domain, String... roles) {
		for (String role: roles) {
			if (!hasRole(domain, role)) return false;
		}
		return true;
	}

	public boolean hasRoleInTree(String role) {
		return jwtUser.hasRoleInTree(role);
	}
	
	public boolean hasRoleOnlyDescending(String role) {
		return hasRoleOnlyDescending(domainName(), role);
	}
	public boolean hasRoleOnlyDescending(String domain, String role) {
		return jwtUser.hasRoleOnlyDescending(domain, role);
	}
	
	public JWTDomain playerDomainWithRole(String role) throws Exception {
		List<JWTDomain> domains = playerDomainsWithRole(role);
		if (domains.size() == 0)
			throw new Exception("There are no domains that match your requirement");
		if (domains.size() > 1)
			throw new Exception("There is more than one domain that matches your requirement");
		return domains.get(0);
	}
	
	public List<JWTDomain> playerDomainsWithRole(String role) {
		return domainsWithRole(role).stream().filter(d -> {
			if (d.getPlayerDomain() != null) {
				return d.getPlayerDomain();
			} else {
				return false;
			}
		}).collect(Collectors.toList());
	}

	public List<JWTDomain> playerDomainsWithRoles(String... roles) {
		Set<JWTDomain> domains = new LinkedHashSet<>();
		for (String role: roles) {
			List<JWTDomain> list = domainsWithRole(role).stream().filter(d -> {
				if (d.getPlayerDomain() != null) {
					return d.getPlayerDomain();
				} else {
					return false;
				}
			}).collect(Collectors.toList());
			domains.addAll(list);
		}
		return new ArrayList<>(domains);
	}
	
	public List<JWTDomain> domainsWithRole(String role) {
		return jwtUser.domainsWithRole(role);
	}
	
	public List<String> roles() {
		return roles(domainName());
	}
	public List<String> roles(String domain) {
		return jwtUser.roles(domain);
	}
	
	public List<String> domains() {
		List<String> domains = new ArrayList<>();
		jwtUser.getDomains().forEach(d -> {
			domains.add(d.getName());
		});
		return domains;
	}
	
	public JWTDomain domain(String domainName) {
		return jwtUser.getDomains().stream()
		.filter(d -> {
			if (d.getName().equalsIgnoreCase(domainName)) {
				return true;
			}
			return false;
		})
		.findFirst()
		.orElse(null);
	}
	
	private String doDecompress(String compressed) throws IOException {
		String decompressed = new String(IO.uncompressString(compressed));
		log.debug("DeCompressing jwtUser :: Size Before : "+compressed.length()+" || Size After : "+decompressed.length());
		return decompressed;
	}
	
	private void readDomainRoles() {
		String jwtUserJson = (String)accessToken.getAdditionalInformation().getOrDefault("jwtUser", "");
		try {
			if (jwtUserJson!=null && !jwtUserJson.isEmpty()) {
				String decompressedJwtUser = doDecompress(jwtUserJson);
				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				jwtUser = mapper.readValue(decompressedJwtUser, JWTUser.class);
				guid();
			} else {
				log.debug("jwtUser empty.. minimal token request ??");
				jwtUser = JWTUser.builder().build();

				//This is only relevant if the "Allow Minimal Token" setting is activated on the domain.
				jwtUser.setGuid((String)accessToken.getAdditionalInformation().getOrDefault("userGuid", ""));
				jwtUser.setDomainName((String)accessToken.getAdditionalInformation().getOrDefault("domainName", ""));
				jwtUser.setUsername((String)accessToken.getAdditionalInformation().getOrDefault("username", ""));
				Integer id = (Integer) accessToken.getAdditionalInformation().getOrDefault("userId", null);
				if(id != null ) {
					jwtUser.setId(Long.valueOf(id.longValue()));
				}

				//log.info("jwtUser empty.. system request ??");
				//jwtUser = JWTUser.builder().build();
			}
		} catch (Exception e) {
			log.error("Could not parse JWTUser.", e);
			jwtUser = JWTUser.builder().build();
		}
	}
	
	private void readAccessToken() {
		accessToken = tokenStore.readAccessToken(tokenValue);
	}

	/**
	 * This needs to be saved as a static variable in LithiumTokenUtil, so that LithiumTokenUtil is able to determine the guid
	 * using the stategy defined, should the guid be null.
	 * @see: lithium.services.LithiumServiceApplication.startup)
	 */
	public static void setUserGuidStrategy(UserGuidStrategy userGuidStrategy) {
		LithiumTokenUtil.userGuidStrategy = userGuidStrategy;
	}

	public static UserGuidStrategy getUserGuidStrategy() {
		return LithiumTokenUtil.userGuidStrategy;
	}
	
	private LithiumTokenUtil(TokenStore tokenStore, OAuth2Authentication authentication, String tokenValue) {
		this.tokenStore = tokenStore;
		this.authentication = authentication;
		this.tokenValue = tokenValue;
		readAccessToken();
		readDomainRoles();
	}
	
	public static LithiumTokenUtilBuilder builder(TokenStore tokenStore, String tokenValue) {
		return new LithiumTokenUtilBuilder(tokenStore, tokenValue);
	}
	public static LithiumTokenUtilBuilder builder(TokenStore tokenStore, OAuth2Authentication authentication) {
		return new LithiumTokenUtilBuilder(tokenStore, authentication);
	}
	public static LithiumTokenUtilBuilder builder(TokenStore tokenStore, Principal principal) {
		return new LithiumTokenUtilBuilder(tokenStore, principal);
	}
	
	public static class LithiumTokenUtilBuilder {
		private String tokenValue;
		private TokenStore tokenStore;
		private OAuth2Authentication authentication;
		
		public LithiumTokenUtilBuilder(TokenStore tokenStore, OAuth2Authentication authentication) {
			this.tokenStore = tokenStore;
			this.authentication = authentication;
			this.tokenValue = ((OAuth2AuthenticationDetails)authentication.getDetails()).getTokenValue();
		}
		public LithiumTokenUtilBuilder(TokenStore tokenStore, Principal principal) {
			if (!(principal instanceof OAuth2Authentication)) {
				throw new OAuth2Exception("Principal not instanceof OAuth2Authentication");
			}
			this.tokenStore = tokenStore;
			this.authentication = (OAuth2Authentication)principal;
			this.tokenValue = ((OAuth2AuthenticationDetails)authentication.getDetails()).getTokenValue();
		}
		public LithiumTokenUtilBuilder(TokenStore tokenStore, String tokenValue) {
			this.tokenStore = tokenStore;
			this.tokenValue = tokenValue;
			this.authentication = tokenStore.readAuthentication(tokenValue);
		}
		public LithiumTokenUtil build() {
			LithiumTokenUtil lithiumTokenUtil = new LithiumTokenUtil(tokenStore, authentication, tokenValue);
			return lithiumTokenUtil;
		}
	}
}
