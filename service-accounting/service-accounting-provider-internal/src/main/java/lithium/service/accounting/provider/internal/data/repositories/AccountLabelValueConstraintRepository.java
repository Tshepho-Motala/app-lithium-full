package lithium.service.accounting.provider.internal.data.repositories;

import lithium.service.accounting.provider.internal.data.LockingPagingSortingRepository;
import lithium.service.accounting.provider.internal.data.entities.AccountLabelValueConstraint;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry;
import java.util.List;

public interface AccountLabelValueConstraintRepository extends LockingPagingSortingRepository<AccountLabelValueConstraint, Long> {
	
	AccountLabelValueConstraint findOneByAccountIdAndLabelValueId(Long accountId, Long labelValueId);

	AccountLabelValueConstraint
		findByAccountAccountCodeCodeAndAccountAccountTypeCodeAndAccountOwnerGuidAndAccountCurrencyCodeAndAccountDomainNameAndLabelValueLabelNameAndLabelValueValue(
			String accountCode,
			String accountTypeCode,
			String userGuid,
			String currencyCode,
			String domainName,
			String labelName,
			String labelValue
		);

    Long deleteByTransactionEntryTransactionIdIn(List<Long> transactionIdsList);
}
