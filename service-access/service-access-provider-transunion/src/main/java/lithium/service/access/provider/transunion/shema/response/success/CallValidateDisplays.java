package lithium.service.access.provider.transunion.shema.response.success;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@Data
@XmlAccessorType(XmlAccessType.FIELD)

public class CallValidateDisplays implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "IdentityCheck",
            namespace = "urn:callcredit.co.uk/callvalidate5")
    private CallValidateIdentityCheck callValidateIdentityCheck;
    @XmlElement(name = "OtherChecks",
            namespace = "urn:callcredit.co.uk/callvalidate5")
    private CallValidateOtherChecks callValidateOtherChecks;

    @XmlElement(name = "AgeVerify",
            namespace = "urn:callcredit.co.uk/callvalidate5")
    private AgeVerify ageVerify;

}
