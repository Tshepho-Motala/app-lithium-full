package lithium.service.cashier.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.cashier.machine.DoMachineContext;
import lithium.service.notifications.client.objects.InboxMessagePlaceholderReplacement;
import lithium.service.notifications.client.objects.UserNotification;
import lithium.service.notifications.client.stream.NotificationStream;

@Service
public class CashierNotificationService extends CashierCommunicationService {
	@Autowired NotificationStream notificationStream;

	public void queuePlayerNotification(String status, DoMachineContext context) {
		if (isStatusValid(status.toLowerCase())) {

			List<InboxMessagePlaceholderReplacement> phrList = constructPlaceholders(context).stream()
					.map(InboxMessagePlaceholderReplacement::fromPlaceholder)
					.collect(Collectors.toList());
			String templateName = cashierTransactionStatusNotification(context.getTransaction().getTransactionType().name(), status, RECIPIENT_PLAYER);
			notificationStream.process(
				UserNotification.builder()
				.userGuid(context.getUser().getGuid())
				.notificationName(templateName)
				.phReplacements(phrList)
				.build()
			);
		}
	}
}
