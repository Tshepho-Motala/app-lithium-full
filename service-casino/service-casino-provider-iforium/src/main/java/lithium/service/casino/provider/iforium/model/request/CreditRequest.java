package lithium.service.casino.provider.iforium.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreditRequest extends Request {

    @JsonProperty("OperatorAccountID")
    @NotEmpty
    @Size(max=50)
    private String operatorAccountId;

    @JsonProperty("AccountTransactionID")
    @NotEmpty
    @Size(max=50)
    private String accountTransactionId;

    @JsonProperty("AccountTransactionTypeID")
    @NotEmpty
    @Size(max=50)
    private String accountTransactionTypeId;

    @JsonProperty("CurrencyCode")
    @NotEmpty
    @Size(max = 3)
    private String currencyCode;

    @JsonProperty("Amount")
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    @DecimalMin(value = "0")
    private BigDecimal amount;

    public CreditRequest(String platformKey, String sequence, Date timestamp, String operatorAccountId,
                         String accountTransactionId, String accountTransactionTypeId, String currencyCode, BigDecimal amount) {
        super(platformKey, sequence, timestamp);
        this.operatorAccountId = operatorAccountId;
        this.accountTransactionId = accountTransactionId;
        this.accountTransactionTypeId = accountTransactionTypeId;
        this.currencyCode = currencyCode;
        this.amount = amount;
    }
}
