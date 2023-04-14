package lithium.service.cashier.processor.flutterwave.api.v3.schema;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class FlutterWaveChargesCustomer {
    String id;
    String phone_number;
    String name;
    String email;
    String created_at;
}
