package lithium.service.user.mass.action.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestrictionData {
    private Set<Long> restrictions;
    private String reason;
    private Integer subType;
}
