package lithium.service.casino.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FreeGamePayload {

    private Boolean freeGame;

    private String providerGuid;

    private String userGuid;
}
