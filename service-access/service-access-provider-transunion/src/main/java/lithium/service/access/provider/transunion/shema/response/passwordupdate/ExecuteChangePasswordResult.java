package lithium.service.access.provider.transunion.shema.response.passwordupdate;

import lithium.service.access.provider.transunion.shema.response.passwordupdate.error.Error;
import lithium.service.access.provider.transunion.shema.response.success.SystemData;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;


@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ExecuteChangePasswordResult implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "Error",
            namespace = "http://www.callcredit.co.uk/SingleAccessPointService/ISingleAccessPointAdminService/1.0")
    private Error error;

    @XmlElement(name = "SystemData",
            namespace = "http://www.callcredit.co.uk/SingleAccessPointService/ISingleAccessPointService/1.0")
    private SystemData systemData;
}
