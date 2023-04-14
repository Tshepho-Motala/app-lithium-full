package lithium.service.user.controllers;

import static lithium.service.Response.Status.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.tokens.JWTDomain;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/security")
public class SecurityController {
	@Autowired
	private TokenStore tokenStore;
	
	@RequestMapping("/id")
	public Response<Long> id(Authentication authentication) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		log.info("id : "+util.id());
		return Response.<Long>builder().data(util.id()).status(OK).build();
	}
	
	@RequestMapping("/username")
	public Response<String> username(Authentication authentication) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		return Response.<String>builder().data(util.username()).status(OK).build();
	}
	
	@RequestMapping("/firstName")
	public Response<String> firstName(Authentication authentication) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		return Response.<String>builder().data(util.firstName()).status(OK).build();
	}
	
	@RequestMapping("/lastName")
	public Response<String> lastName(Authentication authentication) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		return Response.<String>builder().data(util.lastName()).status(OK).build();
	}
	
	@RequestMapping("/email")
	public Response<String> email(Authentication authentication) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		return Response.<String>builder().data(util.email()).status(OK).build();
	}
	
	@RequestMapping("/domainId")
	public Response<Long> domainId(Authentication authentication) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		return Response.<Long>builder().data(util.domainId()).status(OK).build();
	}
	
	@RequestMapping("/domainName")
	public Response<String> domainName(Authentication authentication) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		return Response.<String>builder().data(util.domainName()).status(OK).build();
	}
	
	@RequestMapping("/roleadmin")
	public Response<Boolean> roleadmin(Authentication authentication) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		return Response.<Boolean>builder().data(util.hasAdminRole()).status(OK).build();
	}
	
	@RequestMapping("/roleadmin/{domain}")
	public Response<Boolean> roleadmin(Authentication authentication, @PathVariable("domain") String domain) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		return Response.<Boolean>builder().data(util.hasAdminRole(domain)).status(OK).build();
	}
	
	@RequestMapping("/role/{role}")
	public Response<Boolean> role(Authentication authentication, @PathVariable("role") String role) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		return Response.<Boolean>builder().data(util.hasRole(role)).status(OK).build();
	}
	
	@RequestMapping("/role/{domain}/{role}")
	public Response<Boolean> role(Authentication authentication, @PathVariable("domain") String domain, @PathVariable("role") String role) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		return Response.<Boolean>builder().data(util.hasRole(domain, role)).status(OK).build();
	}
	
	@RequestMapping("/domainsWithRole/{role}")
	public Response<List<JWTDomain>> domainsWithRole(Authentication authentication, @PathVariable("role") String role) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		return Response.<List<JWTDomain>>builder().data(util.domainsWithRole(role)).status(OK).build();
	}
	
	@RequestMapping("/roles")
	public Response<List<String>> roles(Authentication authentication) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		return Response.<List<String>>builder().data(util.roles()).status(OK).build();
	}
	
	@RequestMapping("/roles/{domain}")
	public Response<List<String>> roles(Authentication authentication, @PathVariable("domain") String domain) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		return Response.<List<String>>builder().data(util.roles(domain)).status(OK).build();
	}
}