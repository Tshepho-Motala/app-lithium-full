package lithium.service.user.stream;

import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.user.client.enums.UserLinkTypes;
import lithium.service.user.client.objects.AutoRegistration;
import lithium.service.user.data.entities.User;
import lithium.service.user.services.DomainService;
import lithium.service.user.services.SignupService;
import lithium.service.user.services.UserLinkService;
import lithium.service.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@EnableBinding(RegistrationSuccessQueueSink.class)
@Slf4j
public class RegistrationSuccessQueueProcessor {

  @Autowired UserService userService;
  @Autowired CachingDomainClientService cachingDomainClientService;
	@Autowired SignupService signupService;
	@Autowired DomainService domainService;
	@Autowired ModelMapper modelMapper;
	@Autowired UserLinkService userLinkService;
	@Autowired CachingDomainClientService domainClientService;

	@StreamListener(RegistrationSuccessQueueSink.INPUT)
	void handle(AutoRegistration registrationSuccess) throws Exception {
    User user = userService.findOne(registrationSuccess.getUserId());
    log.debug("Received a message from the queue for processing: User -> {}",  user);

    // Is registration on ecosystem brand?
    if (cachingDomainClientService.isDomainInAnyEcosystem(user.domainName())) {
      log.debug("User is part of an ecosystem");
      // Is registration on a betting brand? (i.e. is the domain the user is registered of the mutually exclusive type?)
      if (cachingDomainClientService.isDomainNameOfEcosystemMutuallyExclusiveType(user.domainName())) {
        // Is there an existing account for the player on the ecosystem root domain?
        // First retrieve the root ecosystem domain
        Optional<String> findRootDomainName = cachingDomainClientService.findEcosystemRootByDomainName(user.domainName());
        if (findRootDomainName.isPresent()) {
          String rootDomainName = findRootDomainName.get();
          // Checks if the user has been created on the root domain (Email is unique in ecosystem , cellphone could be duplicated)
          final List<User> findRootUser = userService.findByDomainNameAndEmail(rootDomainName, user.getEmail());
          if (findRootUser.isEmpty()) {

            //Get the single channel domain setting for LSBET domain
            Optional<String> singleOptInSetting = domainClientService.retrieveDomainFromDomainService(user.getDomain().getName()).findDomainSettingByName(DomainSettings.SINGLE_OPT_ALL_CHANNELS.key());
            boolean singleOptInAllowed = singleOptInSetting.isPresent() && singleOptInSetting.get().equalsIgnoreCase("true");

            // User is not created on the root ecosystem domain; now creating the user
            // As a rule, if a domain forms part of an ecosystem, no duplicates should be allowed on a domain.
            signupService.createUserOnRootEcosystemDomain(rootDomainName, user, registrationSuccess, singleOptInAllowed);
          }
          else {
            //We have to update the communication consent for the root domain
            for(User rootUser: findRootUser) {
              userLinkService.syncCommunicationChannelsForRoot(rootUser, user, registrationSuccess.isChannelOptOut());
            }
          }
        }
      }
      // ---Automated player linking within the ecosystem---
      Optional<String> findRootDomainName = cachingDomainClientService.findEcosystemRootByDomainName(user.domainName());
      if (findRootDomainName.isPresent()) {
        String rootDomainName = findRootDomainName.get();
        if (!rootDomainName.equals(user.getDomain().getName())) {
          Optional<User> byRootDomainAndEmail = userService.findByDomainNameAndEmail(rootDomainName, user.getEmail()).stream().findFirst();
          if (byRootDomainAndEmail.isPresent()) {
            User primaryUser = byRootDomainAndEmail.get();
            User secondaryUser = user;
            String linkNote = "Automated player linking within the ecosystem";
            userLinkService.findAndUpdateOrCreateUserLink(primaryUser, secondaryUser, UserLinkTypes.CROSS_DOMAIN_LINK, linkNote);
            userLinkService.findAndUpdateOrCreateUserLink(secondaryUser, primaryUser, UserLinkTypes.CROSS_DOMAIN_LINK, linkNote);
          }
        }
      }
    }
  }
}
