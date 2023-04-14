package lithium.service.reward.provider.casino.blueprint.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "request")
@JsonPropertyOrder({"apiToken", "games", "playerId", "brandId",
        "isTestPlayer", "currencyCode", "countryCode", "jurisdiction","promotionId","numberOfFreeSpins","coinValue"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlueprintRewardRequest implements Serializable {
    private static final long serialVersionUID = 8000954571692889022L;
    private String apiToken;
    private String playerId;
    private String brandId;

    @JacksonXmlProperty(localName = "isTestPlayer")
    private boolean isTestPlayer;

    private String countryCode;
    private String currencyCode;
    private String jurisdiction;
    private String promotionId;
    private int numberOfFreeSpins;
    private long coinValue;

    @Builder.Default
    @JacksonXmlElementWrapper(localName = "games")
    @JacksonXmlProperty(localName = "gameId")
    private List<String> games = new ArrayList<>();
}
