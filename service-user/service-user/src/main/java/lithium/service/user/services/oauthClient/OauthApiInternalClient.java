package lithium.service.user.services.oauthClient;

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
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "server-oauth2")
public interface OauthApiInternalClient {

    @RequestMapping(value="/token/ece", method=RequestMethod.POST)
    OAuth2AccessToken accessToken(
        @RequestHeader("X-Forwarded-For") String xForwardedFor,
        @RequestHeader("User-Agent") String userAgent,
        @RequestHeader("Authorization") String requestHeader,
        @SpringQueryMap Map<String, String> parameters,
        @RequestParam(name = "locale", defaultValue = "en", required = false) String locale
    ) throws
        Status401UnAuthorisedException,
        Status403AccessDeniedException,
        Status405UserDisabledException,
        Status407IpBlockedException,
        Status447AccountFrozenException,
        Status450AccountFrozenSelfExcludedException,
        Status460LoginRestrictedException,
        Status465DomainUnknownCountryException,
        Status490SoftSelfExclusionException,
        Status491PermanentSelfExclusionException,
        Status492ExcessiveFailedLoginBlockException,
        Status496PlayerCoolingOffException,
        Status500LimitInternalSystemClientException,
        Status500InternalServerErrorException;

  @RequestMapping(value="/token/validateAuth", method=RequestMethod.POST)
  void validateClientAuth(@RequestParam String authorization)
      throws Status500InternalServerErrorException;
}
