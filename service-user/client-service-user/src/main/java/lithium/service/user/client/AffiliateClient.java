package lithium.service.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.user.client.objects.User;

@FeignClient(name="service-user")
public interface AffiliateClient {
	@RequestMapping(path="/affiliates/{domainName}/create", method=RequestMethod.POST)
	public Response<User> create(@PathVariable("domainName") String domainName, @RequestBody User user);
	
	@RequestMapping(path="/affiliates/{domainName}/{id}", method=RequestMethod.GET)
	public Response<User> get(@PathVariable("domainName") String domainName, @PathVariable("id") Long id);
	
	@RequestMapping(path="/affiliates/{domainName}/{id}/changepassword", method=RequestMethod.POST)
	public Response<User> changePassword(@PathVariable("domainName") String domainName, @PathVariable("id") Long id, @RequestBody String password);
	
	@RequestMapping(path="/affiliates/{domainName}/{id}/save", method=RequestMethod.POST)
	public Response<User> save(@PathVariable("domainName") String domainName, @PathVariable("id") Long id, @RequestBody User user);
	
	@RequestMapping(path="/affiliates/{domainName}/isunique/username", method=RequestMethod.GET)
	public Response<Boolean> isUniqueUsername(@PathVariable("domainName") String domainName, @RequestParam("username") String username);
	
	@RequestMapping(path="/affiliates/{domainName}/isunique/email", method=RequestMethod.GET)
	public Response<Boolean> isUniqueEmail(@PathVariable("domainName") String domainName, @RequestParam("email") String email);
}