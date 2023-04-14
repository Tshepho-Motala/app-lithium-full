package lithium.service.access.provider.transunion.shema.response.success;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class CallValidateInnerResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Result",
            namespace = "urn:callcredit.co.uk/callvalidate5")
    private CallValidateInnerResult result;

}
