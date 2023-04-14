package lithium.service.cashier.processor.smartcash.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payer {
    @JsonProperty("account_id")
    private String accountId;
    @JsonProperty("branch_code")
    private String bankCode;
    @NotEmpty
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("pay_code")
    private String payCode;
    @NotEmpty
    @JsonProperty("transfer_type")
    private String transferType;
    @NotEmpty
    @JsonProperty("wallet_id")
    private String walletId;
}
