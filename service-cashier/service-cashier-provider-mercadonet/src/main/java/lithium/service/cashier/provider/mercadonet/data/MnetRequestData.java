package lithium.service.cashier.provider.mercadonet.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.ToString;

import org.joda.time.DateTime;

@Data
@ToString
@XmlRootElement(name = "Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class MnetRequestData {
	@XmlElement(name = "CustLogin")
	private String customerLogin;
	@XmlElement(name = "CustPIN")
	private String customerPin;
	@XmlElement(name = "CustPassword")
	private String customerPassword;
	@XmlElement(name="Password")
	private String password;
	@XmlElement(name = "Udf1")
	private String udf;
	@XmlElement(name = "Method")
	private String method;
	@XmlElement(name = "Amount")
	private Double amount;
	@XmlElement(name = "TransType")
	private String transactionType;
	@XmlElement(name = "CardType")
	private String cardType;
	@XmlElement(name = "TransMethod")
	private String transactionMethod;
	@XmlElement(name = "TransID")
	private Integer transactionId;
	@XmlElement(name = "TransDate")
	private DateTime transactionDate;
	@XmlElement(name = "TransNote")
	private String transactionNote;
	@XmlElement(name = "IPAddress")
	private String ipAddress;
	@XmlElement(name = "CurrencyCode")
	private String currency;
	@XmlElement(name = "BonusAccepted")
	private Boolean bonusAccepted;
	@XmlElement(name = "Bonus")
	private Double bonus;
	@XmlElement(name = "CardNumber")
	private String cardNumber;
}