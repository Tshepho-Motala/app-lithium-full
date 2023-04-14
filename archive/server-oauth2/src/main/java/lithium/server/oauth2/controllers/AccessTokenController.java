package lithium.server.oauth2.controllers;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.IAdditionalInformation;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.server.oauth2.CustomOAuthException;
import lithium.server.oauth2.services.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Map;

@RestController
@Slf4j
public class AccessTokenController {
	@Autowired TokenService tokenService;

	@ExceptionHandler(CustomOAuthException.class)
	public ResponseEntity<OAuth2Exception> handleCustomOAuthException(
		CustomOAuthException e,
		HttpServletResponse response
	) {
		return ResponseEntity.status(e.getHttpErrorCode()).body(e);
	}

	@TimeThisMethod
	@RequestMapping(value="/oauth/token", method=RequestMethod.POST)
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
}
