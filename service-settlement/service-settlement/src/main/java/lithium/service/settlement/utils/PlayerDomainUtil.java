package lithium.service.settlement.utils;

import lithium.tokens.JWTDomain;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlayerDomainUtil {
	public List<String> getDomains(LithiumTokenUtil tokenUtil, String role) {
		List<String> domains = new ArrayList<>();
		List<JWTDomain> jwtDomains = tokenUtil.playerDomainsWithRole(role);
		for (JWTDomain jwtDomain: jwtDomains) {
			domains.add(jwtDomain.getName());
		}
		return domains;
	}
}
