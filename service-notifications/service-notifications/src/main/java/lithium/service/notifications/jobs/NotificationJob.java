package lithium.service.notifications.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.mail.client.stream.MailStream;
import lithium.service.notifications.config.ServiceNotificationsConfigurationProperties;
import lithium.service.notifications.data.entities.Channel;
import lithium.service.notifications.data.entities.Domain;
import lithium.service.notifications.data.entities.Inbox;
import lithium.service.notifications.data.entities.InboxMessagePlaceholderReplacement;
import lithium.service.notifications.data.entities.Inbox_;
import lithium.service.notifications.data.entities.NotificationChannel;
import lithium.service.notifications.data.repositories.InboxRepository;
import lithium.service.notifications.data.specifications.InboxSpecifications;
import lithium.service.pushmsg.client.objects.PushMsgBasic;
import lithium.service.pushmsg.client.stream.PushMsgStream;
import lithium.service.sms.client.objects.SMSBasic;
import lithium.service.sms.client.stream.SMSStream;
import lithium.service.user.client.UserApiInternalClient;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class NotificationJob {
	@Autowired InboxRepository repository;
	@Autowired MailStream mailStream;
	@Autowired SMSStream smsStream;
	@Autowired PushMsgStream pushMsgStream;
	@Autowired LithiumServiceClientFactory services;
	@Autowired ServiceNotificationsConfigurationProperties properties;
	@Autowired LeaderCandidate leaderCandidate;
	
	@Scheduled(fixedDelay=5000)
	public void process() {
		log.debug("Processing notifications");
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		DateTime createdDateBefore = new DateTime().minusMinutes(properties.getMinsBeforeProcessingNotificationChannels());

		List<String> channels = Arrays.asList(Channel.CHANNEL_SMS, Channel.CHANNEL_EMAIL, Channel.CHANNEL_PUSH);

		Specification<Inbox> spec = Specification.where(InboxSpecifications.read(false))
				.and(InboxSpecifications.processing(false))
				.and(InboxSpecifications.processed(false))
				.and(InboxSpecifications.createdBefore(createdDateBefore.toDate()))
				.and(InboxSpecifications.withChannels(channels));

		Sort sort = Sort.by(Sort.Direction.DESC, Inbox_.createdDate.getName());
		PageRequest pageRequest = PageRequest.of(0, 10, sort);
		Page<Inbox> list = repository.findAll(spec, pageRequest);

		log.debug("Found " + list.getContent().size() + " unread, unprocessed items in inbox");

		list.getContent().forEach(item -> {
			item.setProcessing(true);
			item = repository.save(item);
			lithium.service.user.client.objects.User user = userApiInternalClient().getUser(item.getUser().getGuid()).getData();
			for (NotificationChannel notificationChannel: item.getNotification().getChannels()) {
				log.info("Processing notification | user: " + user + ", notificationChannel: " + notificationChannel);
				switch (notificationChannel.getChannel().getName()) {
					case Channel.CHANNEL_SMS: processSMS(item.getNotification().getDomain(), user, item, notificationChannel); break;
					case Channel.CHANNEL_EMAIL: processMail(item.getNotification().getDomain(), user, item, notificationChannel); break;
					case Channel.CHANNEL_PUSH: processPush(item.getNotification().getDomain(), user, item, notificationChannel); break;
					default:
						log.error("Unknown channel: " + notificationChannel.getChannel().getName());
						break;
				}
			}
			item.setProcessing(false);
			item.setProcessed(true);
			item = repository.save(item);
		});
	}
	
	private void processSMS(Domain domain, lithium.service.user.client.objects.User user, Inbox item, NotificationChannel notificationChannel) {
		if ((user.getSmsOptOut() == null) || (!user.getSmsOptOut()) || (user.getSmsOptOut() && notificationChannel.getForced())) {
			if (user.isCellphoneValidated()) {
				smsStream.process(SMSBasic.builder()
					.domainName(user.getDomain().getName())
					.smsTemplateName(notificationChannel.getTemplateName())
					.smsTemplateLang(notificationChannel.getTemplateLang())
					.to(user.getCellphoneNumber())
					.userGuid(user.guid())
					.priority(1)
					.legacyPlaceholders(placeholders(item))
					.build()
				);
			} else {
				log.info("SMS not processed for " + user.guid() + ". User cellphone number is not validated");
			}
		} else {
			log.info("SMS not processed for " + user.guid() + ". User has opted out and the notification channel is not forced");
		}
	}
	
	private void processMail(Domain domain, lithium.service.user.client.objects.User user, Inbox item, NotificationChannel notificationChannel) {
		if ((user.getEmailOptOut() == null) || (!user.getEmailOptOut()) || (user.getEmailOptOut() && notificationChannel.getForced())) {
			if (user.isEmailValidated()) {
				mailStream.process(EmailData.builder()
						.authorSystem()
						.domainName(user.getDomain().getName())
						.emailTemplateName(notificationChannel.getTemplateName())
						.emailTemplateLang(notificationChannel.getTemplateLang())
						.to(user.getEmail())
						.userGuid(user.guid())
						.priority(1)
						.legacyPlaceholders(placeholders(item))
						.build()
				);
			} else {
				log.info("Mail not processed for " + user.guid() + ". User email is not validated");
			}
		} else {
			log.info("Mail not processed for " + user.guid() + ". User has opted out and the notification channel is not forced");
		}
	}
	
	private void processPush(Domain domain, lithium.service.user.client.objects.User user, Inbox item, NotificationChannel notificationChannel) {
		log.info("sending pushmsg for user: "+user.guid());
		if ((user.getPushOptOut() == null) || (!user.getPushOptOut()) || (user.getPushOptOut() && notificationChannel.getForced())) {
			List<String> userGuids = new ArrayList<>();
			userGuids.add(user.guid());
			pushMsgStream.process(
				PushMsgBasic.builder()
				.domainName(domain.getName())
				.templateId(notificationChannel.getTemplateName())
				.language(notificationChannel.getTemplateLang())
				.priority(1)
				.userGuids(userGuids)
				.placeholders(placeholders(item))
				.build()
			);
		} else {
			log.info("Pushmsg not processed for " + user.guid() + ". User has opted out and the notification channel is not forced");
		}
	}
	
	private Map<String, String> placeholders(Inbox inbox) {
		Map<String, String> placeholders = new LinkedHashMap<String, String>();
		placeholders.put("%inboxId%", String.valueOf(inbox.getId()));
		if (inbox.getPhReplacements() != null) {
			for (InboxMessagePlaceholderReplacement phReplacement: inbox.getPhReplacements()) {
				placeholders.put(phReplacement.getKey(), phReplacement.getValue());
			}
		}
		return placeholders;
	}
	
	private UserApiInternalClient userApiInternalClient() {
		UserApiInternalClient client = null;
		try {
			client = services.target(UserApiInternalClient.class, "service-user", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return client;
	}
}
