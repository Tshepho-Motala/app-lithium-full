package lithium.service.user.api.frontend.controllers;

import java.util.List;
import lithium.exceptions.Status414UserNotFoundException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.exceptions.Status438PlayTimeLimitConfigurationNotFoundException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.data.entities.playtimelimit.PlayTimeLimitSetRequest;
import lithium.service.user.data.entities.playtimelimit.PlayerPlayTimeLimit;
import lithium.service.user.services.PlaytimeLimitsV2Service;
import lithium.tokens.LithiumTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @deprecated LSPLAT-5758 Currently being kept for backwards compatibility but should be removed in future
 */
@Deprecated(since="3.07", forRemoval=true)
@RestController
@RequiredArgsConstructor
@RequestMapping("/frontend/play-time-limit")
public class FrontendPlaytimeLimitV1Controller {

  private final PlaytimeLimitsV2Service playtimeLimitsV2Service;
  private final MessageSource messageSource;

  @PostMapping("/set")
  public PlayerPlayTimeLimit set(@RequestBody PlayTimeLimitSetRequest playTimeLimitSetRequest, LithiumTokenUtil tokenUtil)
      throws Status500InternalServerErrorException {
    try {
      return playtimeLimitsV2Service.setPlayTimeLimitForUser(tokenUtil.id(), playTimeLimitSetRequest.getGranularity(),
          playTimeLimitSetRequest.getDurationInMins(), tokenUtil);
    } catch (Status426InvalidParameterProvidedException e) {
      throw new Status500InternalServerErrorException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.INVALID_GRANULARITY",
          new Object[]{new lithium.service.translate.client.objects.Domain(tokenUtil.domainName())}, "Invalid granularity.",
          LocaleContextHolder.getLocale()));
    } catch (Status414UserNotFoundException e) {
      throw new Status500InternalServerErrorException(e.getMessage());
    } catch (Status550ServiceDomainClientException e) {
      throw new Status500InternalServerErrorException(e.getMessage());
    } catch (Status438PlayTimeLimitConfigurationNotFoundException e) {
      throw new Status500InternalServerErrorException(e.getMessage());
    }
  }

  @GetMapping("/get")
  public List<PlayerPlayTimeLimit> get(LithiumTokenUtil tokenUtil)
      throws Status414UserNotFoundException, Status438PlayTimeLimitConfigurationNotFoundException, Status426InvalidParameterProvidedException, Status550ServiceDomainClientException {
    return playtimeLimitsV2Service.getUserPlayTimeLimits(tokenUtil.guid());
  }
}
