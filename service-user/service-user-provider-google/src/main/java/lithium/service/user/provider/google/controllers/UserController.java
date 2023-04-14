package lithium.service.user.provider.google.controllers;

import java.security.Principal;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
//	@Autowired
//	private LithiumServiceClientFactory services;
//	@Autowired
//	private CustomOAuth2RestTemplate customOAuth2RestTemplate;
	
//	private Provider getProvider(String domain, ProviderType kind) throws Exception {
//		DomainClient domainClient = services.target(DomainClient.class, true);
//		Domain domainConfig = domainClient.findByName(domain);
//		if (domainConfig == null) throw new Exception("Domain " + domain + " does not exist");
//		Provider provider = null;
//		switch (kind) {
//			case AUTH:
//				provider = domainConfig.getAuthProvider();
//				break;
//			case USER:
//				provider = domainConfig.getUserProvider();
//				break;
//		}
//		if (provider == null) throw new Exception("Invalid " + kind + " provider for the domain");
//		return provider;
//	}
	
	@RequestMapping(path = "/auth")
	public ResponseEntity<User> auth(@RequestParam String domain, @RequestParam String username, @RequestParam String password, @RequestParam(required=false) Map<String, String> parameters) {
		log.info("auth domain "+domain+" username "+username+" password "+password+" parameters "+parameters);
//		User user = null;
		try {
			return new ResponseEntity<User>(HttpStatus.NOT_IMPLEMENTED);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/user")
	public ResponseEntity<User> user(@RequestParam String domain, @RequestParam String username, @RequestParam(required=false) Map<String, String> parameters, Principal principal) {
		log.info("user domain " + domain + " username " + username + " principal " + principal);
		User user = null;
//		if (user == null) return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		return new ResponseEntity<User>(user, HttpStatus.NOT_IMPLEMENTED);
	}
	
	@RequestMapping(path = "/create", method = RequestMethod.POST)
	public ResponseEntity<User> create(@RequestBody @Valid User user) {
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	@RequestMapping(path = "/update", method = RequestMethod.POST)
	public ResponseEntity<User> update(@RequestBody @Valid User user) {
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
}