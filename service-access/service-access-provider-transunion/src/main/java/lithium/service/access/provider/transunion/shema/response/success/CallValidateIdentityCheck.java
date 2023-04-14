package lithium.service.access.provider.transunion.shema.response.success;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class CallValidateIdentityCheck implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "appverified",
            namespace = "urn:callcredit.co.uk/callvalidate5")
    private String appVerified;

    @XmlElement(name = "namematched",
            namespace = "urn:callcredit.co.uk/callvalidate5")
    private String nameMatched;

    @XmlElement(name = "currentaddressmatched",
            namespace = "urn:callcredit.co.uk/callvalidate5")
    private String currentAddressMatched;

}
