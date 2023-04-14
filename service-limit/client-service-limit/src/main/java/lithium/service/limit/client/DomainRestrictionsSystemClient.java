package lithium.service.limit.client;



import lithium.service.limit.client.objects.DomainRestriction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="service-limit")
public interface DomainRestrictionsSystemClient {

	@RequestMapping(method= RequestMethod.GET, path = "/system/restrictions/domain-restrictions")
	public List<DomainRestriction> getDomainRestrictions(@RequestParam("domainName") String domainName);

	@RequestMapping(method= RequestMethod.GET, path = "/system/restrictions/user-domain-restrictions")
	public List<DomainRestriction> getUserDomainRestrictions(@RequestParam("userGuid") String userGuid);
}
