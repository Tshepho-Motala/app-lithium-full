package lithium.service.cashier.processor.trustly.api.data.response;

import lombok.Data;

@Data
public class Error {
    private String name;
    private int code;
    private String message;
    private ErrorBody error;
}
