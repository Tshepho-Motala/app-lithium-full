package lithium.service.access.provider.transunion.shema.response.passwordupdate.error;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlRootElement
public class ErrorDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "ErrorDetail",
            namespace = "http://www.callcredit.co.uk/Common/Base/Error/1.0")
    private ErrorDetail errorDetail;
}
