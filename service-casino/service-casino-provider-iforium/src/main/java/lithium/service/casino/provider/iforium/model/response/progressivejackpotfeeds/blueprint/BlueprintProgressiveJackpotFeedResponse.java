package lithium.service.casino.provider.iforium.model.response.progressivejackpotfeeds.blueprint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name="jackpots")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.NONE)
@ToString
public class BlueprintProgressiveJackpotFeedResponse {

    @XmlAttribute
    private String version = "1.0";

    @XmlAttribute(name = "currency")
    private String currency;

    @XmlElement(name="game")
    private List<Game> game;

}
