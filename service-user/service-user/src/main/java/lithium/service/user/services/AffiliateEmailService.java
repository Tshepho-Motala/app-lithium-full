package lithium.service.user.services;

import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.user.data.entities.Group;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.repositories.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class AffiliateEmailService extends UserValidationBaseService {
	@Autowired GroupRepository groupRepository;
  @Autowired private CachingDomainClientService cachingDomainClientService;
	
	public void sendRegistrationSuccessEmail(User user) throws Exception {
		Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(user.getDomain().getName());
    Set<Placeholder> placeholders = constructBasicPlaceholders(domain, user);
    mailStream.process(
        EmailData.builder()
            .authorSystem()
            .emailTemplateName("affiliate.reg.success")
            .emailTemplateLang("en")
            .to(user.getEmail())
            .priority(MAIL_PRIORITY_HIGH)
            .userGuid(user.guid())
            .placeholders(placeholders)
            .domainName(user.getDomain().getName())
            .build()
    );
		sendPendingRegistrationEmail();
	}

	public void sendPendingRegistrationEmail() throws Exception {
		Group group = groupRepository.findByName("AdminGroup");
		log.debug("Group " + group);
		if (group != null) {
			List<User> users = userService.findAllByGroupsContains(group);
			log.debug("found " + users.size() + " users in group " + group.getName());
			for (User user: users) {
				Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(user.getDomain().getName());
        Set<Placeholder> placeholders = constructBasicPlaceholders(domain, user);
        mailStream.process(
            EmailData.builder()
                .authorSystem()
                .emailTemplateName("affiliate.reg.pending")
                .emailTemplateLang("en")
                .to(user.getEmail())
                .priority(MAIL_PRIORITY_HIGH)
                .userGuid(user.guid())
                .placeholders(placeholders)
                .domainName(user.getDomain().getName())
                .build()
        );
			}
		}
	}
}
