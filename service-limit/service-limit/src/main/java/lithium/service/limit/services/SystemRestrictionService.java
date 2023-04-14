package lithium.service.limit.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.data.entities.Domain;
import lithium.service.limit.data.entities.DomainRestriction;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.Restriction;
import lithium.service.limit.enums.SystemRestriction;
import lithium.service.notifications.client.enums.SystemNotification;
import lithium.service.notifications.client.objects.Channel;
import lithium.service.notifications.client.objects.Notification;
import lithium.service.notifications.client.objects.NotificationChannel;
import lithium.service.notifications.client.stream.NotificationStream;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class SystemRestrictionService {
	@Autowired private LithiumServiceClientFactory services;
	@Autowired private RestrictionService service;
	@Autowired private NotificationStream notificationStream;

	/**
	 * We need to automagically create a few DomainRestrictionSet's, i.e. System Restrictions.
	 * They will function similarly to a normal DomainRestrictionSet, with a few special requirements:-
	 *  1. They will be distinguishable from normal DomainRestrictionSet's in the Restriction Dictionary per domain.
	 *  2. The ability to disable, delete, or change in any way will not be permitted.
	 *
	 * https://playsafe.atlassian.net/browse/LIVESCORE-1331.
	 */
	public void createSystemRestrictions() {
		try {
			DomainClient domainClient = getDomainClient();
			Iterable<lithium.service.domain.client.objects.Domain> domains = domainClient.findAllDomains().getData();
			Iterator<lithium.service.domain.client.objects.Domain> iterator = domains.iterator();
			while (iterator.hasNext()) {
				lithium.service.domain.client.objects.Domain domain = iterator.next();
				if (domain.getPlayers() != null && domain.getPlayers()) createSystemRestrictions(domain);
			}
		} catch (Exception e) {
			log.error("Unable to create system restrictions. " + e.getMessage(), e);
		}
	}

	/**
	 * To add a new System Restriction, modify the SystemRestriction enum.
	 *
	 * @see SystemRestriction
	 */
	/*
	 * TODO: We need a mechanism to handle new domain registrations.
	 *       Perhaps a queue that multiple services may listen to. In service-limit, the method below would be called on
	 *       a new domain registration. Until implemented, service-limit would need to be restarted after a new domain
	 *       is registered in order to create the system restrictions.
	 */
	public void createSystemRestrictions(lithium.service.domain.client.objects.Domain domain)
			throws Status500InternalServerErrorException {
		Arrays.stream(SystemRestriction.values()).forEach(systemRestriction -> {
			try {
				createSystemRestriction(domain.getName(), systemRestriction);
				createSystemNotification(domain);
			} catch (Status500InternalServerErrorException e) {
				log.error("Could not create system restriction [domain.name="+domain.getName()
					+", restrictionName="+systemRestriction.restrictionName()
					+", restrictionCodes="+systemRestriction.restrictionCodes()+"] " + e.getMessage(), e);
			}
		});
	}

	private void createSystemRestriction(String domainName, SystemRestriction systemRestriction)
			throws Status500InternalServerErrorException {

		if (service.findByDomainAndName(domainName, systemRestriction.restrictionName()) != null) {
			// Exists. Do nothing.
			return;
		}

		List<DomainRestriction> restrictions = new ArrayList<>();
		for (String restrictionCode: systemRestriction.restrictionCodes()) {
			restrictions.add(
				DomainRestriction.builder()
				.restriction(Restriction.builder().code(restrictionCode).build())
				.build()
			);
		}

		DomainRestrictionSet set = DomainRestrictionSet.builder()
				.name(systemRestriction.restrictionName())
				.domain(Domain.builder().name(domainName).build())
				.systemRestriction(true)
				.restrictions(restrictions)
				.dwhVisible(systemRestriction.dwhVisible())
				.altMessageCount(systemRestriction.altMessageCount())
				.communicateToPlayer(systemRestriction.communicateToPlayer())
				.build();

		service.create(set, User.SYSTEM_GUID);

	}

	private DomainClient getDomainClient() throws Status550ServiceDomainClientException {
		try {
			return services.target(DomainClient.class, "service-domain", true);
		} catch (LithiumServiceClientFactoryException e) {
			throw new Status550ServiceDomainClientException(e);
		}
	}

	public void createSystemNotification(lithium.service.domain.client.objects.Domain domain) {
		if(domain.getPlayers()) {
			for(SystemNotification systemNotification: SystemNotification.values()) {

				NotificationChannel notificationChannel = NotificationChannel
						.builder()
						.channel(
								Channel.builder()
								.name(systemNotification.channel())
								.build())
						.templateLang(systemNotification.templateLang())
						.templateName(systemNotification.templateName())
						.forced(systemNotification.forced())

						.build();
				List<NotificationChannel> channels = new ArrayList<>();
				channels.add(notificationChannel);

				Notification notification = Notification.builder()
						.systemNotification(true)
						.name(systemNotification.notificationName())
						.displayName(systemNotification.displayName())
						.description(systemNotification.description())
						.message(systemNotification.message())
						.domain(lithium.service.notifications.client.objects.Domain.builder().name(domain.getName()).build())
						.channels(channels)
						.build();

				notificationStream.createOrUpdateNotification(notification);
			}
		}
	}
}
