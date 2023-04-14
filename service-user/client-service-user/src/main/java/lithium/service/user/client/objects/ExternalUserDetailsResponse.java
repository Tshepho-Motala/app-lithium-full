package lithium.service.user.client.objects;

import java.util.Map;
import lithium.service.user.client.objects.PlayerBasic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalUserDetailsResponse {
    private Integer stage;
    private String domainName;
    private Long status;
    private PlayerBasic playerBasic;
    private Map<String, Object> data;
}
