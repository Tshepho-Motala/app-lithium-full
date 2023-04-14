package lithium.service.user.services.oauthClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status403AccessDeniedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status407IpBlockedException;
import lithium.exceptions.Status447AccountFrozenException;
import lithium.exceptions.Status450AccountFrozenSelfExcludedException;
import lithium.exceptions.Status460LoginRestrictedException;
import lithium.exceptions.Status465DomainUnknownCountryException;
import lithium.exceptions.Status492ExcessiveFailedLoginBlockException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OauthApiInternalClientService {
    @Autowired
    LithiumServiceClientFactory factory;

  private final static String GRANT_TYPE = "password";

    public OAuth2AccessToken getToken(
        String xForwardedFor,
        String userAgent,
        String authorization,
        String userGuid,
        String userPsw
    ) throws
        Status403AccessDeniedException,
        Status407IpBlockedException,
        Status500LimitInternalSystemClientException,
        Status405UserDisabledException,
        Status450AccountFrozenSelfExcludedException,
        Status491PermanentSelfExclusionException,
        Status496PlayerCoolingOffException,
        Status490SoftSelfExclusionException,
        Status460LoginRestrictedException,
        Status492ExcessiveFailedLoginBlockException,
        Status401UnAuthorisedException,
        Status447AccountFrozenException,
        Status500InternalServerErrorException,
        Status465DomainUnknownCountryException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", GRANT_TYPE);
        parameters.put("username", userGuid);
        parameters.put("password", userPsw);

        return getClient().accessToken(xForwardedFor, userAgent, authorization, parameters, LocaleContextHolder.getLocale().toLanguageTag());
    }

    private OauthApiInternalClient getClient() throws Status500InternalServerErrorException {
        try {
            return factory.target(OauthApiInternalClient.class, "server-oauth2",false);
        } catch (LithiumServiceClientFactoryException e) {
            throw new Status500InternalServerErrorException(e.getMessage());
        }
    }

    public String getClientId(String authorization) {
      String base64Credentials = authorization.substring("Basic".length()).trim();
      byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
      String credentials = new String(credDecoded, StandardCharsets.UTF_8);
      // credentials = username:password
      final String[] values = credentials.split(":", 2);
      return values[0];
    }

    public void validateClientAuth(String authorization) throws Status500InternalServerErrorException {
      getClient().validateClientAuth(authorization);
    }
}
