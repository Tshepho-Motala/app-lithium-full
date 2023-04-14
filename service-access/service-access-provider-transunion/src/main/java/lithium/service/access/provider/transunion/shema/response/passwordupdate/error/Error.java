package lithium.service.access.provider.transunion.shema.response.passwordupdate.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
public class Error implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "ErrorDetails",
            namespace = "http://www.callcredit.co.uk/Common/Base/Error/1.0")
    private ErrorDetails errorDetails;

    @XmlElement(name = "Success",
            namespace = "http://www.callcredit.co.uk/Common/Base/Error/1.0")
    private boolean success;
}
