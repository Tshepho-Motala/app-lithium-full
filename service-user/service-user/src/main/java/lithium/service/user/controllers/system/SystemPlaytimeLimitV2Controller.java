package lithium.service.user.controllers.system;

import java.time.LocalDateTime;
import java.util.List;
import lithium.exceptions.Status414UserNotFoundException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.exceptions.Status438PlayTimeLimitConfigurationNotFoundException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.service.user.client.objects.PlayTimeLimitPubSubDTO;
import lithium.service.user.client.objects.PlayerPlaytimeLimitV2ConfigDto;
import lithium.service.user.client.system.SystemPlayTimeLimitsV2Client;
import lithium.service.user.data.entities.Period;
import lithium.service.user.data.entities.PlayerPlaytimeLimitV2Entry;
import lithium.service.user.data.entities.User;
import lithium.service.user.services.PlaytimeLimitsV2Service;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/system/playtime-limit/v2")
public class SystemPlaytimeLimitV2Controller implements SystemPlayTimeLimitsV2Client {

  private final PlaytimeLimitsV2Service playtimeLimitsV2Service;
  private final ModelMapper modelMapper;

  @PostMapping("/is-allowed-to-play")
  public boolean isAllowedToPlay(@RequestParam("playerGuid") String playerGuid)
      throws Status414UserNotFoundException, Status438PlayTimeLimitConfigurationNotFoundException, Status438PlayTimeLimitReachedException, Status426InvalidParameterProvidedException, Status550ServiceDomainClientException {
    return playtimeLimitsV2Service.isAllowedToPlay(playerGuid);
  }

  @PostMapping("/configuration/get")
  public PlayerPlaytimeLimitV2ConfigDto getPlayerConfiguration(@RequestParam("playerGuid") String playerGuid)
      throws Status414UserNotFoundException, Status438PlayTimeLimitConfigurationNotFoundException {
    return modelMapper.map(playtimeLimitsV2Service.getPlayerConfiguration(playerGuid), PlayerPlaytimeLimitV2ConfigDto.class);
  }

  @PostMapping("/active-entry/get")
  public PlayTimeLimitPubSubDTO updateAndGetPlayerEntry(@RequestParam("playerGuid") String playerGuid)
      throws Status414UserNotFoundException, Status550ServiceDomainClientException, Status426InvalidParameterProvidedException {
    return playtimeLimitsV2Service.updateAndGetPlayerEntryDTO(playerGuid);
  }

  @PostMapping("/get-v1-limits")
  public List<PlayTimeLimitPubSubDTO> getPlayTimeTimeForUserLimits(@RequestParam("playerGuid") String playerGuid)
      throws Status414UserNotFoundException, Status550ServiceDomainClientException, Status426InvalidParameterProvidedException {
    return playtimeLimitsV2Service.getPubSubLimits(playerGuid);
  }

  @PostMapping("/period/find-or-create")
  public Period find(
      @RequestParam String domainName,
      @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime date,
      @RequestParam long granularity) throws Status550ServiceDomainClientException, Status426InvalidParameterProvidedException {
    return playtimeLimitsV2Service.findOrCreatePeriod(domainName, date, granularity);
  }

  @PostMapping("/update-seconds-accumulated")
  @Deprecated
  public PlayerPlaytimeLimitV2Entry updateSecondsAccumulated(
      @RequestParam("id") User user,
      @RequestParam("secondsAccumulated") long secondsAccumulated) {
    return playtimeLimitsV2Service.updateSecondsAccumulated(user, secondsAccumulated);
  }
}
