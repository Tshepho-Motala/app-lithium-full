package lithium.service.libraryvbmigration.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HistoricCashierTransaction {
    private long customerId;
    private String lithiumUserGuid;
    private long transactionId;
    private String type;
    private String status;
    private Date createdDate;
    private Date updatedDate;
    private String currencyCode;
    private double amount;
    private String operationTypeDescription;
    private String paymentMethodType;
    private String paymentMethod;
    private String paymentProvider;
    private String operationGroupDescription;
    private String operationDescription;
    private String operationCategory;
}
