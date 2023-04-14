package lithium.service.cashier.services;

import lithium.service.cashier.client.objects.ProcessorNotificationData;
import lithium.service.client.objects.placeholders.Placeholder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.objects.SummaryAccountTransactionType;
import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.cashier.machine.DoMachineContext;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.mail.client.stream.MailStream;

import java.util.Set;

@Service
public class CashierMailService extends CashierCommunicationService {
	@Autowired MailStream mailStream;
	
	public void sendDepositOrWithdrawalOrReversalMail(String status, String[] recipientTypes, DoMachineContext context) throws Exception {
		if (isStatusValid(status.toLowerCase())) {
			for (String recipient: recipientTypes) {
				String to = "";
				if (recipient.equalsIgnoreCase(RECIPIENT_INTERNAL)) {
					to = context.getExternalDomain().getSupportEmail();
				} else if (recipient.equalsIgnoreCase(RECIPIENT_PLAYER)) {
					to = context.getExternalUser().getEmail();
				}
				if (to != null && !to.isEmpty()) {
					String templateName = cashierTransactionStatusNotification(context.getTransaction().getTransactionType().name(), status, recipient);
					mailStream.process(
							EmailData.builder()
									.authorSystem()
									.domainName(context.getUser().domainName())
									.emailTemplateName(templateName)
									.emailTemplateLang(ISO_LANG_CODE_ENG)
									.to(to)
									.priority(1)
									.userGuid((recipient.equalsIgnoreCase(RECIPIENT_PLAYER)) ? context.getUser().getGuid(): null)
									.placeholders(constructPlaceholders(context))
									.build()
					);
				}
			}
		}
	}

	public void sendDepositOrWithdrawalMail(String status, String[] recipientTypes, DoMachineContext context) throws Exception {
		DoMachineState[] states = DoMachineState.values();
		boolean validState = false;
		for (int i = 0; i < states.length && validState == false; i++) {
			if (states[i].name().toLowerCase().equals(status.toLowerCase())) {
				validState = true;
			}
		}
		if (validState) {
			for (String recipient: recipientTypes) {
				String to = "";
				if (recipient.equalsIgnoreCase(RECIPIENT_INTERNAL)) {
					to = context.getExternalDomain().getSupportEmail();
				} else if (recipient.equalsIgnoreCase(RECIPIENT_PLAYER)) {
					to = context.getExternalUser().getEmail();
				}
				if (to != null && !to.isEmpty()) {
					String templateName = cashierTransactionStatusNotification(context.getTransaction().getTransactionType().name(), status, recipient);
					mailStream.process(
							EmailData.builder()
									.authorSystem()
									.domainName(context.getUser().domainName())
									.emailTemplateName(templateName)
									.emailTemplateLang(ISO_LANG_CODE_ENG)
									.to(to)
									.priority(1)
									.userGuid((recipient.equalsIgnoreCase(RECIPIENT_PLAYER)) ? context.getUser().getGuid() : null)
									.placeholders(constructPlaceholders(context))
									.build()
					);
				}
			}
		}
	}
	
	@Async
	public void sendNthDepositMail(DoMachineContext context) throws Exception {
		Response<SummaryAccountTransactionType> response =
			cashierService.accountingSummaryTransactionType(
				CashierTranType.DEPOSIT,
				context.getUser().domainName(),
				context.getUser().getGuid(),
				Period.GRANULARITY_TOTAL,
				context.getExternalDomain().getCurrency()
			);
		if (response.isSuccessful()) {
			mailStream.process(
					EmailData.builder()
							.authorSystem()
							.domainName(context.getUser().domainName())
							.emailTemplateName("player.deposit." + response.getData().getTranCount())
							.emailTemplateLang(ISO_LANG_CODE_ENG)
							.to(context.getExternalUser().getEmail())
							.priority(1)
							.userGuid(context.getUser().getGuid())
							.placeholders(constructPlaceholders(context))
							.build()
			);
		}
	}

	public void sendProcessorNotificationMail(ProcessorNotificationData notificationData, String[] recipientTypes, DoMachineContext context) throws Exception {
		for (String recipient: recipientTypes) {
			String to = "";
			if (recipient.equalsIgnoreCase(RECIPIENT_INTERNAL)) {
				to = context.getExternalDomain().getSupportEmail();
			} else if (recipient.equalsIgnoreCase(RECIPIENT_PLAYER)) {
				to = context.getExternalUser().getEmail();
			} else if (recipient.equalsIgnoreCase(RECIPIENT_EXTERNAL)) {
				to = notificationData.getTo();
			}
			if (to != null && !to.isEmpty()) {
				EmailData data = EmailData.builder()
						.authorSystem()
						.domainName(context.getUser().domainName())
						.emailTemplateName(notificationData.getTemplateName())
						.emailTemplateLang(ISO_LANG_CODE_ENG)
						.to(to)
						.priority(1)
						.userGuid(context.getExternalUser() != null ? context.getExternalUser().getGuid() : null)
						.placeholders(constructPlaceholders(context))
						.build();
				Set<Placeholder> placeHolders = constructPlaceholders(context);
				if (notificationData.getPlaceholders() != null) {
					placeHolders.addAll(notificationData.getPlaceholders());
				}
				data.setPlaceholders(placeHolders);
				mailStream.process(data);
			}
		}
	}

	public void sendAdjectiveSuccessfulDepositEmail(String status, DoMachineContext context) {
		StringBuilder templateStringBuilder = new StringBuilder();

		templateStringBuilder.append("email")
				.append(".")
				.append(context.getTransaction().getTransactionType().name().toLowerCase())
				.append(".")
				.append(status.toLowerCase())
				.append(".")
				.append(RECIPIENT_PLAYER)
				.append(".")
				.append(context.getProcessor().getProcessor().getCode().toLowerCase()); //[transactionType].[status].[recipient] ?

		mailStream.process(
				EmailData.builder()
						.authorSystem()
						.domainName(context.getUser().domainName())
						.emailTemplateName(templateStringBuilder.toString())
						.emailTemplateLang(ISO_LANG_CODE_ENG)
						.to(context.getExternalUser().getEmail())
						.priority(1)
						.userGuid(context.getUser().getGuid())
						.placeholders(constructPlaceholders(context))
						.build()
		);
	}
}

