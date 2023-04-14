package lithium.service.notifications.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.gateway.client.stream.GatewayExchangeStream;
import lithium.service.notifications.client.objects.InboxMessagePlaceholderReplacement;
import lithium.service.notifications.client.objects.UserNotification;
import lithium.service.notifications.data.entities.Domain;
import lithium.service.notifications.data.entities.Inbox;
import lithium.service.notifications.data.entities.Notification;
import lithium.service.notifications.data.repositories.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class NotificationProcessor {
	@Autowired NotificationRepository notificationRepository;
	@Autowired InboxService inboxService;
	@Autowired GatewayExchangeStream gatewayExchangeStream;
	@Autowired private InboxUserService inboxUserService;
	@Autowired private InboxLabelValueService inboxLabelValueService;

	@Async
	public void process(String userGuid, String notificationName, List<InboxMessagePlaceholderReplacement> phReplacements) throws Exception {
		process(UserNotification.builder().userGuid(userGuid).notificationName(notificationName).phReplacements(phReplacements).cta(false).build());
	}

	@Async
	public void process(UserNotification userNotification) throws Exception {
		String[] domainAndUser = userNotification.getUserGuid().split("/");
		if (domainAndUser.length != 2) throw new Exception("Invalid userGuid");
		Notification notification = notificationRepository.findByDomainNameAndName(domainAndUser[0], userNotification.getNotificationName());
		if (notification != null) {
			Inbox item = inboxService.addToInbox(notification.getDomain(), userNotification.getUserGuid(), notification, notification.getMessage(), userNotification.getPhReplacements(), userNotification.isCta());

			inboxLabelValueService.save(item, userNotification.getMetaData());

			// Do placeholder replacements on the message before sending to the front-end
			item.setMessage(replaceMessagePlaceholders(item));
			//processPopup(notification.getDomain(), userGuid, item);
			inboxUserService.updateSummaryFromInbox(item, false);
		} else {
			log.warn("Notification with name " + userNotification.getNotificationName() + " for domain " + domainAndUser[0] + " not found");
		}
	}

	private void processPopup(Domain domain, String userGuid, Inbox item) {
		log.info("Sending popup for user: "+userGuid);
		final ObjectMapper mapper = new ObjectMapper();
		try {
			item.setPhReplacements(null);
			String json = mapper.writeValueAsString(item);
			log.debug("Json : "+json);
			gatewayExchangeStream.process("playerroom/"+userGuid, "popup", json);
		} catch (Exception e) {
			log.error("Error streaming new content to playerroom/"+userGuid, e);
		}
	}

	/**
	 * Accepts a non null inbox item (required!). Does placeholder replacements on the message and returns the message post replacements.
	 *
	 * @param item
	 * @return
	 */
	private String replaceMessagePlaceholders(Inbox item) {
		if (item == null) throw new IllegalArgumentException("Improper usage of *private* method. Item should not be null ;)");
		String text = item.getMessage();
		if (item.getPhReplacements() != null && item.getMessage() != null && !item.getMessage().isEmpty()) {
			for (lithium.service.notifications.data.entities.InboxMessagePlaceholderReplacement phReplacement: item.getPhReplacements()) {
				text = text.replace(phReplacement.getKey(), phReplacement.getValue());
			}
		}
		return text;
	}
}
