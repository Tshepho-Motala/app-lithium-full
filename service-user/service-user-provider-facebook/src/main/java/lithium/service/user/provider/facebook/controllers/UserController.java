package lithium.service.user.provider.facebook.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.user.client.objects.Domain;
import lithium.service.user.client.objects.User;
import lithium.service.user.provider.facebook.user.FBConnection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
	@Getter
	@Value("${spring.application.name}")
	private String moduleName;
	
	@Autowired FBConnection fbConnection;
	@Autowired LithiumServiceClientFactory serviceFactory;
	
	public Provider providerAuth(String domainName) throws Exception {
		return provider(domainName, ProviderType.AUTH);
	}
	
	private Provider provider(String domainName, ProviderType providerType) throws Exception {
		ProviderClient providerClient = serviceFactory.target(ProviderClient.class, true);
		Response<Iterable<Provider>> response = providerClient.listByDomainAndType(domainName, providerType.type());
		List<Provider> providers = new ArrayList<>();
		
		if (response.isSuccessful()) {
			response.getData().forEach(providers::add);
			providers.removeIf(p -> (p.getEnabled() == false || !p.getUrl().equalsIgnoreCase(moduleName)));
			providers.sort(Comparator.comparingInt(Provider::getPriority));
			if (providers.size() != 1) throw new Exception("Provider Setup Issue.");
			return providers.get(0);
		}
		throw new Exception("No provider found.");
	}
	
	@RequestMapping(path = "/auth")
	public Response<User> auth(
		@RequestParam(name="code", required=true) String code,
		@RequestParam(name="state", required=true) String state,
		@RequestParam(required=false) Map<String, String> parameters
	) {
		log.debug("@RequestMapping(path = \"/auth\")");
		log.info("auth domain : '"+state+"' code : '"+code+"'");
		log.info("parameters  : "+parameters);
		try {
			Response<User> response = Response.<User>builder().build();
			
			Provider provider = providerAuth(state);
			String appId = provider.getPropertyValue("appId");
			String appSecret = provider.getPropertyValue("appSecret");
			
			String accessToken = fbConnection.getAccessToken(code, appId, appSecret);
			log.info("accessToken : "+accessToken);
			Facebook facebook = new FacebookTemplate(accessToken, "lithium", "453118391906446");
			String [] fields = { "id", "email",  "first_name", "last_name" };
			org.springframework.social.facebook.api.User profile = facebook.fetchObject("me", org.springframework.social.facebook.api.User.class, fields);
			
			log.info("fbProfileData :: "+profile);
			
			response.setData(
				User.builder()
				.firstName(profile.getFirstName())
				.lastName(profile.getLastName())
				.email(profile.getEmail())
//				.emailValidated(true)
//				.cellphoneNumber(userDetails.getMobileNumber())
//				.cellphoneValidated(true)
//				.socialSecurityNumber(userDetails.getSsn())
//				.dobDay(dobDay)
//				.dobMonth(dobMonth)
//				.dobYear(dobYear)
//				.domain(buildUserDomain(domainName))
//				.username(userDetails.getMobileNumber())
//				.postalAddress((userDetails.getAddress()!=null)?
//					Address.builder()
//					.addressLine1(userDetails.getAddress().getAddressLine1())
//					.addressLine2(userDetails.getAddress().getAddressLine2())
//					.postalCode(userDetails.getAddress().getZipCode())
//					.city(userDetails.getAddress().getCity())
//					.country(userDetails.getAddress().getCountry())
//					.build():null
//				)
				.externalUsername(""+profile.getId())
//				.passwordPlaintext(authAttemptService.passwordGen(xRequestId))
				.build()
			);
//			if (!callbackRequest.success()) {
//				response.setStatus(Status.UNAUTHORIZED);
//				response.setMessage("[Vipps] "+callbackRequest.getErrorInfo().getErrorMessage());
//			}
			log.info("Response :"+response);
			return response;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<User>builder().status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RequestMapping(path = "/user")
	public Response<User> user(
		@RequestParam("domain") String domainName,
		@RequestParam String username,
		@RequestParam(required=false) Map<String, String> parameters,
		Principal principal
	) {
		log.info("user domain : '"+domainName+"' username : '"+username+"' principal " + principal);
		log.info("parameters  : "+parameters);
		Response<User> response = Response.<User>builder().build();
		
//		response.setData(
//			User.builder()
//			.firstName(userDetails.getFirstName())
//			.lastName(userDetails.getLastName())
//			.email(userDetails.getEmail())
//			.dobDay(dobDay)
//			.dobMonth(dobMonth)
//			.dobYear(dobYear)
//			.emailValidated(true)
//			.cellphoneNumber(userDetails.getMobileNumber())
//			.cellphoneValidated(true)
//			.socialSecurityNumber(userDetails.getSsn())
//			.domain(buildUserDomain(domainName))
//			.username(userDetails.getMobileNumber())
//			.externalUsername(userDetails.getUserId())
//			.passwordPlaintext(authAttemptService.passwordGen(authAttempt.getXRequestId()))
//			.build()
//		);
		
		log.info("Response :"+response);
		return response;
	}
	
	@RequestMapping(path = "/create", method = RequestMethod.POST)
	public ResponseEntity<User> create(@RequestBody @Valid User user) {
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	@RequestMapping(path = "/update", method = RequestMethod.POST)
	public ResponseEntity<User> update(@RequestBody @Valid User user) {
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	protected Domain buildUserDomain(String domainName) {
		try {
			return Domain.builder()
			.name(domainName)
			.build();
		} catch (Exception e) {
			log.error("Could not retrieve domain details.", e);
		}
		return null;
	}
}