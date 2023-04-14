package lithium.service.cashier.processor.mvend.context;

import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public abstract class RequestContext {
    private Long sessionId;
    private String groupRef;
    private String username;
    private String password;
    private String msisdn;
    private String timestamp;
    private String hash;
    private String userGuid;
    private DomainMethodProcessor propertiesDmp;
    private DomainMethodProcessor processingDmp;
    private Long balanceInCents;
    private String firstName;
}
