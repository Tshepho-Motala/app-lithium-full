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
@XmlRootElement(name = "CustomerInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MnetResponseData {
	@XmlElement(name = "Name")
	private String firstname;
	@XmlElement(name = "LastName")
	private String lastname;
	@XmlElement(name = "SSN")
	private String ssn;
	@XmlElement(name = "Phone")
	private String phoneNumber;
	@XmlElement(name = "Email")
	private String emailAddress;
	@XmlElement(name = "City")
	private String city;
	@XmlElement(name = "Address")
	private String address;
	@XmlElement(name = "ZipCode")
	private String zipCode;
	@XmlElement(name = "DOB")
	private DateTime birthdate;
	@XmlElement(name = "Country")
	private String country;
	@XmlElement(name = "State")
	private String state;
	@XmlElement(name = "Balance")
	private Double balance;
	@XmlElement(name="CurrencyCode")
	private String currencyCode = "USD";
}