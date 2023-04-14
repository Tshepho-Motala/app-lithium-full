package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class IncompleteUser {
	private Long id;
	private String domainName;
	private String email;
	private String firstName;
	private String lastNamePrefix;
	private String lastName;
	private String cellphoneNumber;
	private String countryCode;
	private String affiliateGuid;
	private String affiliateSecondaryGuid1;
	private String affiliateSecondaryGuid2;
	private String affiliateSecondaryGuid3;
	private Date createdDate;
	private String username;
	private String stage;
	private String referrerGuid;
	private Domain domain;
	private String gender;
}
