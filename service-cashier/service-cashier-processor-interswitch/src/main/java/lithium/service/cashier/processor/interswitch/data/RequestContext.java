package lithium.service.cashier.processor.interswitch.data;

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
    private String userId;
    private String timestamp;
    private String signature;
    private String userGuid;
    private String domainName;
    private DomainMethodProcessor propertiesDmp;
    private Long balanceInCents;
    private String firstName;
}
