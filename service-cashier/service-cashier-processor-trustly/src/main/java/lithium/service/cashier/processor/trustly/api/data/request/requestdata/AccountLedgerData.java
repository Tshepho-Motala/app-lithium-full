package lithium.service.cashier.processor.trustly.api.data.request.requestdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.trustly.api.data.Currency;
import lithium.service.cashier.processor.trustly.api.data.request.RequestData;
import lombok.Data;

@Data
public class AccountLedgerData extends RequestData {
    @JsonProperty("FromDate")
    private String fromDate;
    @JsonProperty("ToDate")
    private String toDate;
    @JsonProperty("Currency")
    private Currency currency;
}
