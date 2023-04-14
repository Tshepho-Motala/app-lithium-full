package lithium.service.cashier.processor.interswitch.api.schema;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "CustomerInformationRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerInformationRequest {
    @XmlElement(name = "ServiceUrl", required = true)
    private String serviceUrl;              //Mandatory Alphanumeric
    @XmlElement(name = "ServiceUsername", nillable = true)
    private String serviceUsername;	        //Optional Alphanumeric
    @XmlElement(name = "ServicePassword", nillable = true)
    private String servicePassword;	        //Optional Alphanumeric
    @XmlElement(name = "FtpUrl", nillable = true)
    private String ftpUrl;	                //Optional Alphanumeric
    @XmlElement(name = "FtpUsername", nillable = true)
    private String ftpUsername;	            //Optional Alphanumeric
    @XmlElement(name = "FtpPassword", nillable = true)
    private String ftpPassword;	            //Optional Alphanumeric
    @XmlElement(name = "MerchantReference", required = true)
    private String merchantReference;       //Mandatory Alphanumeric
    @XmlElement(name = "CustReference", required = true)
    private String custReference;	        //Mandatory Alphanumeric
    @XmlElement(name = "PaymentItemCategoryCode", nillable = true)
    private String paymentItemCategoryCode;	//Optional Alphanumeric
    @XmlElement(name = "PaymentItemCode", nillable = true)
    private String paymentItemCode;	        //Optional Alphanumeric
    @XmlElement(name = "TerminalId", nillable = true)
    private String terminalId;	            //Optional Alphanumeric
    @XmlElement(name = "ThirdPartyCode", nillable = true)
    private String thirdPartyCode;
    @XmlElement(name = "Amount", nillable = true)
    private String amount;	                //Optional Numeric
}
