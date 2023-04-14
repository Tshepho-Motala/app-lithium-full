package lithium.service.casino.client.objects.progressivejackpotfeed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressiveJackpotFeedResponse {
    private List<ProgressiveJackpotGameBalance> progressiveJackpotGameBalances;
}
