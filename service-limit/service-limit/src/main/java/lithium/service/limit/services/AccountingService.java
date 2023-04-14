package lithium.service.limit.services;

import lithium.service.Response;
import lithium.service.accounting.client.AccountingPeriodClient;
import lithium.service.accounting.client.AccountingSummaryAccountClient;
import lithium.service.accounting.client.AccountingSummaryTransactionTypeClient;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.objects.SummaryAccount;
import lithium.service.accounting.objects.SummaryAccountTransactionType;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.user.client.objects.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountingService {
	@Autowired private LithiumServiceClientFactory services;

	public static final String ACCOUNT_CODE_PLAYER_BALANCE = "PLAYER_BALANCE";
	public static final String ACCOUNT_CODE_PLAYER_BALANCE_PENDING_WITHDRAWAL = "PLAYER_BALANCE_PENDING_WITHDRAWAL";

	public static final String TRAN_TYPE_DEPOSIT = "CASHIER_DEPOSIT";
	public static final String TRAN_TYPE_WITHDRAWAL = "CASHIER_PAYOUT";

	public long getTransactionTypeAmountCents(User user, String currencyCode, int granularity, int offset,
											  String accountCode, String tranType) throws Exception {
		Long amountCents = 0L;
		Long accountId = null;

		AccountingPeriodClient accountingPeriodClient = services.target(AccountingPeriodClient.class);
		AccountingSummaryAccountClient accountingSummaryAccountClient =
			services.target(AccountingSummaryAccountClient.class);
		AccountingSummaryTransactionTypeClient accountingSummaryTranTypeClient =
			services.target(AccountingSummaryTransactionTypeClient.class);

		Period period = accountingPeriodClient.findByOffset(user.getDomain().getName(), granularity, offset).getData();

		Response<SummaryAccount> summaryAccountResponse = accountingSummaryAccountClient.find(period.getId(),
			accountCode, "PLAYER_BALANCE", currencyCode, user.guid());
		SummaryAccount summary = summaryAccountResponse.getData();
		if (summary != null) {
			accountId = summary.getAccount().getId();
		}

		if (accountId != null) {
			Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(accountId,
				period.getId(), tranType);
			SummaryAccountTransactionType d = r.getData();

			if (tranType.contentEquals(TRAN_TYPE_DEPOSIT)) {
				amountCents = (d != null)? d.getCreditCents() - d.getDebitCents(): 0;
			} else if (tranType.contentEquals(TRAN_TYPE_WITHDRAWAL)) {
				amountCents = (d != null)? d.getDebitCents() - d.getCreditCents(): 0;
			}
		}

		return amountCents;
	}
}
