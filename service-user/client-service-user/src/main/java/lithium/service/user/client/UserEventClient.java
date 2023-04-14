package lithium.service.user.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lithium.service.Response;
import lithium.service.user.client.objects.UserEvent;

@FeignClient(name="service-user")
public interface UserEventClient {
	/* should be used from authenticated systems (e.g service-casino) */
	
	@RequestMapping(path = "/userevent/system/{domainName}/{userName}/stream")
	public Response<UserEvent> streamUserEvent(@PathVariable("domainName") String domainName, @PathVariable("userName") String userName, @RequestBody UserEvent userEvent);
	
	@RequestMapping(method = RequestMethod.POST, path = "/userevent/system/{domainName}/{userName}/register")
	public Response<UserEvent> registerEvent(@PathVariable("domainName") String domainName, @PathVariable("userName") String userName, @RequestBody UserEvent userEvent);
	
	@RequestMapping(path = "/userevent/system/{domainName}/{userName}/get")
	public Response<List<UserEvent>> getEvents(@PathVariable("domainName") String domainName, @PathVariable("userName") String userName);
	
	@RequestMapping(path = "/userevent/system/{domainName}/{userName}/{type}/get")
	public Response<List<UserEvent>> getEventsByType(@PathVariable("domainName") String domainName, @PathVariable("userName") String userName, @PathVariable("type") String type);
	
	@RequestMapping(path = "/userevent/system/{domainName}/{userName}/{id}/getuserevent")
	public Response<UserEvent> getUserEvent(@PathVariable("domainName") String domainName, @PathVariable("userName") String userName, @PathVariable("id") Long id);
	
	@RequestMapping(method = RequestMethod.POST, path = "/userevent/system/{domainName}/{userName}/{id}/received")
	public Response<UserEvent> markReceived(@PathVariable("domainName") String domainName, @PathVariable("userName") String userName, @PathVariable("id") Long id);
	
	/* should be used from website (passing required player authentication)	*/
	
	@RequestMapping(method = RequestMethod.GET, path = "/userevent/{domainName}/{id}/getuserevent")
	public Response<UserEvent> getUserEvent(@PathVariable("domainName") String domainName, @PathVariable("id") Long id);
	
	@RequestMapping(path = "/userevent/{domainName}/get")
	public Response<List<UserEvent>> getUserEvents(@PathVariable("domainName") String domainName);
	
	@RequestMapping(path = "/userevent/{domainName}/{type}/get")
	public Response<List<UserEvent>> getUserEventsByType(@PathVariable("domainName") String domainName, @PathVariable("type") String type);
	
	@RequestMapping(method = RequestMethod.POST, path = "/userevent/{domainName}/{id}/received")
	public Response<UserEvent> markUserEventReceived(@PathVariable("domainName") String domainName, @PathVariable("id") Long id);
}