package lithium.service.cashier.processor.interswitch.api.schema;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name = "PaymentNotificationRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentNotificationRequest {

    @XmlElement(name = "ServiceUrl", required = true)
    private String serviceUrl;	//Mandatory	Third party platform service url
    @XmlElement(name = "ServiceUsername")
    private String serviceUsername;	//Optional	ANY	Alphanumeric	Username to access Service at Merchant’s end
    @XmlElement(name = "ServicePassword")
    private String servicePassword;	//Optional	ANY	Alphanumeric	Password to access Service at Merchant’s end
    @XmlElement(name = "FtpUrl")
    private String ftpUrl;	//Optional	ANY	Alphanumeric	Third Party platform FTP Location
    @XmlElement(name = "FtpUsername")
    private String ftpUsername; //	Optional	ANY	Alphanumeric	Username to access FTP Location at Third Party’s end
    @XmlElement(name = "FtpPassword")
    private String ftpPassword;  //	Optional	ANY	Alphanumeric	Password to access FTP Location at Third Party’s end
    @XmlElementWrapper(name = "Payments") //String Payments	Mandatory Container element for a collection of payments
    @XmlElement(name = "Payment")
    private List<Payment> paymentList;
}
