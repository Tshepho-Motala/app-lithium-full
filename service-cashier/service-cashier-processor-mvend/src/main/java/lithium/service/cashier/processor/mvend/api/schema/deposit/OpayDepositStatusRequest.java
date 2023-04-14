package lithium.service.cashier.processor.mvend.api.schema.deposit;

import lombok.Data;

@Data
public class OpayDepositStatusRequest {

    private String network_ref;
    private String date;
    private String signature;

}
