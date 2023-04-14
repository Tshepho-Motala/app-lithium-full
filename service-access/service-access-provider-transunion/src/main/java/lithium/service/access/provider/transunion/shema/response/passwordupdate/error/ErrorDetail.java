package lithium.service.access.provider.transunion.shema.response.passwordupdate.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "ErrorCode",
            namespace = "http://www.callcredit.co.uk/Common/Base/Error/1.0")
    private String errorCode;

    @XmlElement(name = "ErrorMessage",
            namespace = "http://www.callcredit.co.uk/Common/Base/Error/1.0")
    private String errorMessage;
}
