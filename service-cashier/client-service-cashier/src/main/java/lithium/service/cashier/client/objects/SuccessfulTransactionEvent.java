package lithium.service.cashier.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessfulTransactionEvent {
    private String transactionType;
    private String userGuid;
    private Long amount;
    private Long transactionId;
    private Date createdDate;
    private Date updatedDate;
    private Boolean isFirstDeposit;
}
