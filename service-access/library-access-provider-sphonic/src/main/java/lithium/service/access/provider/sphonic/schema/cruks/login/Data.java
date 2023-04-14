package lithium.service.access.provider.sphonic.schema.cruks.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lithium.service.access.provider.sphonic.util.SphonicDeserializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@lombok.Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {
	@JsonProperty("Result")
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String result;
	private VendorData vendorData;
}
