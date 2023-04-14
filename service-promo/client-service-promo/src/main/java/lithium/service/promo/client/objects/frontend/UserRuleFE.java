package lithium.service.promo.client.objects.frontend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRuleFE {
    private String started;
    private boolean complete;
    private String completed;
    private double percentage;
    private RuleFE rule;
}
