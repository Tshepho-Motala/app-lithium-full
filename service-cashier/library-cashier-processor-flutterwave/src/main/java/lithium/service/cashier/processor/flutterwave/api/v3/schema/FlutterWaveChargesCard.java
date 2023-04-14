package lithium.service.cashier.processor.flutterwave.api.v3.schema;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class FlutterWaveChargesCard {
    Integer first_6digits;
    Integer last_4digits;
    String issuer;
    String country;
    String type;
    String expiry;
}
