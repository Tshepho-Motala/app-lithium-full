package lithium.service.casino.provider.iforium.model.response.progressivejackpotfeeds.blueprint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name="jackpot")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.NONE)
@ToString
public class Jackpot {

    @XmlAttribute
    private String id;

    @XmlAttribute(name = "current_amount")
    private String currentAmount;

    @XmlAttribute(name = "won_by_amount")
    private String wonByAmount;

    @XmlAttribute(name = "group_name")
    private String groupName;
}
