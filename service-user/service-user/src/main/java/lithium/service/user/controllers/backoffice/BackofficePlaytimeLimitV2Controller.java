package lithium.service.user.controllers.backoffice;

import java.util.List;
import lithium.exceptions.Status414UserNotFoundException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.exceptions.Status438PlayTimeLimitConfigurationNotFoundException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.data.dto.PlayerPlaytimeLimitConfigRequest;
import lithium.service.user.data.dto.PlayerPlaytimeLimitV2ConfigDto;
import lithium.service.user.data.dto.PlayerPlaytimeLimitV2EntryDto;
import lithium.service.user.services.PlaytimeLimitsV2Service;
import lithium.tokens.LithiumTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/backoffice/playtime-limit/v2")
public class BackofficePlaytimeLimitV2Controller {

  private final PlaytimeLimitsV2Service playtimeLimitsV2Service;

  @PostMapping("/configuration/set")
  public PlayerPlaytimeLimitV2ConfigDto setPlayerConfiguration(@RequestBody PlayerPlaytimeLimitConfigRequest playerPlaytimeLimitConfigRequest,
      LithiumTokenUtil tokenUtil)
      throws Status414UserNotFoundException, Status426InvalidParameterProvidedException, Status550ServiceDomainClientException {
    return new PlayerPlaytimeLimitV2ConfigDto(playtimeLimitsV2Service.setPlayerConfiguration(playerPlaytimeLimitConfigRequest, tokenUtil));
  }

  @PostMapping("/configuration/get")
  public PlayerPlaytimeLimitV2ConfigDto getPlayerConfiguration(@RequestParam("playerGuid") String playerGuid) throws Status414UserNotFoundException {
    try {
      return new PlayerPlaytimeLimitV2ConfigDto(playtimeLimitsV2Service.getPlayerConfiguration(playerGuid));
    } catch (Status438PlayTimeLimitConfigurationNotFoundException e) {
      log.debug(e.getMessage(), e);
      return null;
    }
  }

  @PostMapping("/active-entry/get")
  public PlayerPlaytimeLimitV2EntryDto updateAndGetPlayerEntry(@RequestParam("playerGuid") String playerGuid)
      throws Status414UserNotFoundException, Status550ServiceDomainClientException, Status426InvalidParameterProvidedException {
    return new PlayerPlaytimeLimitV2EntryDto(playtimeLimitsV2Service.updateAndGetPlayerEntry(playerGuid));
  }

  @PostMapping("/migrate-v1-data")
  public void migrateV1Data(LithiumTokenUtil tokenUtil, @RequestParam(value = "pageSize", required = false) Integer pageSize)
      throws Status550ServiceDomainClientException {
    playtimeLimitsV2Service.migrateV1Data(tokenUtil, pageSize);
  }

  @GetMapping("/active-granularity/get")
  public List<lithium.service.user.client.objects.Granularity> getActiveGranularities() {
    return playtimeLimitsV2Service.getActiveGranularities();
  }

  @PostMapping("/configuration/remove-pending")
  public void removePendingPlayTimeLimitConfigurationById(@RequestParam("configId") long configId, LithiumTokenUtil tokenUtil) {
    playtimeLimitsV2Service.removePendingPlayTimeLimitConfigurationById(configId, tokenUtil);
  }

}
