package lithium.service.user.mass.action.api.backoffice.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FileDataSummaryResponse {

    private long totalPlayers;
    private long existingPlayers;
    private long undefinedPlayers;
    private long duplicatePlayers;
    private long existingNotFailedPlayers;
}