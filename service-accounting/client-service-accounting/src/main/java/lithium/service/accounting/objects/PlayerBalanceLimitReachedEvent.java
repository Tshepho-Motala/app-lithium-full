package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class PlayerBalanceLimitReachedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String domainName;
    private Long amountCents;
    private String ownerGuid;
    private String authorGuid;
    private String comment;
    private boolean balanceLimitEscrow;
}
