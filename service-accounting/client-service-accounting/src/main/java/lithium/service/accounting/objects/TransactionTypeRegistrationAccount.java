package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class TransactionTypeRegistrationAccount {
    private String accountTypeCode;
    @Builder.Default
    private boolean debit = false;
    @Builder.Default
    private boolean credit = false;
    @Builder.Default
    private int dividerToCents = 1;
}
