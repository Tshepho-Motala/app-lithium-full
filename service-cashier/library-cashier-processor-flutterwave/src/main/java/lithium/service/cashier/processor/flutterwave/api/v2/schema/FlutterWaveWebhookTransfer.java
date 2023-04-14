package lithium.service.cashier.processor.flutterwave.api.v2.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlutterWaveWebhookTransfer {
    Integer id;
    String account_number;
    String bank_code;
    String fullname;
    String date_created;
    String currency;
    String debit_currency;
    Integer amount;
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
