package lithium.service.cashier.processor.opay.api.v2.schema;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BaseResponse {
    private String status;
    private String message;
}
