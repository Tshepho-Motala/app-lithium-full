package lithium.util;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class DomainValidationUtil {
	public static void validate(String source, String target) throws Status500InternalServerErrorException {
		if (!source.contentEquals(target)) {
			String msg = "Domain validation failed";
			log.error(msg + " [source="+source+", target="+target+"]");
			throw new Status500InternalServerErrorException(msg);
		}
	}

	public static void validate(String domainName, String role, LithiumTokenUtil tokenUtil)
			throws Status500InternalServerErrorException {
		if (!tokenUtil.hasRole(domainName, role)) {
			String msg = "Access to the resource is denied to the token holder";
			log.error(msg + " [domainName="+domainName+", role="+role+", guid="+tokenUtil.guid()+"]");
			throw new Status500InternalServerErrorException(msg);
		}
	}

	public static void validate(String domainName, LithiumTokenUtil tokenUtil, String... roles)
			throws Status500InternalServerErrorException {
		if (!tokenUtil.hasRolesForDomain(domainName, roles)) {
			String msg = "Access to the resource is denied to the token holder";
			log.error(msg + " [domainName="+domainName+", roles="+roles+", guid="+tokenUtil.guid()+"]");
			throw new Status500InternalServerErrorException(msg);
		}
	}

	public static void filterDomainsWithRole(String[] domains, String role, LithiumTokenUtil tokenUtil) {
		List<String> domainsWithRole = tokenUtil.domainsWithRole(role)
		.stream()
		.map(jwtDomain -> jwtDomain.getName())
		.collect(Collectors.toList());

		List<String> filteredDomains = new ArrayList<>();
		for (int index = 0; index < domains.length; index++) {
			String domain = domains[index];
			if (domainsWithRole.contains(domain)) {
				filteredDomains.add(domain);
			}
		}

		domains = filteredDomains.toArray(new String[filteredDomains.size()]);
		log.debug("Filtered domains: " + Arrays.toString(domains));
	}

	public static void filterDomainsWithRoles(String[] domains, LithiumTokenUtil tokenUtil, String... roles) {
		Set<String> domainsWithRole = new LinkedHashSet<>();
		for (String role: roles) {
			domainsWithRole.addAll(
				tokenUtil.domainsWithRole(role)
				.stream()
				.map(jwtDomain -> jwtDomain.getName())
				.collect(Collectors.toList())
			);
		}
		List<String> filteredDomains = new ArrayList<>();
		for (int index = 0; index < domains.length; index++) {
			String domain = domains[index];
			if (domainsWithRole.contains(domain)) {
				filteredDomains.add(domain);
			}
		}

		domains = filteredDomains.toArray(new String[filteredDomains.size()]);
		log.debug("Filtered domains: " + Arrays.toString(domains));
	}
}
