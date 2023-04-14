package lithium.service.cashier.processor.interswitch.api.schema;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class InterswitchWithdrawRequest {
    private String mac;
    private String transferCode;
    private Beneficiary beneficiary;
    private String initiatingEntityCode;
    private Initiation initiation;
    private Sender sender;
    private Termination termination;
}
