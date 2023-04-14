package lithium.service.casino.provider.betsoft.data.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.ToString;

@XmlType(propOrder = {"result", "code", "description", "userName", "firstName", "lastName", "email", "currency"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountInfoResponse extends Response {
	@XmlElement(name = "USERNAME")
	private String userName;
	@XmlElement(name = "FIRSTNAME")
	private String firstName;
	@XmlElement(name = "LASTNAME")
	private String lastName;
	@XmlElement(name = "EMAIL")
	private String email;
	@XmlElement(name = "CURRENCY")
	private String currency;
	
	protected AccountInfoResponse() {};
	
	public AccountInfoResponse(String userName, String firstName, String lastName, 
			String email, String currency) {
		super();
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.currency = currency;
	}
	
	public AccountInfoResponse(String code, String result) {
		super(code, result);
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
}