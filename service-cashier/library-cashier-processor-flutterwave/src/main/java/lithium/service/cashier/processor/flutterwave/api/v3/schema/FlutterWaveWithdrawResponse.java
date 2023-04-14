package lithium.service.cashier.processor.flutterwave.api.v3.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FlutterWaveWithdrawResponse {
    String status;
    String message;
    FlutterWaveWithdrawData data;
}

