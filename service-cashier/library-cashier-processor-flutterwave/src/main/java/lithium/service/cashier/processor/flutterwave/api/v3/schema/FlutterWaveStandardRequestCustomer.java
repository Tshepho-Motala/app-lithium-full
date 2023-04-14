package lithium.service.cashier.processor.flutterwave.api.v3.schema;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class FlutterWaveStandardRequestCustomer {
    String email;
    String phonenumber;
    String name;
}
