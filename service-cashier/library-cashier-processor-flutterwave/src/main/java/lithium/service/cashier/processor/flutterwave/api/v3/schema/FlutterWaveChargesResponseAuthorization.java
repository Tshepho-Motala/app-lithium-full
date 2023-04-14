package lithium.service.cashier.processor.flutterwave.api.v3.schema;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class FlutterWaveChargesResponseAuthorization {
    String mode;
    String note;
}
