package lithium.service.cashier.processor.neteller.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.service.cashier.processor.neteller.data.enums.Locale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class Profile {
	private Locale locale;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String mobile;
	private DateOfBirth dateOfBirth;
}
