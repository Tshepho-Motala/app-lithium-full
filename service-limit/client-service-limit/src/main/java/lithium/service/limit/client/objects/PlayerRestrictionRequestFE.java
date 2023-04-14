package lithium.service.limit.client.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerRestrictionRequestFE {
    private String comment;
    private Integer subType;
}
