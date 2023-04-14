package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountMigrationHistoricDetails {
    private String userGuid;
    private LocalDate createdOn;
    private Long entryAmountCents;
    private String transactionTypeCode;
    private String customerId;
}