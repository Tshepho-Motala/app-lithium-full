package lithium.service.limit.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRestriction {
    private Long userId;
    private String userName;
    private Boolean login;
    private Boolean deposit;
    private Boolean withdraw;
}
