package lithium.service.cashier.processor.opay.api.v2.schema.validate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OpayUserRequest {
    private String phoneNumber;
}
