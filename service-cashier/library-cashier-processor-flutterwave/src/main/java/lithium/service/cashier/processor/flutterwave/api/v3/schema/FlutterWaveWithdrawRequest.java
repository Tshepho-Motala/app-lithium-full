package lithium.service.cashier.processor.flutterwave.api.v3.schema;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
@Builder
public class FlutterWaveWithdrawRequest {
    String account_bank;
    String account_number;
    BigDecimal amount;
    String currency;
    String reference;
}
