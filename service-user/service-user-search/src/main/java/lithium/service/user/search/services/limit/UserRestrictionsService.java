package lithium.service.user.search.services.limit;

import lithium.service.user.client.objects.RestrictionData;
import lithium.service.user.client.objects.RestrictionsMessageType;
import lithium.service.user.search.data.entities.DomainRestriction;
import lithium.service.user.search.data.entities.User;
import lithium.service.user.search.data.entities.UserRestriction;
import lithium.service.user.search.data.repositories.user_search.UserRestrictionsRepository;
import lithium.service.user.search.data.repositories.user_search.DomainRestrictionsRepository;
import lithium.service.user.search.services.user_search.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Slf4j
@Service(value = "user_search.UserRestrictionsService")
public class UserRestrictionsService {

  @Autowired
  @Qualifier("user_search.UserService")
  private UserService userService;

  @Autowired
  @Qualifier("user_search.UserRestrictionsRepository")
  private UserRestrictionsRepository userRestrictionsRepository;

  @Autowired
  @Qualifier("user_search.DomainRestrictionsRepository")
  private DomainRestrictionsRepository domainRestrictionsRepository;

  public void processUserRestrictionRuleSets(RestrictionData data) {

    RestrictionsMessageType messageType = data.getMessageType();

    DomainRestriction domainRestriction = getDomainRestriction(data);
    updateDomainRestriction(data, domainRestriction);

    if (messageType.equals(RestrictionsMessageType.DOMAIN_SET_UPDATE)) return;

    User user = userService.findOrCreateUser(data.getGuid());
    UserRestriction userRestriction = userRestrictionsRepository.findByUserAndDomainRestriction(user, domainRestriction);

    if (messageType.equals(RestrictionsMessageType.USER_SET_DELETE)) {
      deleteUserRestriction(data, userRestriction);
    } else if(messageType.equals(RestrictionsMessageType.USER_SET_UPDATE)) {
      updateUserRestriction(data, domainRestriction, user, userRestriction);
    }
  }

  private UserRestriction updateUserRestriction(RestrictionData data, DomainRestriction domainRestriction, User user, UserRestriction userRestriction) {
    if (userRestriction == null) {
      userRestriction = UserRestriction.builder()
          .domainRestriction(domainRestriction)
          .user(user)
          .build();
    }
    userRestriction.setActiveFrom(data.getActiveFrom());
    userRestriction.setActiveTo(data.getActiveTo());
    return userRestrictionsRepository.save(userRestriction);
  }

  private void deleteUserRestriction(RestrictionData data, UserRestriction userRestriction) {
    if (userRestriction != null) {
      userRestrictionsRepository.delete(userRestriction);
    } else {
      log.error("User restriction already deleted : " + data);
    }
  }

  private DomainRestriction getDomainRestriction(RestrictionData data) {
    return domainRestrictionsRepository.findById(data.getDomainRestrictionId())
        .orElse(DomainRestriction.builder().build());
  }

  private DomainRestriction updateDomainRestriction(RestrictionData data, DomainRestriction domainRestriction) {
    // DomainRestrictions only for update.Never removed
    domainRestriction.setId(data.getDomainRestrictionId());
    domainRestriction.setName(data.getDomainRestrictionName());
    domainRestriction.setDomainName(data.getDomainName());
    domainRestriction.setEnabled(data.isEnabled());
    domainRestriction.setDeleted(data.isDeleted());
    return domainRestrictionsRepository.save(domainRestriction);
  }
}
