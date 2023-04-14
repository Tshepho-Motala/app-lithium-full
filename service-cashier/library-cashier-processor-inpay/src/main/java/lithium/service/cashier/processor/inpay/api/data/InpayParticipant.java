package lithium.service.cashier.processor.inpay.api.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InpayParticipant {
	private String type;
	private String name;
	@JsonProperty("address_lines")
	private String addressLines;
	private String postcode;
	private String city;
	@JsonProperty("country_code")
	private String countryCode;
  @JsonProperty("birthdate")
  private String birthDate;
  private String email;
}
