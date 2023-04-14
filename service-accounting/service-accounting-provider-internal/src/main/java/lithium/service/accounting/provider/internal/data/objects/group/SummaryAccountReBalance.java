package lithium.service.accounting.provider.internal.data.objects.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SummaryAccountReBalance {
    private long accountId;
    private String userGuid;
    private String domainName;
    private String accountCode;
    private String currencyCode;
    private long mismatchedTransactions;
}
