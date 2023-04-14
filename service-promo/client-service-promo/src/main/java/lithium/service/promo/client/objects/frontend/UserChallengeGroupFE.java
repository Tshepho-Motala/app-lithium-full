package lithium.service.promo.client.objects.frontend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChallengeGroupFE {
    private String completed;
    private double percentage;
    private List<UserChallengeFE> userChallenges;
}
