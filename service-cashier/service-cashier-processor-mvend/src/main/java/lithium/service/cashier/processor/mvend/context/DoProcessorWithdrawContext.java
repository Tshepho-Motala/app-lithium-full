package lithium.service.cashier.processor.mvend.context;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.mvend.api.schema.withdraw.WithdrawConfirmRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.web.client.RestTemplate;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DoProcessorWithdrawContext extends RequestContext {
    DoProcessorRequest request;
    DoProcessorResponse response;
}
