package lithium.service.cashier.client.objects;

import java.io.Serializable;

import org.joda.time.DateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
	private static final long serialVersionUID = -4794486225082431245L;

	private String guid;

	private String firstName;
	
	private String lastName;
	
	private String ssn;
	
	private String phoneNumber;
	
	private String email;
	
	private String city;

	private String postalAddress;
	
	private String zipCode;
	
	private DateTime dateOfBirth;

	private String country; //ISO 3166-1

	private String state; //ISO 3166-2
	
	private String password;
	
	private Long balanceCents; //float balance
	
	private String currency;
	
	private String domain;
	
	private String username;

	private boolean testAccount;
}