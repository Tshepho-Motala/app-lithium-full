package lithium.service.user.client;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bouncycastle.cert.ocsp.Req;
import lithium.service.user.client.objects.AuthRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.client.objects.User;

@FeignClient(name="service-user")
public interface UserClient {
	@RequestMapping(path = "/users/auth")
	public Response<User> auth(@RequestParam("domain") String domain, @RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("ipAddress") String ipAddress, @RequestParam("userAgent") String userAgent, @RequestParam(required=false) Map<String, String> extraParameters, @RequestParam("locale") String locale);

	@RequestMapping(value = "/users/auth", method = RequestMethod.POST)
	Response<User> auth(@RequestBody AuthRequest request);

	@RequestMapping(path = "/users/user")
	public Response<User> user(@RequestParam("domain") String domain, @RequestParam("username") String username, @RequestParam(required=false) Map<String, String> extraParameters);
	
	@RequestMapping(path = "/users/create", method = RequestMethod.POST)
	public Response<User> create(@RequestBody User user);
	
	@RequestMapping(path = "/users/d/{domainName}", method = RequestMethod.DELETE)
	public Response<String> delete(@PathVariable("domainName") String domain, @RequestParam(required=false) Map<String, String> extraParameters);
	
	@RequestMapping(path = "/{domainName}/users/table") 
	public DataTableResponse<User> table(
			@PathVariable("domainName") String domainName,
			@RequestParam("draw") String drawEcho,
			@RequestParam("start") Long start,
			@RequestParam("length") Long length,
			@RequestParam(name="domainNames", required=false) String[] domainNames,
			@RequestParam(name="players", required=false) Boolean players,
			@RequestParam(name="labelNameString", required=false) String labelNameString,
			@RequestParam(name="labelValueString", required=false) String labelValueString);

	@RequestMapping(path = "/{domainName}/users/table")
	public DataTableResponse<User> table(
			@PathVariable("domainName") String domainName,
			@RequestParam("draw") String drawEcho,
			@RequestParam("start") Long start,
			@RequestParam("length") Long length);

	@RequestMapping(value = "/{domainName}/users/find-users-by-usernames-or-guids", method = RequestMethod.POST)
	Response<List<User>> findByGuids(@PathVariable("domainName") String domainName, @RequestBody List<String> userGuids);
}
