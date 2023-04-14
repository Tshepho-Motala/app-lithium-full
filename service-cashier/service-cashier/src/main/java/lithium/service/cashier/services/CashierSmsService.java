package lithium.service.cashier.services;

import lithium.service.Response;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.objects.SummaryAccountTransactionType;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.cashier.machine.DoMachineContext;
import lithium.service.sms.client.objects.SMSBasic;
import lithium.service.sms.client.stream.SMSStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CashierSmsService extends CashierCommunicationService {
	@Autowired
	SMSStream smsStream;

	public void sendDepositOrWithdrawalOrReversalSms(String status, String[] recipientTypes, DoMachineContext context) throws Exception {
		if (isStatusValid(status.toLowerCase())) {
			for (String recipient: recipientTypes) {
				String to = "";
				if (recipient.equalsIgnoreCase(RECIPIENT_INTERNAL)) {
					// TODO: Add an option to read from support sms if that becomes a thing one day.
					to = null;
				} else if (recipient.equalsIgnoreCase(RECIPIENT_PLAYER)) {
					to = context.getExternalUser().getCellphoneNumber();
				}
				if (to != null && !to.isEmpty()) {
					String templateName = cashierTransactionStatusNotification(context.getTransaction().getTransactionType().name(), status, recipient);
					smsStream.process(
							SMSBasic.builder()
									.domainName(context.getUser().domainName())
									.smsTemplateName(templateName)
									.smsTemplateLang(ISO_LANG_CODE_ENG)
									.to(to)
									.priority(1)
									.userGuid((recipient.equalsIgnoreCase(RECIPIENT_PLAYER))? context.getUser().getGuid(): null)
									.placeholders(constructPlaceholders(context))
									.build()
					);
				}
			}
		}
	}
	
	@Async
	public void sendNthDepositSms(DoMachineContext context) throws Exception {
		String to = context.getExternalUser().getCellphoneNumber();
		if (to != null) {
			Response<SummaryAccountTransactionType> response =
				cashierService.accountingSummaryTransactionType(
					CashierTranType.DEPOSIT,
					context.getUser().domainName(),
					context.getUser().getGuid(),
					Period.GRANULARITY_TOTAL,
					context.getExternalDomain().getCurrency()
				);
			if (response.isSuccessful()) {
				smsStream.process(
						SMSBasic.builder()
							.domainName(context.getUser().domainName())
							.smsTemplateName(("sms.player.deposit." + response.getData().getTranCount()))
							.smsTemplateLang(ISO_LANG_CODE_ENG)
							.to(to)
							.priority(1)
							.userGuid(context.getUser().getGuid())
							.placeholders(constructPlaceholders(context))
							.build()
				);
		}
		}
	}

    @Async
    public void sendAdjectiveSuccessfulDepositSms(String status, DoMachineContext context) {
        String to = context.getExternalUser().getCellphoneNumber();
        StringBuilder templateStringBuilder = new StringBuilder();

        templateStringBuilder.append("sms")
                .append(".")
                .append(context.getTransaction().getTransactionType().name().toLowerCase())
                .append(".")
                .append(status.toLowerCase())
                .append(".")
                .append(RECIPIENT_PLAYER)
                .append(".")
                .append(context.getProcessor().getProcessor().getCode().toLowerCase()); //[transactionType].[status].[recipient]

        smsStream.process(
                SMSBasic.builder()
                        .domainName(context.getUser().domainName())
                        .smsTemplateName(templateStringBuilder.toString())
                        .smsTemplateLang(ISO_LANG_CODE_ENG)
                        .to(to)
                        .priority(1)
                        .userGuid(context.getUser().getGuid())
                        .placeholders(constructPlaceholders(context))
                        .build()
        );
    }
}
