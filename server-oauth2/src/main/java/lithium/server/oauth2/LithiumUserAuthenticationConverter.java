package lithium.server.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Slf4j
public class LithiumUserAuthenticationConverter extends DefaultUserAuthenticationConverter {
 	private UserDetailsService userDetailsService;

	private static final String DOMAIN_NAME = "domainName";

 	public void setUserDetailsService(UserDetailsService userDetailsService) {
 		this.userDetailsService = userDetailsService;
 	}

 	@Override
 	public Authentication extractAuthentication(Map<String, ?> map) {
 		log.trace("extractAuthentication map: {}", map);
 		String domainName = (String) map.get(DOMAIN_NAME);
 		String username = (String) map.get(USERNAME);
 		log.trace("domainName: {}, username: {}", domainName, username);
        UserDetails user = userDetailsService.loadUserByUsername(domainName + "/" + username);
        return new UsernamePasswordAuthenticationToken(user, "N/A", getAuthorities(map));
 	}

 	public Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
 		if (!map.containsKey(AUTHORITIES)) {
 			return new ArrayList<>();
 		}
 		Object authorities = map.get(AUTHORITIES);
 		if (authorities instanceof String) {
 			return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
 		}
 		if (authorities instanceof Collection) {
 			return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
 					.collectionToCommaDelimitedString((Collection<?>) authorities));
 		}
 		throw new IllegalArgumentException("Authorities must be either a String or a Collection");
 	}
}