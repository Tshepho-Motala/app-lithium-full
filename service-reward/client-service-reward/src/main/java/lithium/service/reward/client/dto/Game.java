package lithium.service.reward.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Game {
    private String name;
    private String commercialName;
    private String providerGameId;
    private String guid;
    private String description;
}
