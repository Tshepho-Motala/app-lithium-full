package lithium.service.cashier.processor.trustly.api.data.response;

import lombok.Data;

@Data
public class ErrorData {
    private String code;
    private String message;
}
