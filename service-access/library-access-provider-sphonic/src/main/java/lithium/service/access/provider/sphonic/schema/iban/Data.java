package lithium.service.access.provider.sphonic.schema.iban;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lithium.service.access.provider.sphonic.util.SphonicDeserializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.Optional;

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

    public Map<String, String> toMap() {
        Map<String, String> map = vendorData.toMap();
        Optional.ofNullable(result).ifPresent(rs -> map.put("result", rs));
        return map;
    }
}
