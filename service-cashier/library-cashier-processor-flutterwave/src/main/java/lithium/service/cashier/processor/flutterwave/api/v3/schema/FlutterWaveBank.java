package lithium.service.cashier.processor.flutterwave.api.v3.schema;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FlutterWaveBank {
    int id;
    String code;
    String name;
}
