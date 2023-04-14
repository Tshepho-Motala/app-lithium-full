package lithium.security;

import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.List;

@Slf4j
public class LithiumSecurityEvaluator { //implements PermissionEvaluator {
	private TokenStore tokenStore;
	
	public LithiumSecurityEvaluator(TokenStore tokenStore) {
		this.tokenStore = tokenStore;
	}
//	private String retrieveToken(HttpServletRequest request) throws JwtTokenMissingException {
//		String header = request.getHeader("Authorization");
//		if (header == null || !header.startsWith("Bearer ")) {
//			throw new JwtTokenMissingException("No JWT token found in request headers");
//		}
//		return header.substring(7);
//	}
//	public boolean hasRole(HttpServletRequest request, String role) throws JwtTokenMissingException {
//		String authToken = retrieveToken(request);
//		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
//		return util.hasRole(role);
//	}
	public boolean authenticatedSystem(Authentication authentication) {
		List<GrantedAuthority> list = AuthorityUtils.createAuthorityList("ROLE_SYSTEM");
		if (
			authentication.isAuthenticated() && 
			(
				authentication.getAuthorities().contains(list.get(0))
			)
			
		) {
			return true;
		}
		return false;
	}
	
	public boolean hasRole(Authentication authentication, String role) throws JwtTokenMissingException {
		String[] roles = {role};
		return hasRole(authentication, roles);
	}
	public boolean hasRole(Authentication authentication, String... roles) throws JwtTokenMissingException {
		boolean isSystemAuthed = authenticatedSystem(authentication);
		if(isSystemAuthed) return true;
		
		if (!isOAuthTypeToken(authentication)) return false;
		
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		if (util.hasAdminRole()) {
			return true;
		}
		for (String role:roles) {
			if (util.hasRole(role)) {
				return true;
			}
		}
		return false;
	}
	public boolean hasRoleInTree(Authentication authentication, String role) {
		String[] roles = {role};
		return hasRoleInTree(authentication, roles);
	}
	public boolean hasRoleInTree(Authentication authentication, String... roles) {
		boolean isSystemAuthed = authenticatedSystem(authentication);
		if(isSystemAuthed) return true;
		
		if (!isOAuthTypeToken(authentication)) return false;
		
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		if (util.hasAdminRole()) {
			return true;
		}
		for (String role:roles) {
			if (util.hasRoleInTree(role)) {
				return true;
			}
		}
		return false;
	}
	public boolean hasAllRolesForDomain(Authentication authentication, String domainName, String... roles) {
		if (authenticatedSystem(authentication)) return true;

		LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, authentication).build();

		if (tokenUtil.hasAdminRole()) return true;

		return tokenUtil.hasAllRolesForDomain(domainName, roles);
	}
	public boolean hasDomainRole(Authentication authentication, String domainName, String role) throws JwtTokenMissingException {
		String[] roles = {role};
		return hasDomainRole(authentication, domainName, roles);
	}
	public boolean hasDomainRole(Authentication authentication, String domainName, String... roles) throws JwtTokenMissingException {
		boolean isSystemAuthed = authenticatedSystem(authentication);
		if(isSystemAuthed) return true;
		
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		for (String role:roles) {
			if (util.hasRole(domainName, role)) {
				log.debug(util.domainName()+"/"+util.id()+" has role: "+role+" for : "+domainName);
				return true;
			}
		}
		if (util.hasAdminRole(domainName)) {
			log.info("Not Found : "+gatherRoles(roles)+" for : "+domainName+" but has ADMIN");
			return true;
		}
		log.info("Not Found : "+gatherRoles(roles)+" for : "+domainName);

		return false;
	}
	private String gatherRoles(String... roles) {
		String roleStr = "";
		for (String role:roles) {
			roleStr += (role+" | ");
		};
		return roleStr;
	}
	
	public boolean authenticatedApi(String api) {
		//TODO: Add some API auth in here to validate remote provider service calls
		return true;
	}
	
	private boolean isOAuthTypeToken(Authentication auth) {
		if(auth instanceof OAuth2Authentication) return true;
		
		return false;
	}
//	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
//		// TODO Auto-generated method stub
//		return false;
//	}
}