package lithium.server.oauth2.controllers;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.server.oauth2.CustomOAuthException;
import lithium.server.oauth2.services.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
public class RefreshTokenController  {
    @Autowired TokenStore tokenStore;
    @Autowired TokenEndpoint tokenEndpoint;
    @Autowired TokenService tokenService;

    @ExceptionHandler(CustomOAuthException.class)
    public ResponseEntity<OAuth2Exception> handleCustomOAuthException(CustomOAuthException e, HttpServletResponse response) {
        return ResponseEntity
            .status(e.getHttpErrorCode())
            .body(e);
    }

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
}
