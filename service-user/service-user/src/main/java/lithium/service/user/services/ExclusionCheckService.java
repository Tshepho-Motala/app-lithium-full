package lithium.service.user.services;

import static lithium.service.user.client.enums.StatusReason.GAMSTOP_SELF_EXCLUSION;

import lithium.service.Response;
import lithium.service.access.client.gamstop.GamstopClient;
import lithium.service.access.client.gamstop.objects.CheckExclusionRequest;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.objects.User;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExclusionCheckService {

  @Autowired
  @Setter
  private LithiumServiceClientFactory services;

  @Autowired
  @Setter
  private UserService userService;

  @Setter
  @Autowired
  private UserProviderService userProviderService;

  public Response<User> checkGamstopStatus(Response<User> userResponse, String domainName, String username) {
    if (userResponse.getData() == null) {
      try {
        lithium.service.user.data.entities.User userEntity = userService.findByUsernameThenEmailThenCell(domainName.toLowerCase(), username);
        if (userEntity != null && userEntity.getStatusReason() != null) {
          lithium.service.user.client.enums.StatusReason statusReason = lithium.service.user.client.enums.StatusReason
              .fromName(userEntity.getStatusReason().getName());
          if (statusReason == GAMSTOP_SELF_EXCLUSION) {
            GamstopClient gamstopClient = getGamstopClient();
            CheckExclusionRequest checkExclusionRequest = CheckExclusionRequest.builder()
                .domainName(userEntity.getDomain().getName())
                .userGuid(userEntity.guid())
                .build();
            gamstopClient.checkExclusion(checkExclusionRequest);
            return userProviderService.findLocalUser(userEntity.getDomain().getName(), userEntity.getUsername());
          }
        }
      } catch (Exception ex){
        log.error("Failed to check gamstop exclusion, {}", userResponse, ex);
      }
    }
    return userResponse;
  }

  public GamstopClient getGamstopClient() throws Status550ServiceDomainClientException {
    try {
      return services.target(GamstopClient.class, "service-access-provider-gamstop", true);
    } catch (LithiumServiceClientFactoryException fe) {
      throw new Status550ServiceDomainClientException(fe);
    }
  }
}
