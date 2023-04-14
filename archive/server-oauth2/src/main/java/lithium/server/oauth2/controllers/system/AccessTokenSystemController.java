package lithium.server.oauth2.controllers.system;

import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status403AccessDeniedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status407IpBlockedException;
import lithium.exceptions.Status460LoginRestrictedException;
import lithium.exceptions.Status492ExcessiveFailedLoginBlockException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.server.oauth2.ProviderAuthClientDetailsService;
import lithium.server.oauth2.services.TokenService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;
import java.util.Base64;
import java.util.Map;

@Slf4j
@RestController
@EnableCustomHttpErrorCodeExceptions
public class AccessTokenSystemController {
	@Autowired TokenService tokenService;

    @Autowired
    ProviderAuthClientDetailsService providerAuthClientDetailsService;

	@RequestMapping(value="/token/ece", method=RequestMethod.POST)
	public OAuth2AccessToken accessToken(
		@RequestHeader("X-Forwarded-For") String xForwardedFor,
		@RequestHeader("User-Agent") String userAgent,
		@RequestHeader("Authorization") String authorization,
		@RequestParam Map<String, String> parameters
	) throws
		Status401UnAuthorisedException,
		Status403AccessDeniedException,
		Status405UserDisabledException,
		Status407IpBlockedException,
		Status460LoginRestrictedException,
		Status490SoftSelfExclusionException,
		Status491PermanentSelfExclusionException,
		Status492ExcessiveFailedLoginBlockException,
		Status496PlayerCoolingOffException,
		Status500LimitInternalSystemClientException,
		Status500InternalServerErrorException
	{
		try {
			OAuth2AccessToken accessToken = tokenService.accessToken(tokenService.convertToAuth(authorization), parameters);
			//TODO: Riaan: Figure out why principal goes missing in this controller.
//			OAuth2AccessToken accessToken = tokenService.accessToken(principal, parameters);
			DefaultOAuth2AccessToken enhancedAccessToken = tokenService.enhancedAccessToken(accessToken);
			return enhancedAccessToken;
		} catch (ErrorCodeException e) {
			throw e;
		} catch (Exception e) {
			log.error("Failed to enhance the OAuth2AccessToken response object | " + e.getMessage(), e);
			throw new Status500InternalServerErrorException("server-oauth2", e.getMessage(), e);
		}
	}

    @RequestMapping(value="/token/validateAuth", method=RequestMethod.POST)
	public void validateClientAuth(@RequestParam Map<String, String> parameters) throws Status500InternalServerErrorException {
        try {
            Authentication authentication = tokenService.convertToAuth(parameters.get("authorization"));
            String requestClientId = authentication.getPrincipal().toString();
            String requestClientPsw = authentication.getCredentials().toString();
            ClientDetails clientDetails = providerAuthClientDetailsService.loadClientByClientId(requestClientId);
            if (!clientDetails.getClientSecret().equalsIgnoreCase(requestClientPsw)) {
                String message = "Unknown Provider Auth Client : "+requestClientId+":"+requestClientPsw;
                log.error(message);
                throw new Status500InternalServerErrorException(message);
            }
        } catch (ClientRegistrationException ex) {
            throw new Status500InternalServerErrorException("server-oauth2", ex.getMessage(), ex);
        }
    }
}
