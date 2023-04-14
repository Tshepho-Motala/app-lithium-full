package lithium.service.limit.services;

import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.mail.client.stream.MailStream;
import lithium.service.notifications.client.objects.InboxMessagePlaceholderReplacement;
import lithium.service.notifications.client.objects.UserNotification;
import lithium.service.notifications.client.stream.NotificationStream;
import lithium.service.sms.client.objects.SMSBasic;
import lithium.service.sms.client.stream.SMSStream;
import lithium.service.user.client.objects.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



@Service
public class PlayerCommsService {
	@Autowired private MailStream mail;
	@Autowired private SMSStream sms;
	@Autowired private NotificationStream notification;

	private static final String DEFAULT_LANG = "en";

	private static final int PRIORITY_HIGH = 1;

	public void communicateWithPlayer(String templateName, User user, Set<Placeholder> placeholders) {
		sendMail(templateName, user, placeholders);
		sendSMS(templateName, user, placeholders);
		sendNotification(templateName, user, placeholders);
	}

	private void sendMail(String templateName, User user, Set<Placeholder> placeholders) {
		if (notNullOrEmpty(user.getEmail())) {
			mail.process(
					EmailData.builder()
							.authorSystem()
							.domainName(user.getDomain().getName())
							.emailTemplateName(templateName)
							.emailTemplateLang(DEFAULT_LANG)
							.to(user.getEmail())
							.priority(PRIORITY_HIGH)
							.userGuid(user.guid())
							.placeholders(placeholders)
							.build()
			);
		}
	}

	private void sendSMS(String templateName, User user, Set<Placeholder> placeholders) {
		if (notNullOrEmpty(user.getCellphoneNumber())) {
			sms.process(
				SMSBasic.builder()
				.domainName(user.getDomain().getName())
				.smsTemplateName(templateName)
				.smsTemplateLang(DEFAULT_LANG)
				.to(user.getCellphoneNumber())
				.priority(PRIORITY_HIGH)
				.userGuid(user.guid())
				.placeholders(placeholders)
				.build()
			);
		}
	}

	private void sendNotification(String notificationName, User user, Set<Placeholder> placeholders) {
		List<InboxMessagePlaceholderReplacement> nPlaceholders = placeholders.stream()
				.map(InboxMessagePlaceholderReplacement::fromPlaceholder)
				.collect(Collectors.toList());
		notification.process(
			UserNotification.builder()
			.notificationName(notificationName)
			.userGuid(user.guid())
			.phReplacements(nPlaceholders)
			.build()
		);
	}

	public boolean notNullOrEmpty(final String data) {
		if (data != null && !data.trim().isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}
