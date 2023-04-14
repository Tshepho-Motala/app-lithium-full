package lithium.service.limit.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimitSystemAccessUpdate {
    private String domainName;
    private Long verificationId;
    private boolean login;
    private boolean deposit;
    private boolean withdraw;
    private boolean betPlacement;
    private boolean casino;
    private String comment;
}
