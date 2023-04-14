package lithium.service.sms.provider.mobivate.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "deliveryreceipt")
public class MobivateWebhook implements Serializable {
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "clientReference")
    private String clientReference;
    @XmlElement(name = "status")
    private String status;
    @XmlElement(name = "deliveryMessageId")
    private String deliveryMessageId;
}
