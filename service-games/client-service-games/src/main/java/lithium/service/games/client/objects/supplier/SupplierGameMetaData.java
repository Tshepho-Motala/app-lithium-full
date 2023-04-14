package lithium.service.games.client.objects.supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.games.client.objects.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupplierGameMetaData {
    private Long id;

    private String supplierGameGuid;

    private SupplierGameMetaVertical gameVertical;

    private String gameType;

    private String gameSubType;

    private String name;

    private Set<SupplierGameMetaDescription> descriptions;

    private SupplierGameMetaDisplay display;

    private Boolean open;

    private List<SupplierGameMetaBetLimit> betLimits;

    private SupplierGameMetaDealer dealer;

    private Integer players;

    private SupplierGameMetaHours operationHours;

    private List<SupplierGameMetaLinks> links;

    private Integer seats;

    private Boolean betBehind;

    private String seatsTaken;

    private String dealerHand;

    private List<SupplierGameMetaResults> results;

    private String history;

    @JsonProperty("gameID")
    private String gameGuid;

    private Game game;

    @JsonIgnore
    public List<SupplierGameMetaLinks> getLinks() {
        return links;
    }

    @JsonIgnore
    public Set<SupplierGameMetaDescription> getDescriptions() {
        return descriptions;
    }
}
