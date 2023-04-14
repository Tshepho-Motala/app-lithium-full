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
public class WalletTransactionsChanges {
    private String domain;
    private String eventSource;
    private String eventType;
    private String playerGuid;
    private String accountId;
    private Long value;
    private String status;
    private String statusDetail;
    private Long transactionId;
    private Date createdDate;
    private Date updatedDate;
    private String transactionRemark;
    private Boolean isFirstDeposit;
    private String declineReason;
}
