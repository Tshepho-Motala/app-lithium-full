package lithium.service.accounting.objects.reconciliation;

import lithium.service.accounting.objects.SummaryDomain;
import lithium.service.accounting.objects.SummaryDomainLabelValue;
import lithium.service.accounting.objects.SummaryDomainTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SummaryReconciliationResponse {
	private boolean safeToProcessThisDate;
	private List<SummaryDomain> summaryDomainList;
	private List<SummaryDomainLabelValue> summaryDomainLabelValueList;
	private List<SummaryDomainTransactionType> summaryDomainTransactionTypeList;
}
