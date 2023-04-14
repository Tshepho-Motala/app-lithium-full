package lithium.service.cashier.processor.interswitch.api.schema;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class InterswitchWebDepositCallbackRequest {
    private String amount;
    private String apprAmt;
    private String cardNum;
    private String desc;
    private String mac;
    private String payRef;
    private String resp;
    private String retRef;
    private String txnref;

}
