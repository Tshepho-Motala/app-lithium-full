package lithium.service.cashier.processor.trustly.api.data.request.requestdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.trustly.api.data.request.RequestData;
import lombok.Data;

@Data
public class DenyWithdrawalData extends RequestData {
    @JsonProperty("OrderID")
    private String orderID;
}
