package lithium.service.casino.provider.roxor.api.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private String playerId;
    private String website;
    private String screenName;
}
