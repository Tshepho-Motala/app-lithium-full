package lithium.server.security.controllers;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.server.security.exceptions.CustomOAuthException;
import lithium.server.security.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class SecurityController {

    @GetMapping("/messages")
    public String[] getMessages() {
        String[] messages = new String[] {"Message 1", "Message 2", "Message 3"};
        return messages;
    }

    @RequestMapping("/user")
    @ResponseBody
    public Principal user(Principal user) {
        return user;
    }

    @Autowired TokenService tokenService;

    @ExceptionHandler(CustomOAuthException.class)
    public ResponseEntity<OAuth2Exception> handleCustomOAuthException(
            CustomOAuthException e,
            HttpServletResponse response
    ) {
        return ResponseEntity.status(e.getHttpErrorCode()).body(e);
    }

    @TimeThisMethod
    @RequestMapping(value="/oauth/token", method= RequestMethod.POST)
    public ResponseEntity<OAuth2AccessToken> postAccessToken(
            Principal principal,
            @RequestParam Map<String, String> parameters
    ) {
        try {
            SW.start("response");
            ResponseEntity<OAuth2AccessToken> response = tokenService.tokenResponseEntity(principal, parameters);
            SW.stop();

            SW.start("token");
            log.debug("Principal:: " + principal.getName());
            if (principal.getName().contentEquals("system")) {
                // Just return original response
                return response;
            }
            OAuth2AccessToken token = response.getBody();
            SW.stop();

            SW.start("newToken");
            if (parameters.get("grant_type").contentEquals("refresh_token")) {
                // If we're trying to refresh the token, then we need to validate the session and update last activity
                // if validation is successful.
                tokenService.validateAndUpdateSession(token.getValue());
            }
            DefaultOAuth2AccessToken newToken = tokenService.enhancedAccessToken(token);
            SW.stop();

            return new ResponseEntity<>(newToken, response.getHeaders(), response.getStatusCode());
        } catch (Exception e) {
            SW.stop();
            if (e instanceof ErrorCodeException || e.getCause() instanceof ErrorCodeException) {
                ErrorCodeException ec = (ErrorCodeException) e;
                CustomOAuthException customException =
                        new CustomOAuthException(ec.getErrorCode(), new ErrorCodeException(ec.getCode(), ec.getMessage(), ec, ""));

                // Check for additional information in the exception type and add it to the oauth exception
                if (ec instanceof IAdditionalInformation || ec.getCause() instanceof IAdditionalInformation) {
                    IAdditionalInformation ai = (IAdditionalInformation) ec;
                    ai.getAdditionalInformation().entrySet().stream().forEach(entry -> {
                        customException.addAdditionalInformation(entry.getKey(), entry.getValue()); });
                }

                throw customException;
            }
            log.error("Failed to enhance the OAuth2AccessToken response object | " + e.getMessage(), e);
            throw new CustomOAuthException("500", new ErrorCodeException(500, e.getMessage(), e, ""));
        }
    }

    @Autowired TokenStore tokenStore;
    @Autowired TokenEndpoint tokenEndpoint;
    @Autowired TokenService tokenService;

    @TimeThisMethod
    @RequestMapping(value = "/token/refresh", method=RequestMethod.POST)
    public ResponseEntity<OAuth2AccessToken> postAccessToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("refresh_token") String refreshToken
    ) throws CustomOAuthException {
        OAuth2RefreshToken oAuth2RefreshToken = null;
        try {
            SW.start("oAuth2RefreshToken");
            oAuth2RefreshToken = tokenStore.readRefreshToken(refreshToken);
            tokenService.validateAndUpdateSession(refreshToken);
            SW.stop();
        } catch (Exception e) {
            SW.stop();
            throw new CustomOAuthException(CustomOAuthException.ACCESS_DENIED, new Status401UnAuthorisedException(e.getMessage()));
        }

        SW.start("oAuth2Authentication");
        OAuth2Authentication oAuth2Authentication = tokenStore.readAuthenticationForRefreshToken(oAuth2RefreshToken);
        SW.stop();

        SW.start("parameters");
        Map<String, String> parameters = Stream.of(new String[][] {
                { "grant_type", grantType },
                { "refresh_token", refreshToken },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        SW.stop();

        try {
            ResponseEntity<OAuth2AccessToken> response = tokenEndpoint.postAccessToken(oAuth2Authentication, parameters);
            DefaultOAuth2AccessToken newToken = tokenService.enhancedAccessToken(response.getBody());
            return new ResponseEntity<>(newToken, response.getHeaders(), response.getStatusCode());
        } catch (Exception e) {
            SW.stop();
            throw new CustomOAuthException(CustomOAuthException.ACCESS_DENIED, new Status401UnAuthorisedException(e.getMessage()));
        }
    }

    @Autowired TokenService tokenService;
    @Autowired ProviderAuthClientDetailsService providerAuthClientDetailsService;

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