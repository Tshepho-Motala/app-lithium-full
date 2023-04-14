package lithium.service.access.provider.transunion.shema.response.success;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class CallValidate5Response implements Serializable {
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Response",
            namespace = "http://www.callcredit.co.uk/SingleAccessPointService/ISingleAccessPointService/1.0")
    private CallValidateInnerResponse response;
}
