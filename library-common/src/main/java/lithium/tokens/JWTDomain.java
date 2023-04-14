package lithium.tokens;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Used in the construction of the JWT object.
 * 
 * @author riaans
 */

@Data
@Slf4j
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JWTDomain {
	@JsonProperty("n")
	private String name;
	@JsonProperty("dn")
	private String displayName;
	@JsonProperty("pd")
	private Boolean playerDomain;
	@JsonProperty("p")
	private String parent;

	/**
	 * null represents not inside an ecosystem
	 * See lithium.service.domain.client.EcosystemRelationshipTypes
	 * and CachingDomainClientService#findEcosystemRelationshipTypeByDomainNameShort
	 */
	@JsonProperty("ert")
	private String ecosystemRelationshipType;
	@JsonProperty("r")
	private List<JWTRole> roles;
	
	public void addRole(JWTRole role) {
		if (roles == null) roles = new ArrayList<>();
		roles.add(role);
	}
	
	public boolean hasRole(String role) {
		if (roles == null) roles = new ArrayList<>();
		return roles.stream().anyMatch(r -> {
			log.trace("Checking Role: "+role+" == "+r.getName());
			if (r.getName().equals(role)) {
				return true;
			} else if (role.endsWith("_*")) {
				if (r.getName().startsWith(role.substring(0, role.length()-1))) {
					log.warn("Partial Match Found! "+r.getName()+"::"+role);
					return true;
				}
			}
			return false;
		});
	}
	
	public void replaceRole(JWTRole oldRole, JWTRole newRole) {
		if (roles == null) roles = new ArrayList<>();
		roles.removeIf(r -> (r.getName().equals(oldRole.getName())));
		roles.add(newRole);
	}
	
	public JWTRole findRole(String role) {
		if (roles == null) roles = new ArrayList<>();
		return roles.stream().filter(r -> {
			if (r.getName().equals(role)) {
				return true;
			} else if (role.endsWith("_*")) {
				if (r.getName().startsWith(role.substring(0, role.length()-1))) {
					log.warn("Partial Match Found! "+r.getName()+"::"+role);
					return true;
				}
			}
			return false;
		}).findFirst().orElse(null);
	}
	
	/*
	public List<JWTRole> allDescendingRoles() {
		return roles.stream().filter(r -> {
			if ((r.getDescending() != null) && (r.getDescending())) {
				return true;
			}
			return false;
		}).collect(Collectors.toList());
	}
	
	public List<JWTRole> allSelfRoles() {
		return roles.stream().filter(r -> {
			if ((r.getSelfApplied() != null) && (r.getSelfApplied())) {
				return true;
			}
			return false;
		}).collect(Collectors.toList());
	}
	*/
}