package lithium.service.cashier.provider.mercadonet.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name="customer")
public class Customer implements Comparable<Customer>, Serializable {
	private static final long serialVersionUID = 282587128134879576L;
	
	private int id;
	private int siteId;
	@NotBlank(message="customer.screenname.empty")
	@Size(min=4, max=10, message="customer.screenname.size.wrong")
	private String screenname;
	@NotBlank(message="customer.signinname.empty")
	@Size(min=4, max=32, message="customer.signinname.size.wrong")
	private String signinname;
	private String siteName;
	@Size(max=32, message="customer.firstname.too.long")
	private String firstname;
	@Size(max=64, message="customer.lastname.too.long")
	private String lastname;
	@Size(max=64, message="customer.address.too.long")
	private String address;
	@Size(max=64, message="customer.apt.too.long")
	private String apt;
	@Size(max=32, message="customer.city.too.long")
	private String city;
	private String region;
	private String country;
	@Email(message="customer.email.not.valid")
	@NotBlank(message="customer.email.empty")
	@Size(max=64, message="customer.email.too.long")
	private String emailAddress;
	@Size(max=32, message="customer.phonenumber.too.long")
	@NotBlank(message="customer.phonenumber.empty")
	private String phoneNumber;
	private int status;
	@Size(min=6, max=32, message="customer.password.size.wrong")
	private String password;
	private boolean terminalPlayer;
	@Size(max=16, message="customer.postalcode.too.long")
	private String postalCode;
	@Size(max=64, message="customer.secretquestion.too.long")
	private String secretQuestion;
	@Size(max=64, message="customer.secretanswer.too.long")
	private String secretAnswer;
	private DateTime createdDate;
	@Past(message="customer.birthdate.invalid")
	private DateTime birthDate;
	
	private long balance;
	
	public String getBirthDateFormatted(String pattern) {
		return new SimpleDateFormat(pattern).format((getBirthDate() != null)?getBirthDate().toDate():new Date());
	}
	public String getBirthDateFormatted() {
		return getBirthDateFormatted("yyyy/MM/dd");
	}
	public void setBirthDateFormatted(String birthDate) throws ParseException {
		setBirthDate(new DateTime(new SimpleDateFormat("yyyy/MM/dd").parse(birthDate).getTime()));
	}
	public String getBalanceFormattedUsd() {
		return "$"+new BigDecimal(balance).movePointLeft(2)+"";
	}
	@Override
	public int compareTo(Customer anotherCustomer) {
		return this.screenname.compareToIgnoreCase(anotherCustomer.screenname);
	}
}