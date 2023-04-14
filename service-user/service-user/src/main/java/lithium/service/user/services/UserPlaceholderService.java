package lithium.service.user.services;


import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.utils.UserToPlaceholderBinder;
import lithium.service.user.data.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_OPT_OUT_EMAIL_URL;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_VERIFICATION_STATUS;

@Service
@Slf4j
public class UserPlaceholderService {

  @Autowired
  private UserService userService;
  @Autowired
  private CachingDomainClientService cachingDomainClientService;
  @Autowired
  private LimitInternalSystemService limitInternalSystemService;

  public Set<Placeholder> getPlaceholdersForGuid(String guid) throws Exception {
    User user = userService.findFromGuid(guid);
    if (user == null) {
      log.warn("Can't build placeholders for userGuid:" + guid + " User not found");
      throw new UserNotFoundException();
    }
    Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(user.domainName());
    return getPlaceholdersWithExternalData(user, domain);
  }

  public Set<Placeholder> getPlaceholdersWithExternalData(User user, Domain domain) {
    Set<Placeholder> placeholders = new UserToPlaceholderBinder(userService.convertUser(user)).completePlaceholders();
    placeholders.add(userVerificationStatus(user));
    placeholders.add(userOptOutEmailUrl(user, domain));
    return placeholders;
  }

  private Placeholder userVerificationStatus(User user) {
    if (user.getVerificationStatus() != null) {
      try {
        String verificationStatusCode = limitInternalSystemService.getVerificationStatusCode(user.getVerificationStatus());
        return USER_VERIFICATION_STATUS.from(verificationStatusCode);
      } catch (Status500LimitInternalSystemClientException e) {
        log.error("Unable to add verification status placeholder to user: " + user, e);
      }
    }
    return USER_VERIFICATION_STATUS.from(Optional.empty());
  }

  private Placeholder userOptOutEmailUrl(User user, Domain domain) {
    try {
      StringBuilder parameters = new StringBuilder()
          .append("userid=" + user.getId())
          .append("&guid=" + domain.getName() + "/" + user.getUsername())
          .append("&fullName=" + user.getFirstName() + " " + user.getLastName())
          .append("&email=" + user.getEmail())
          .append("&cell=" + user.getCellphoneNumber())
          .append("&optout=true")
          .append("&method=email");
      String optOutEmailUrl = domain.getUrl() + "?action=optout&h=" + Base64.getEncoder().encodeToString(parameters.toString().getBytes("UTF-8"));
      return USER_OPT_OUT_EMAIL_URL.from(optOutEmailUrl);
    } catch (UnsupportedEncodingException e) {
      log.warn("cant construct optOutEmailUrl placeholder for userGuid:" + user.guid());
      return USER_OPT_OUT_EMAIL_URL.from(Optional.empty());
    }
  }

}
