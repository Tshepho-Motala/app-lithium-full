package lithium.service.casino.provider.slotapi.services.oauthClient;

import lithium.exceptions.Status500InternalServerErrorException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "server-oauth2")
public interface OauthApiInternalClient {

  @RequestMapping(value="/token/validateAuth", method=RequestMethod.POST)
  void validateClientAuth(@RequestParam String authorization)
      throws Status500InternalServerErrorException;
}
