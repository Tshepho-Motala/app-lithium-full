package lithium.service.cashier.processor.interswitch.data;

import lithium.service.cashier.client.objects.DepositStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DepositRequestContext extends RequestContext {
    private DepositRequest request;
    private Long amountInCents;
    private Long cashierReferenceNumber;
    private String additionalReferenceNumber;
    private DepositStatus depositStatus;
    private String notificationMessage;
    private String merchantReference;
    private String methodCode;
    private String channelName;
}
