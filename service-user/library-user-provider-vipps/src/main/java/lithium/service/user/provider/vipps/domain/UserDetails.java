package lithium.service.user.provider.vipps.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserDetails implements Serializable {
	private static final long serialVersionUID = 8093264229173611280L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@OneToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "address_id")
	private Address address;
	private String bankIdVerified;
	private String dateOfBirth;
	private String email;
	private String firstName;
	private String lastName;
	private String mobileNumber;
	private String ssn;
	private String userId;
	
	public boolean isBankIdVerified() {
		if (bankIdVerified.equals("Y")) return true;
		return false;
	}
}