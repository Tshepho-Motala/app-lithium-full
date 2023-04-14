package lithium.service.limit.client.objects;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerLimitPreferenceMigrationDetails {

    private String playerGuid;
    private String customerID;
    private String domainName;
    private int limitType;
    private int granularity;
    private long amountCents;


}
