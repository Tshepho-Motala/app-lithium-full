package lithium.service.limit.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserRestrictionsRequest {
    private String userGuid;
    private  long userId;
    private List<Long> domainRestrictionSets = new ArrayList<>();
    private String comment;
    private Integer subType;

}
