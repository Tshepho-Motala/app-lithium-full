package lithium.service.limit.client.objects;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = -4883983506372754994L;
    private Long id;
    private int version;
    private String guid;
    private boolean isTestAccount;
    private LossLimitsVisibility lossLimitsVisibility;
}
