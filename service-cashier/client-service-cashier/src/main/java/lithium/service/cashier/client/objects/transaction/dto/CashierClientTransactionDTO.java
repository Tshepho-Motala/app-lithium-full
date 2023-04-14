package lithium.service.cashier.client.objects.transaction.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.objects.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CashierClientTransactionDTO {


    private Long id;

    private Long amountCents;

    private TransactionType transactionType;

    private Date createdOn;

    private TransactionWorkflowHistoryDTO current;
    private TransactionPaymentTypeDTO transactionPaymentType;
    private ProcessorUserCardDTO paymentMethod;
    private TransactionStatusDTO transactionStatus;

    private User user;

    private DomainMethod domainMethod;

    private String processorName;
    private String domainMethodName;
    private String guid;
    private String descriptor;
    private String statusCode;
    private String declineReason;
    private String processorReference;
    private String additionalReference;
    private boolean testAccount;
    private boolean autoApproved;
    private String reviewedByFullName;
    private String accountInfo;
    private String bonusCode;
    private Long bonusId;
    private String runtime;
    private String currencyCode;

}
