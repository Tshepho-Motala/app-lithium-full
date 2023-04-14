package lithium.service.cashier.processor.flutterwave.api.v3.schema;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class FlutterWaveStandardRequest {
    String tx_ref;
    String amount;
    String currency;
    String payment_options;
    String redirect_url;
    FlutterWaveStandardRequestCustomer customer;
    FlutterWaveStandardRequestCustomizations customizations;
}
