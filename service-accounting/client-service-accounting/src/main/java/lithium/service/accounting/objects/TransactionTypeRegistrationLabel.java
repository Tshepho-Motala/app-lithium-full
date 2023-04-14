package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
public class TransactionTypeRegistrationLabel {
    private String label;

    @Builder.Default
    private boolean optional = true;
    @Builder.Default
    private boolean summarise = false; //This flag is for enabling summaries for all granularities.
    @Builder.Default
    private boolean summariseTotal = false; //This flag is for enabling summaries only on GRANULARITY_TOTAL. If set true, summarise above must be true, but will be superseded.
    @Builder.Default
    private boolean synchronous = false; //This is not currently used, but might be needed in future.
    @Builder.Default
    private boolean unique = false;

    private final String uniqueAccountTypeCode;
}
