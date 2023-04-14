package lithium.service.reward.provider.casino.blueprint.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "request")
@JsonPropertyOrder({"apiToken", "promotionId"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlueprintRewardCancelRequest {
    private String apiToken;
    private String promotionId;
}
