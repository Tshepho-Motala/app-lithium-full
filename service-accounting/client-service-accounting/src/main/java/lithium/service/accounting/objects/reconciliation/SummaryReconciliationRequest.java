package lithium.service.accounting.objects.reconciliation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SummaryReconciliationRequest {
    private String date;
    private String dateFormat;
    @Builder.Default
    private int summaryDomainPageNum = 0;
    @Builder.Default
    private int summaryDomainTransactionTypePageNum = 0;
    @Builder.Default
    private int summaryDomainLabelValuePageNum = 0;
    @Builder.Default
    private int dataFetchSizePerType = 100;
}
