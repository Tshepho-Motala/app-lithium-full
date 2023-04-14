package lithium.service.access.provider.transunion.shema.response.fault;



import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "Fault")
public class FaultResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Reason",
            namespace = "http://www.w3.org/2003/05/soap-envelope")
    private FaultReason faultReason;

    public FaultResponse() {
    }

    public FaultReason getFaultReason() {
        return faultReason;
    }
}
