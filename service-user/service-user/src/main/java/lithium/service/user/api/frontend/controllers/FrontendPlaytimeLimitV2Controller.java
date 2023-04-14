package lithium.service.user.api.frontend.controllers;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/frontend/playtime-limit/v2")
public class FrontendPlaytimeLimitV2Controller {

  private final PlaytimeLimitsV2Service playtimeLimitsV2Service;

  @PostMapping("/configuration/set")
  public PlayerPlaytimeLimitV2ConfigDto setPlayerConfiguration(@RequestBody PlayerPlaytimeLimitConfigRequest playerPlaytimeLimitConfigRequest,
      LithiumTokenUtil tokenUtil)
      throws Status414UserNotFoundException, Status426InvalidParameterProvidedException, Status550ServiceDomainClientException {
    playerPlaytimeLimitConfigRequest.setUserId(tokenUtil.id());
    return new PlayerPlaytimeLimitV2ConfigDto(playtimeLimitsV2Service.setPlayerConfiguration(playerPlaytimeLimitConfigRequest, tokenUtil));
  }

  @GetMapping("/configuration/get")
  public PlayerPlaytimeLimitV2ConfigDto getPlayerConfiguration(LithiumTokenUtil tokenUtil)
      throws Status414UserNotFoundException, Status438PlayTimeLimitConfigurationNotFoundException {
    return new PlayerPlaytimeLimitV2ConfigDto(playtimeLimitsV2Service.getPlayerConfiguration(tokenUtil.guid()));
  }

  @PostMapping("/active-entry/get")
  public PlayerPlaytimeLimitV2EntryDto updateAndGetPlayerEntry(LithiumTokenUtil tokenUtil)
      throws Status414UserNotFoundException, Status550ServiceDomainClientException, Status426InvalidParameterProvidedException {
    return new PlayerPlaytimeLimitV2EntryDto(playtimeLimitsV2Service.updateAndGetPlayerEntry(tokenUtil.guid()));
  }

}
