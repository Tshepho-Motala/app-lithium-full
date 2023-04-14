package lithium.service.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;

@FeignClient(name="service-user")
public interface EmailValidationClient {
	@RequestMapping(path = "/{domainName}/{emailOrUsername}/emailvalidation/step1")
	public Response<String> validateEmail(@PathVariable("domainName") String domainName, @PathVariable("emailOrUsername") String emailOrUsername);
	
	@RequestMapping(path = "/{domainName}/{emailOrUsername}/emailvalidation/step2")
	public Response<String> validateTokenAndSetEmailValidated(@PathVariable("domainName") String domainName, @PathVariable("emailOrUsername") String emailOrUsername, @RequestParam(name="resend", required=false) Boolean resend, @RequestParam("token") String token);
}