package lithium.service.access.provider.transunion.shema.response.fault;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
public class EnvelopeBody implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "Fault",
            namespace = "http://www.w3.org/2003/05/soap-envelope")
    private FaultResponse faultResponse;

    public EnvelopeBody() {
    }

    public FaultResponse getFaultResponse() {
        return faultResponse;
    }
}
