package lithium.service.cashier.processor.flutterwave.api.v3.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class FlutterWaveWithdrawData {
    Integer id;
    String account_number;
    String bank_code;
    String fullname;
    String created_at;
    String currency;
    String debit_currency;
    Integer amount;
    BigDecimal fee;
    String status;
    String reference;
    String meta;
    String narration;
    String complete_message;
    Integer  requires_approval;
    Integer is_approved;
    String bank_name;
}
