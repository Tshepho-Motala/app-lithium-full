package lithium.service.games.provider.google.rge.client.objects.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Prediction {
    @JsonProperty("user_guid")
    private String userGuid;

    private List<Recommendation> recommendations;
}
