package lithium.service.affiliate.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lithium.service.affiliate.client.objects.Job;

// Contains exposed methods in provider implementations to be used by service-affiliate

@FeignClient(name="service-affiliate")
public interface AffiliateProviderClient {
	@RequestMapping(path="/affiliate-provider/job/{domain}/run", method=RequestMethod.POST)
	public void runJob(Job job);
//	
//	@RequestMapping(path="/affiliates/{domainName}/{id}", method=RequestMethod.GET)
//	public Response<User> get(@PathVariable("domainName") String domainName, @PathVariable("id") Long id);
//	
//	@RequestMapping(path="/affiliates/{domainName}/{id}/changepassword", method=RequestMethod.POST)
//	public Response<User> changePassword(@PathVariable("domainName") String domainName, @PathVariable("id") Long id, @RequestBody String password);
//	
//	@RequestMapping(path="/affiliates/{domainName}/{id}/save", method=RequestMethod.POST)
//	public Response<User> save(@PathVariable("domainName") String domainName, @PathVariable("id") Long id, @RequestBody User user);
}