package lithium.service.user.provider.vipps.controllers;

import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import lithium.service.user.provider.vipps.domain.AuthAttempt;
import lithium.service.user.provider.vipps.domain.CallbackRequest;
import lithium.service.user.provider.vipps.domain.ErrorInfo;
import lithium.service.user.provider.vipps.domain.SignupOrLoginResponse;
import lithium.service.user.provider.vipps.domain.User;
import lithium.service.user.provider.vipps.service.AuthAttemptService;
import lithium.service.user.provider.vipps.service.UserService;
import lithium.service.user.provider.vipps.service.VippsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class VippsController {
	@Autowired
	private VippsService vippsService;
	@Autowired
	private AuthAttemptService authAttemptService;
	@Autowired
	private UserService userService;
	
	@GetMapping(path = "/login")
	public RedirectView login(
		@RequestParam("domain") String domainName,
		@RequestParam(name="returnAddr", required=false) String returnAddr,
		WebRequest request,
		RedirectAttributes attributes
	) throws Exception {
		SignupOrLoginResponse response = vippsService.auth(domainName, returnAddr);
		
		return new RedirectView(response.getUrl());
	}
	
	@PostMapping(path = "/{domainName}/v1/userDetails")
	public void callback(
		@PathVariable("domainName") String domainName,
		WebRequest request,
		@RequestBody CallbackRequest callbackRequest
	) {
		log.info("Callback Request : "+callbackRequest);
		try {
			AuthAttempt authAttempt = authAttemptService.findAuthAttemptByRequestId(callbackRequest.getRequestId());
			log.debug("AuthAttempt :"+authAttempt);
			callbackRequest.setId(authAttempt.getCallbackRequest().getId());
			callbackRequest.getUserDetails().setId((authAttempt.getCallbackRequest().getUserDetails()!=null)?authAttempt.getCallbackRequest().getUserDetails().getId():null);
			authAttempt.setCallbackRequest(callbackRequest);
			authAttempt = authAttemptService.saveAuthAttempt(authAttempt);
			log.debug("Saved AuthAttempt : "+authAttempt);
			User user = userService.findOrCreate(domainName, callbackRequest.getUserDetails().getUserId());
			user.setCurrentAuthAttempt(authAttempt);
			if (authAttempt.success()) user.setCurrentUserDetails(authAttempt.getCallbackRequest().getUserDetails());
			log.debug("Saving User : "+user);
			user = userService.save(user);
			log.debug("Saved User : "+user);
			if (authAttempt.success()) vippsService.svcUserSaveUser(domainName, callbackRequest);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	@GetMapping(path="/{domainName}/{xRequestId}/fallback")
	public RedirectView fallback(
		@PathVariable("domainName") String domainName,
		@PathVariable("xRequestId") String xRequestId,
		@RequestParam(name="rad", required=false) String returnAddr,
		HttpServletResponse response,
		HttpServletRequest request,
		RedirectAttributes attributes
	) {
		log.info("/"+domainName+"/"+xRequestId+"/fallback?rad="+returnAddr);
		try {
			returnAddr = URLDecoder.decode(returnAddr, "UTF-8");
			String suffix = "";
			Map<String, Object> oauth = vippsService.fallback(domainName, xRequestId, response);
			
			ErrorInfo errorInfo = (ErrorInfo)oauth.get("ErrorInfo");
			if (errorInfo==null) {
				String token = (String)oauth.getOrDefault("access_token", "");
				String tokenType = (String)oauth.getOrDefault("token_type", "");
				log.info("token :: "+tokenType+" "+token);
				Integer expiresIn = (Integer)oauth.getOrDefault("expires_in", 0);
				log.info("Expires in : "+expiresIn);
				
				Cookie cookie = new Cookie("lithium-oauth-token", token);
				cookie.setMaxAge(expiresIn);
				cookie.setDomain(request.getLocalName());
				cookie.setPath("/");
				attributes.addAttribute("token", token);
				response.addHeader("Authorization", tokenType+" "+token);
				response.addCookie(cookie);
				
				log.info("Cookie : "+cookie);
			} else {
				suffix = "?error="+errorInfo.getErrorMessage();
			}
			
			String redirect = "";
			if (returnAddr == null)
				redirect = "http://"+request.getLocalName()+":9800"+suffix;
			else
				redirect = returnAddr+""+suffix;
			log.info("Redirecting to : "+redirect);
			return new RedirectView(redirect);
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}
	
	@DeleteMapping(path="/{domainName}/{apiToken}/v2/consents/{userId}")
	public void remove(
		@PathVariable("domainName") String domainName,
		@PathVariable("apiToken") String apiToken,
		@PathVariable("userId") String userId,
		HttpServletRequest request
	) throws Exception {
		log.info("/"+domainName+"/"+apiToken+"/v2/consents/"+userId);
		if (!vippsService.accessTokenValid(apiToken)) {
			log.error("Access Token Invalid. ("+apiToken+")");
			return;
		}
		if (!vippsService.validIp(request.getRemoteAddr())) {
			log.error("Invalid IP Address. ("+request.getRemoteAddr()+")");
			return;
		}
		User user = userService.find(domainName, userId);
		String mobile = user.getCurrentUserDetails().getMobileNumber();
		userService.delete(domainName, userId);
		vippsService.svcUserDeleteUser(domainName, mobile);
	}
}
