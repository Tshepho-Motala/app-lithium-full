package lithium.service.affiliate.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lithium.service.Response;
import lithium.service.affiliate.client.objects.AffiliatePlayer;
import lithium.service.affiliate.client.objects.AffiliatePlayerBasic;

//Contains exposed methods in service-affiliate to be used by other services

@FeignClient(name="service-affiliate")
public interface AffiliateAPIClient {
	
	@RequestMapping("/affiliate/player/add")
	public Response<AffiliatePlayer> addAffiliatePlayer(@RequestBody AffiliatePlayerBasic player) throws Exception;
	
//	@RequestMapping(path="/affiliates/{domainName}/create", method=RequestMethod.POST)
//	public Response<User> create(@PathVariable("domainName") String domainName, @RequestBody User user);
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