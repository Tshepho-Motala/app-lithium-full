package lithium.service.cashier.processor.opay.context;

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
    private String msisdn;
    private String timestamp;
    private String signature;
    private String userGuid;
    private DomainMethodProcessor propertiesDmp;
    private Long balanceInCents;
    private String firstName;
    private String formattedPhoneNumber;
}
