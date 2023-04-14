package lithium.service.access.provider.transunion.shema.response.fault;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Envelope",
        namespace = "http://www.w3.org/2003/05/soap-envelope")
public class MainTUResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "Body",
            namespace = "http://www.w3.org/2003/05/soap-envelope")
    private EnvelopeBody envelopeBody;

    public MainTUResponse() {
    }

    public EnvelopeBody getEnvelopeBody() {
        return envelopeBody;
    }
}
