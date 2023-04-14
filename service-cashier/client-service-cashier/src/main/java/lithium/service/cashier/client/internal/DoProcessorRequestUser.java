package lithium.service.cashier.client.internal;

import java.security.MessageDigest;
import java.util.Date;

import org.joda.time.DateTime;

import lithium.service.user.client.objects.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoProcessorRequestUser {
	private String username;
	private String domain;
	private String email;
	private String firstName;
	private String lastName;
	private String telephoneNumber;
	private String cellphoneNumber;
	private Address residentialAddress;
	private Address postalAddress;
	private String socialSecurityNumber;
	private Integer dobYear;
	private Integer dobMonth;
	private Integer dobDay;
	private String lastKnownIP;
	private String lastKnownUserAgent;
	private Date createdDate;
	private String shortGuid;
	private String realGuid;
	private String countryCode;
	private String iban;
	private String gender;
	private Long id;
	private String lastNamePrefix;
	private String os;
	private String browser;

	public DateTime getDateOfBirth() {
		if ((dobYear != null) && (dobMonth != null) && (dobDay != null)) {
			return new DateTime(dobYear, dobMonth, dobDay, 0, 0);
		}
		return null;
	}
	public String getGuid() {
		return domain + "/" + username;
	}
	private String md5Domain() {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(domain.getBytes());
			
			byte[] digest = md.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				sb.append(String.format("%02x", b & 0xff));
			}
			
			return sb.toString().toLowerCase();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public String md5Guid() {
		return md5Domain()+"/"+username;
	}
	public boolean validMd5Guid(String md5Guid) {
		if (md5Guid.equals(md5Guid())) return true;
		return false;
	}
	public String getFullName() {
		return firstName + " " + lastName;
	}
	private String currency;
	private String locale;
	private String language;
}
