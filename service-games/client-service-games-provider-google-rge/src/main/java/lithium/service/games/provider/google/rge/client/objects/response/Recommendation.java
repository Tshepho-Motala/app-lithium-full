package lithium.service.games.provider.google.rge.client.objects.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recommendation {
    private String gameGUID;
    private String gameProviderName;
    private Integer gameRank;
}
