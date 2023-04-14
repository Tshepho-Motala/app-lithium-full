package lithium.service.reward.provider.casino.blueprint.dto;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@JacksonXmlRootElement(localName = "response")
@Getter
@Setter
@Builder
public class BlueprintRewardResponse {
    private boolean success;
    private String promotionId;
    private String error;
}
