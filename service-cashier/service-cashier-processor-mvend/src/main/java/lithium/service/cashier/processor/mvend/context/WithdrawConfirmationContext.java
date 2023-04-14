package lithium.service.cashier.processor.mvend.context;

import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.mvend.api.schema.Response;
import lithium.service.cashier.processor.mvend.api.schema.withdraw.WithdrawConfirmRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WithdrawConfirmationContext extends RequestContext {
    private WithdrawConfirmRequest request;
    private Response response;
    private Long transactionId;
    private DoProcessorResponseStatus status;
}
