package lithium.service.user.provider.ldap.controllers;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.security.Principal;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.validation.Valid;

import org.springframework.ldap.authentication.DefaultValuesAuthenticationSourceDecorator;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.user.client.objects.Domain;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
	
	private LdapContextSource contextSource(String url, String base, String userDn, String password) {
		DefaultValuesAuthenticationSourceDecorator authenticationSourceDecorator = new DefaultValuesAuthenticationSourceDecorator();
		authenticationSourceDecorator.setDefaultPassword(password);
		authenticationSourceDecorator.setDefaultUser(userDn);
		authenticationSourceDecorator.setTarget(new AuthenticationSource() {
			@Override
			public String getPrincipal() {
				return userDn;
			}
			@Override
			public String getCredentials() {
				return password;
			}
		});
		LdapContextSource ldapContextSource = new LdapContextSource();
		ldapContextSource.setUrl(url);
		ldapContextSource.setBase(base);
		ldapContextSource.setCacheEnvironmentProperties(false);
//		ldapContextSource.setUserDn(userDn);
//		ldapContextSource.setPassword(password);
		ldapContextSource.setAuthenticationSource(authenticationSourceDecorator);
		return ldapContextSource;
	}
	
	private LdapTemplate ldapTemplate(String domain, Map<String, String> parameters) throws Exception {
		String parameterName = parameters.get("provider-name");
		String url = parameters.get(parameterName+"-url");
		String base = parameters.get(parameterName+"-base");
		String userDn = parameters.get(parameterName+"-userDn");
		String ldapPassword = parameters.get(parameterName+"-password");
		
		LdapTemplate ldapTemplate = new LdapTemplate(contextSource(url, base, userDn, ldapPassword));
		
		return ldapTemplate;
	}
	
	private User search(String domain, String username, LdapTemplate ldapTemplate) {
		return ldapTemplate.search(query().where("sAMAccountName").is(username), new AttributesMapper<User>() {
			public User mapFromAttributes(Attributes attrs) throws NamingException {
				User user = User
					.builder()
					.domain(Domain.builder().name(domain).build())
					.firstName(attrs.get("givenName").get().toString())
					.lastName(attrs.get("sn").get().toString())
					.email(attrs.get("mail").get().toString())
					.username(username)
					.build();
				return user;
			}
		}).get(0);
	}
	
	@RequestMapping(path = "/auth")
	public Response<User> auth(@RequestParam String domain, @RequestParam String username, @RequestParam String password, @RequestParam(required=false) Map<String, String> parameters) {
		log.info("auth domain "+domain+" username "+username+" password "+password);
		User user = null;
		try {
			LdapTemplate ldapTemplate = ldapTemplate(domain, parameters);
			boolean authenticated = ldapTemplate.authenticate(LdapUtils.emptyLdapName(), "sAMAccountName="+username, password);
			log.info("authenticated : "+authenticated);
			if (authenticated) {
//					Object o = ldapTemplate.findOne(query().where("sAMAccountName").is(username), Object.class);
				user = search(domain, username, ldapTemplate);
				user.setPasswordPlaintext(password);
				return Response.<User>builder().status(Response.Status.OK).data(user).build();
			} else {
				return Response.<User>builder().status(Response.Status.UNAUTHORIZED).build();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<User>builder().status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@RequestMapping(path = "/user")
	public Response<User> user(@RequestParam String domain, @RequestParam String username, @RequestParam(required=false) Map<String, String> parameters, Principal principal) {
		log.info("user domain " + domain + " username " + username + " principal " + principal);
		User user = null;
		try {
			LdapTemplate ldapTemplate = ldapTemplate(domain, parameters);
			user = search(domain, username, ldapTemplate);
			return Response.<User>builder().status(Response.Status.OK).data(user).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<User>builder().status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@RequestMapping(path = "/create", method = RequestMethod.POST)
	public Response<User> create(@RequestBody @Valid User user) {
//		users.save(user);
		return Response.<User>builder().status(Response.Status.OK).data(user).build();
	}
	
	@RequestMapping(path = "/update", method = RequestMethod.POST)
	public Response<User> update(@RequestBody @Valid User user) {
//		users.save(user);
		return Response.<User>builder().status(Response.Status.OK).data(user).build();
	}
}
