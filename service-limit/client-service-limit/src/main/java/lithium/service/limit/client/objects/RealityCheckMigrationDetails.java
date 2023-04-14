package lithium.service.limit.client.objects;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RealityCheckMigrationDetails {
    private String playerGuid;
    private Long realityCheckInterval;
    private String customerID;

}
