package lithium.service.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lithium.service.Response;
import lithium.service.user.client.objects.UserPasswordReset;

@FeignClient(name="service-user")
public interface PasswordResetClient {
	@RequestMapping(path = "/{domainName}/{emailOrUsernameOrPhoneNumber}/passwordreset/step1")
	public Response<String> resetPassword(@PathVariable("domainName") String domainName, @PathVariable String emailOrUsernameOrPhoneNumber);
	
	@RequestMapping(path = "/{domainName}/{emailOrUsernameOrPhoneNumber}/passwordreset/step2")
	public Response<String> validateTokenAndResetPassword(@PathVariable("domainName") String domain, @PathVariable String emailOrUsernameOrPhoneNumber, @RequestBody UserPasswordReset userPasswordReset);
	
}