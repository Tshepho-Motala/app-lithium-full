package lithium.service.user.client.objects;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class EcosystemUserProfiles {
    public List<EcosystemUserProfile> ecosystemUserProfileList;
}
