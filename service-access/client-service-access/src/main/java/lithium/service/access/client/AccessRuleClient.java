package lithium.service.access.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.access.client.objects.AccessRule;

@FeignClient("service-access")
public interface AccessRuleClient {
	@RequestMapping("/accessrules/{domainName}/{accessRuleName}")
	public Response<AccessRule> findByName(@PathVariable("domainName") String domainName, @PathVariable("accessRuleName") String accessRuleName);
	
	@RequestMapping("/accessrules/findByValue")
	public Response<List<AccessRule>> findByValue(
		@RequestParam("domainName") String domainName,
		@RequestParam("listName") String listName,
		@RequestParam("accessRuleName") String accessRuleName,
		@RequestParam("value") String value
	);
	
	@RequestMapping("/accessrules/addValueInListInRuleInDomain")
	public Response<List<AccessRule>> addValueInListInRuleInDomain(
		@RequestParam("domainName") String domainName,
		@RequestParam("listName") String listName,
		@RequestParam("accessRuleName") String accessRuleName,
		@RequestParam("value") String value
	);
}