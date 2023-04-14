package lithium.service.access.provider.sphonic.schema.cruks.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lithium.service.access.provider.sphonic.util.SphonicDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VendorData {
	@JsonProperty("RequestCRUKSCode")
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String requestCRUKSCode;
	@JsonProperty("ResponseCRUKSCode")
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String responseCRUKSCode;
	@JsonProperty("DebtorReference")
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String debtorReference;
	@JsonProperty("IsRegistered")
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String isRegistered;
}
