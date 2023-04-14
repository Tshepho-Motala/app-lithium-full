package lithium.service.cashier.processor.flutterwave.api.v3.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlutterWaveGetTransactionResponseData {
    Long id;
    String account_number;
    String bank_code;
    String full_name;
    String created_at;
    String currency;
    String debit_currency;
    BigDecimal amount;
    BigDecimal fee;
    String status;
    String reference;
    String meta;
    String narration;
    String approver;
    String complete_message;
    String requires_approval;
    String is_approved;
    String bank_name;
}
