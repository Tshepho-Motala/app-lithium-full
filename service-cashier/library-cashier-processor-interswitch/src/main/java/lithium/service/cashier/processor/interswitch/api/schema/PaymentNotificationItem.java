package lithium.service.cashier.processor.interswitch.api.schema;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentNotificationItem {

    @XmlElement(name = "ItemName")
    private String itemName;	//Mandatory	20	Alphanumeric	The name of an item that was paid for (There will always be at least one Item)
    @XmlElement(name = "ItemCode")
    private String itemCode;	//Mandatory	10	Alphanumeric	The code of an item that was paid for (There will always be at least one item)
    @XmlElement(name = "ItemAmount")
    private BigDecimal itemAmount;	//Mandatory	ANY	Numeric	The amount that was paid for the item (Will be equal to Amount if only one item was paid for). Note this would be negative for reversal payment notification.
    @XmlElement(name = "LeadBankCode")
    private String leadBankCode;	//Optional	3	String	This code identifies the bank where the collected funds will be remitted to. See Bank Code in Appendix.
    @XmlElement(name = "LeadBankCbnCode")
    private String leadBankCbnCode;	//Optional	ANY	String	This is a unique code assigned to the LeadBank (in Nigeria). See Bank CBN Code in appendix.
    @XmlElement(name = "LeadBankName")
    private String leadBankName;	//Optional	ANY	String	This is the name of the bank where the collected funds will be remitted to
    @XmlElement(name = "CategoryCode")
    private String categoryCode;	//Optional	10	Alphanumeric	The category code for payment item grouping
    @XmlElement(name = "CategoryName")
    private String categoryName;	//Optional	20	Alphanumeric	The category name for payment item grouping
    @XmlElement(name = "ItemQuantity")
    private Integer itemQuantity;	//Optional	2	Numeric	Payment item count
}
