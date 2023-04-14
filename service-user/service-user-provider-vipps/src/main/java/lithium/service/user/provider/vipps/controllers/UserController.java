package lithium.service.user.provider.vipps.controllers;

import java.security.Principal;
import java.util.Map;

import javax.validation.Valid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.client.objects.Address;
import lithium.service.user.client.objects.Domain;
import lithium.service.user.client.objects.User;
import lithium.service.user.provider.vipps.config.Config;
import lithium.service.user.provider.vipps.domain.AuthAttempt;
import lithium.service.user.provider.vipps.domain.CallbackRequest;
import lithium.service.user.provider.vipps.domain.UserDetails;
import lithium.service.user.provider.vipps.service.AuthAttemptService;
import lithium.service.user.provider.vipps.service.UserService;
import lithium.service.user.provider.vipps.service.VippsService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
	@Autowired
	private AuthAttemptService authAttemptService;
	@Autowired
	private UserService userService;
	@Autowired
	private VippsService vippsService;
	
	@RequestMapping(path = "/auth")
	public Response<User> auth(
		@RequestParam("domain") String domainName,
		@RequestParam String username,
		@RequestParam String password,
		@RequestParam String ipAddress,
		@RequestParam String userAgent,
		@RequestParam(required=false) Map<String, String> parameters
	) {
		log.debug("@RequestMapping(path = \"/auth\")");
		log.info("auth domain : '"+domainName+"' username : '"+username+"' password : '"+password+"'");
		log.info("parameters  : "+parameters);
		try {
			Response<User> response = Response.<User>builder().build();
//			String providerName = parameters.get("provider-name");
			String xRequestId = parameters.getOrDefault("external-token", null);
			log.debug("xRequestId : "+xRequestId);
			if (xRequestId==null) {
				response.setStatus(Status.UNAUTHORIZED);
				response.setMessage("[Vipps] Unauthorized Login Attempt.");
				log.info("Response :"+response);
				return response;
			}
			
			AuthAttempt authAttempt =  authAttemptService.findAuthAttemptByXRequestId(xRequestId);
			log.info("AuthAttempt : "+authAttempt);
			if (authAttempt==null) {
				response.setStatus(Status.UNAUTHORIZED);
				response.setMessage("[Vipps] Unauthorized Login Attempt.");
				log.info("Response :"+response);
				return response;
			}
			
			CallbackRequest callbackRequest =  authAttempt.getCallbackRequest();
			log.info("CallbackRequest : "+callbackRequest);
			if (callbackRequest==null) {
				response.setStatus(Status.UNAUTHORIZED);
				response.setMessage("[Vipps] Unauthorized Login Attempt.");
				log.info("Response :"+response);
				return response;
			}
			UserDetails userDetails = callbackRequest.getUserDetails();
			log.info("UserDetails : "+userDetails);
			
			
			
//			String clientId = parameters.getOrDefault(providerName+"-clientId", "");
//			String clientSecret = parameters.getOrDefault(providerName+"-clientSecret", "");
//			String accessTokenUrl = parameters.getOrDefault(providerName+"-accessTokenUrl", "");
//			String ocpApimSubscriptionKey = parameters.getOrDefault(providerName+"-ocpApimSubscriptionKey", "");
//			vippsService.auth(clientId, clientSecret, accessTokenUrl, ocpApimSubscriptionKey);
			
//			Authenticate authenticate = youWagerService.authenticate(parameters, domain);
//			log.debug("AUTH :: "+authenticate);
			
			if (userDetails == null) {
				/**
				 * Might not have received the callback form vipps, lets try and request user details first before we block it.
				 */
				String providerName = parameters.getOrDefault("provider-name", "");
				String url = parameters.getOrDefault(providerName+"-"+Config.BASE_URL.property(), "")+"/signup/v1/loginRequests/";
				CallbackRequest callbackRequest2 = vippsService.loginRequestDetails(domainName, url, callbackRequest.getRequestId());
				if (callbackRequest2==null) {
					response.setStatus(Status.UNAUTHORIZED);
					response.setMessage("[Vipps] Unauthorized Login Attempt.");
					log.info("Response :"+response);
					return response;
				}
				callbackRequest.setStatus(callbackRequest2.getStatus());
				callbackRequest.setUserDetails(callbackRequest2.getUserDetails());
				authAttempt.setCallbackRequest(callbackRequest);
				userDetails = callbackRequest.getUserDetails();
				authAttemptService.saveAuthAttempt(authAttempt);
				if (!callbackRequest.success()) {
					response.setStatus(Status.UNAUTHORIZED);
					response.setMessage("[Vipps] Unauthorized Login Attempt.");
					log.info("Response :"+response);
					return response;
				}
			}
			
			Integer dobYear = null;
			Integer dobMonth = null;
			Integer dobDay = null;
			if (userDetails.getDateOfBirth() != null) {
				DateTime dob = DateTime.parse(userDetails.getDateOfBirth(), DateTimeFormat.forPattern("yyyy-MM-dd"));
				dobYear = dob.getYear();
				dobMonth = dob.getMonthOfYear();
				dobDay = dob.getDayOfMonth();
			}
			
			response.setData(
				User.builder()
				.firstName(userDetails.getFirstName())
				.lastName(userDetails.getLastName())
				.email(userDetails.getEmail())
				.emailValidated(true)
				.cellphoneNumber(userDetails.getMobileNumber())
				.cellphoneValidated(true)
				.socialSecurityNumber(userDetails.getSsn())
				.dobDay(dobDay)
				.dobMonth(dobMonth)
				.dobYear(dobYear)
				.domain(buildUserDomain(domainName))
				.username(userDetails.getMobileNumber())
				.postalAddress((userDetails.getAddress()!=null)?
					Address.builder()
					.addressLine1(userDetails.getAddress().getAddressLine1())
					.addressLine2(userDetails.getAddress().getAddressLine2())
					.postalCode(userDetails.getAddress().getZipCode())
					.city(userDetails.getAddress().getCity())
					.country(userDetails.getAddress().getCountry())
					.build():null
				)
				.externalUsername(userDetails.getUserId())
				.passwordPlaintext(authAttemptService.passwordGen(xRequestId))
				.build()
			);
			if (!callbackRequest.success()) {
				response.setStatus(Status.UNAUTHORIZED);
				response.setMessage("[Vipps] "+callbackRequest.getErrorInfo().getErrorMessage());
			}
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
		
		lithium.service.user.provider.vipps.domain.User localUser = userService.find(domainName, username);
		UserDetails userDetails = null;
		AuthAttempt authAttempt = null;
		if (localUser == null) {
			String xRequestId = parameters.getOrDefault("external-token", "");
			
			if (xRequestId==null) {
				response.setStatus(Status.UNAUTHORIZED);
				response.setMessage("[Vipps] Unauthorized Login Attempt.");
				log.info("Response :"+response);
				return response;
			}
			
			authAttempt =  authAttemptService.findAuthAttemptByXRequestId(xRequestId);
			log.debug("AuthAttempt : "+authAttempt);
			
			CallbackRequest callbackRequest = null;
			
			if (authAttempt!=null) callbackRequest = authAttempt.getCallbackRequest();
			if (callbackRequest!=null) userDetails = callbackRequest.getUserDetails();
			log.debug("UserDetails : "+userDetails);
		} else {
			userDetails = localUser.getCurrentUserDetails();
			authAttempt = localUser.getCurrentAuthAttempt();
		}
		
		if ((userDetails==null) || (authAttempt==null)) {
			response.setStatus(Status.NOT_FOUND);
			response.setMessage("[Vipps] User Not Found..");
			log.warn("Response :"+response);
			return response;
		}
		
		Integer dobYear = null;
		Integer dobMonth = null;
		Integer dobDay = null;
		if (userDetails.getDateOfBirth() != null) {
			DateTime dob = DateTime.parse(userDetails.getDateOfBirth(), DateTimeFormat.forPattern("yyyy-MM-dd"));
			dobYear = dob.getYear();
			dobMonth = dob.getMonthOfYear();
			dobDay = dob.getDayOfMonth();
		}
		
		response.setData(
			User.builder()
			.firstName(userDetails.getFirstName())
			.lastName(userDetails.getLastName())
			.email(userDetails.getEmail())
			.dobDay(dobDay)
			.dobMonth(dobMonth)
			.dobYear(dobYear)
			.emailValidated(true)
			.cellphoneNumber(userDetails.getMobileNumber())
			.cellphoneValidated(true)
			.socialSecurityNumber(userDetails.getSsn())
			.domain(buildUserDomain(domainName))
			.username(userDetails.getMobileNumber())
			.externalUsername(userDetails.getUserId())
			.passwordPlaintext(authAttemptService.passwordGen(authAttempt.getXRequestId()))
			.build()
		);
		
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
	
	@DeleteMapping(path="/d/{domainName}")
	public Response<String> delete(
		@PathVariable("domainName") String domainName,
		@RequestParam(required=false) Map<String, String> parameters,
		Principal principal
	) {
		try {
			log.info("deleteUser domain : '"+domainName+"' principal " + principal);
			log.info("parameters  : "+parameters);
			
			String xRequestId = parameters.getOrDefault("xRequestId", "");
			
			AuthAttempt authAttempt =  authAttemptService.findAuthAttemptByXRequestId(xRequestId);
			log.info("AuthAttempt : "+authAttempt);
			if ((authAttempt == null) || (!authAttempt.pendingRemoval())) return Response.<String>builder().status(Status.NOT_FOUND).build();
			
			CallbackRequest callbackRequest = authAttempt.getCallbackRequest();
			UserDetails userDetails = callbackRequest.getUserDetails();
			log.info("UserDetails : "+userDetails);
			
			callbackRequest.setStatus(lithium.service.user.provider.vipps.domain.CallbackRequest.Status.REMOVED);
			callbackRequest.setUserDetails(authAttemptService.obfuscateUserDetails(userDetails));
			authAttempt.setCallbackRequest(callbackRequest);
			authAttempt = authAttemptService.saveAuthAttempt(authAttempt);
			return Response.<String>builder().data(userDetails.getUserId()).status(Status.OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<String>builder().status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	private Domain buildUserDomain(String domainName) {
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