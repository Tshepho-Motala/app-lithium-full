package lithium.service.casino.provider.nucleus.data.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.ToString;

@XmlType(propOrder = {"result", "code", "description", "userId", "userName", "firstName", "lastName", "email", "currency", "balanceCents"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthenticationResponse extends Response {
	@XmlElement(name = "USERID")
	private String userId;
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
	@XmlElement(name = "BALANCE")
	private Long balanceCents;
	
	public AuthenticationResponse() {
		super();
	};
	
	public AuthenticationResponse(String userId, String userName, String firstName, String lastName, 
			String email, String currency, Long balanceCents) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.currency = currency;
		this.balanceCents = balanceCents;
	}
	
	public AuthenticationResponse(String code, String result) {
		super(code, result);
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
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
	
	public Long getBalanceCents() {
		return balanceCents;
	}

	public void setBalanceCents(Long balanceCents) {
		this.balanceCents = balanceCents;
	}
	
}
