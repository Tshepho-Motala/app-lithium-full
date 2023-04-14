package lithium.service.user.client.system;

import java.util.List;
import lithium.exceptions.Status414UserNotFoundException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.exceptions.Status438PlayTimeLimitConfigurationNotFoundException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.service.user.client.objects.PlayTimeLimitPubSubDTO;
import lithium.service.user.client.objects.PlayerPlaytimeLimitV2ConfigDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-user")
public interface SystemPlayTimeLimitsV2Client {
  @RequestMapping(method = RequestMethod.POST, path ="/system/playtime-limit/v2/is-allowed-to-play")
  boolean isAllowedToPlay(@RequestParam("playerGuid") String playerGuid)
      throws Status414UserNotFoundException, Status438PlayTimeLimitConfigurationNotFoundException, Status438PlayTimeLimitReachedException, Status426InvalidParameterProvidedException, Status550ServiceDomainClientException;

  @RequestMapping(method = RequestMethod.POST, path ="/system/playtime-limit/v2/configuration/get")
  PlayerPlaytimeLimitV2ConfigDto getPlayerConfiguration(@RequestParam("playerGuid") String playerGuid) throws Status414UserNotFoundException, Status438PlayTimeLimitConfigurationNotFoundException;

  @RequestMapping(method = RequestMethod.POST, path ="/system/playtime-limit/v2/active-entry/get")
  PlayTimeLimitPubSubDTO updateAndGetPlayerEntry(@RequestParam("playerGuid") String playerGuid) throws Status414UserNotFoundException, Status550ServiceDomainClientException, Status426InvalidParameterProvidedException;

  @RequestMapping(method = RequestMethod.POST, path = "/system/playtime-limit/v2/get-v1-limits")
  List<PlayTimeLimitPubSubDTO> getPlayTimeTimeForUserLimits(@RequestParam("playerGuid") String playerGuid) throws Status414UserNotFoundException, Status550ServiceDomainClientException, Status426InvalidParameterProvidedException;
}
