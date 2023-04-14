package lithium.service.cashier.stream;

import lithium.math.CurrencyAmount;
import lithium.service.accounting.objects.PlayerBalanceLimitReachedEvent;
import lithium.service.accounting.stream.IPlayerBalanceLimitReachedProcessor;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.services.DirectWithdrawalService;
import lithium.service.cashier.services.ProcessorAccountService;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class DirectWithdrawQueueProcessor implements IPlayerBalanceLimitReachedProcessor {
	@Autowired
	private DirectWithdrawalService directWithdrawalService;
	@Autowired
	private ProcessorAccountService processorAccountService;

	@Override
	public void onPlayerBalanceLimitReached(PlayerBalanceLimitReachedEvent data) throws Exception {
		String domainName = data.getDomainName();
		CurrencyAmount amount = CurrencyAmount.fromCents(data.getAmountCents());
		String guid = data.getOwnerGuid();
		String comment = data.getComment();

		log.info("Got dw request: " + domainName + "|" + amount.toCents() + "|" + guid + "|" + comment);

		if (!directWithdrawalService.enoughFunds(domainName, amount, data.isBalanceLimitEscrow(), guid)) {
			log.error("Player (" + guid + ") does not have sufficient funds to initiate direct withdraw: " + data);
			return;
		}

		ProcessorAccount processorAccount = processorAccountService.getVerifiedContraProcessorAccount(guid);
		if (isNull(processorAccount)) {
			log.warn("Can't find contra processor account for " + guid + " while trying to initiate direct withdraw: " + data);
			return;
		}

		Map<String, String> fields = new HashMap<>();
		fields.put("processorAccountId", String.valueOf(processorAccount.getId()));

		DoResponse response = directWithdrawalService.getDirectWithdrawalResponse(domainName, processorAccount.getMethodCode(), amount.toAmount().toPlainString(),
				fields, 0L, guid, User.SYSTEM_GUID, data.isBalanceLimitEscrow(), "0.0.0.0", new HashMap<>(), null);
		log.info("response={}", response);

		directWithdrawalService.changeHistory(guid, amount.toAmount().toPlainString(), processorAccount.getMethodCode(), User.SYSTEM_GUID,  comment, null);
	}
}
