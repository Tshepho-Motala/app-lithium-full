package lithium.service.access.provider.transunion.shema.response.passwordupdate;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ExecuteChangePasswordResponse",
        namespace = "http://www.callcredit.co.uk/SingleAccessPointService/ISingleAccessPointAdminService/1.0")
@Data
public class ExecuteChangePasswordResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "ExecuteChangePasswordResult",
            namespace = "http://www.callcredit.co.uk/SingleAccessPointService/ISingleAccessPointAdminService/1.0")
    private ExecuteChangePasswordResult changePassResultBody;

}
