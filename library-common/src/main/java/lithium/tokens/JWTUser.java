package lithium.tokens;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Used in the construction of the JWT object.
 * 
 * @author riaans
 */

@Data
@Slf4j
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class JWTUser {
	@JsonProperty("i")
	private Long id;
	@JsonProperty("u")
	private String username;
	@JsonProperty("f")
	private String firstName;
	@JsonProperty("l")
	private String lastName;
	@JsonProperty("e")
	private String email;
	@JsonProperty("di")
	private Long domainId;
	@JsonProperty("dn")
	private String domainName;
	@JsonProperty("ddn")
	private String domainDisplayName;
	@JsonProperty("pd")
	private Boolean playerDomain;
	@JsonProperty("ds")
	private List<JWTDomain> domains;
	@JsonProperty("a")
	private String apiToken;
	@JsonProperty("sg")
	private String shortGuid;
	@JsonProperty("g")
	private String guid;
	@JsonProperty("sid")
	private Long sessionId;

	public String guid() {
		return getGuid();
	}
	
	public void addDomain(JWTDomain domain) {
		if (domains == null) domains = new ArrayList<>();
		domains.add(domain);
	}
	public boolean hasDomain(String name) {
		if (domains == null) domains = new ArrayList<>();
		return domains.stream()
		.anyMatch(d -> {
			if (d.getName().equalsIgnoreCase(name)) {
				return true;
			}
			return false;
		});
	}
	public JWTDomain findDomain(String name) {
		if (domains == null) domains = new ArrayList<>();
		return domains.stream()
		.filter(d -> {
			if (d.getName().equalsIgnoreCase(name)) {
				return true;
			}
			return false;
		}).findFirst().orElse(null);
	}
	
	public List<JWTDomain> domainsWithRole(String role) {
		List<JWTDomain> domainsWithRole = new ArrayList<>();
		domains.stream().forEach(d -> {
			if (hasRole(d.getName(), role)) {
				domainsWithRole.add(d);
			}
		});
		return domainsWithRole;
	}
	
	public boolean hasRoleOnlyDescending(String role) {
		return hasRoleOnlyDescending(getDomainName(), role);
	}
	
	public boolean hasRoleOnlyDescending(String domain, String role) {
		JWTDomain jwtDomain = findDomain(domain);
		if (jwtDomain == null) {
			return false;
		} else {
			JWTRole jwtRole = jwtDomain.findRole(role);
			if ((jwtRole!=null) && ((jwtRole.getDescending()!=null)?jwtRole.getDescending():true)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean hasRoleInTree(String role) {
		for (JWTDomain jwtDomain:domains) {
			if (jwtDomain.hasRole(role)) {
				return true;
			}
		}
		log.debug("hasRoleInTree failed to find : "+role);
		log.debug("domains : "+domains);
		return false;
	}

	public boolean hasRole(String role) {
		return hasRole(getDomainName(), role);
	}
	
	public boolean hasRole(String domain, String role) {
		JWTDomain jwtDomain = findDomain(domain);
		if (jwtDomain == null) {
			return false;
		} else {
			if (!role.equals("ADMIN") && hasRole(getDomainName(), "ADMIN")) return true;
			JWTRole jwtRole = jwtDomain.findRole(role);
			if ((jwtRole!=null) && ((jwtRole.getSelfApplied()!=null)?jwtRole.getSelfApplied():true)) {
				return true;
			} else {
				JWTDomain jwtParentDomain = findDomain(jwtDomain.getParent());
				if (jwtParentDomain == null) {
					return false;
				} else {
					JWTRole jwtParentRole = jwtParentDomain.findRole(role);
					if (jwtParentRole!=null) {
						return ((jwtParentRole.getDescending()!=null)?jwtParentRole.getDescending():false);
					} else {
						return hasRole(jwtParentDomain.getName(), role);
					}
				}
			}
		}
	}
	
	public List<String> roles() {
		return roles(getDomainName(), false);
	}
	
	public List<String> roles(String domain) {
		return roles(domain, false);
	}
	
	private List<String> roles(String domain, boolean busyTraversing) {
		List<String> roles = new ArrayList<>();
		JWTDomain jwtDomain = findDomain(domain);
		if (jwtDomain == null) {
			return null;
		} else {
			if (jwtDomain.getRoles() != null) {
				jwtDomain.getRoles().stream().forEach(r -> {
					if ((r.getSelfApplied()) && (!busyTraversing)) {
						roles.add(r.getName());
					}
					if ((r.getDescending()) && (busyTraversing)) {
						roles.add(r.getName());
					}
				});
			}
			JWTDomain jwtParentDomain = findDomain(jwtDomain.getParent());
			if (jwtParentDomain != null) {
				jwtParentDomain.getRoles().stream().forEach(r -> {
					if (r.getDescending()) {
						roles.add(r.getName());
					}
				});
				if (jwtParentDomain.getParent() != null) {
					roles.addAll(roles(jwtParentDomain.getParent(), true));
				}
			}
		}
		return roles;
	}
}
