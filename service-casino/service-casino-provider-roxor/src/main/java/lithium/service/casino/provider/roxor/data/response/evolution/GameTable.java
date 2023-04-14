package lithium.service.casino.provider.roxor.data.response.evolution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.games.client.objects.supplier.GameVerticalEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameTable {

    private String name;

    private GameVerticalEnum gameVertical;

    private GameProvider gameProvider;

    @JsonProperty("gameTypeUnified")
    private String gameType;

    private String gameSubType;

    private String language;

    private String display;

    private Boolean open;

    private Map<String, BetLimits> betLimits;

    private Dealer dealer;

    private Integer players;

    private OperationHours operationHours;

    private VideoSnapshot videoSnapshot;

    private Integer seats;

    private Boolean betBehind;

    private List<Integer> seatsTaken;

    private List<Integer> dealerHand;

    private Map<String, String> descriptions = new HashMap<>();

    // TODO LSPLAT-10163 commented this as the structure changed on the evolution API response. This is currently not being used but will need a rework when required.
    //private List<String> history;

    private List<Road> road;

}
