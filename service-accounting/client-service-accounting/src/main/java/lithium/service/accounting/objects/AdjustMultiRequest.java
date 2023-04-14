package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdjustMultiRequest {
    private Long amountCents;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private DateTime date;
    private String accountCode;
    private String accountTypeCode;
    private String transactionTypeCode;
    private String contraAccountCode;
    private String contraAccountTypeCode;
    private String[] labels;
    private String currencyCode;
    private String domainName;
    private String ownerGuid;
    private String authorGuid;
    private Boolean allowNegativeAdjust;
    private String[] negAdjProbeAccCodes;

    public boolean isAllowNegativeAdjust() {
        return Optional.ofNullable(allowNegativeAdjust).orElse(true);
    }
}
