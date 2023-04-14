package lithium.service.access.provider.sphonic.schema.kyc.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lithium.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.BooleanUtils;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SphonicKYCResponse {
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SphonicResponse {
		@JsonProperty("data")
		private ResponseData data;
	}

	@JsonProperty("SphonicResponse")
	private SphonicResponse sphonicResponse;

	@JsonIgnore
	public Boolean getAddressVerified() {
		String residencyMatch = sphonicResponse.getData().getKycResponse().getAggregateKycResult().getResult().getResidencyMatch();
		return StringUtil.isEmpty(residencyMatch) || residencyMatch.equalsIgnoreCase("none") ? null : Boolean.parseBoolean(residencyMatch);
	}

	@JsonIgnore
	public Boolean getAgeVerified() {
		String dobMatch = sphonicResponse.getData().getKycResponse().getAggregateKycResult().getResult().getDobMatch();
		return StringUtil.isEmpty(dobMatch) || dobMatch.equalsIgnoreCase("none") ? null : Boolean.parseBoolean(dobMatch);
	}

	@JsonIgnore
	public String getFinalResult() {
		return this.getSphonicResponse().getData().getResult().getFinalResult().toLowerCase();
	}

}
